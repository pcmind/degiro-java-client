package cat.indiketa.degiro;

import cat.indiketa.degiro.exceptions.DInvalidCredentialsException;
import cat.indiketa.degiro.exceptions.DeGiroException;
import cat.indiketa.degiro.http.DResponse;
import cat.indiketa.degiro.http.IDCommunication;
import cat.indiketa.degiro.model.DCashMovement;
import cat.indiketa.degiro.model.DNewOrder;
import cat.indiketa.degiro.model.DOrderAction;
import cat.indiketa.degiro.model.DOrderConfirmation;
import cat.indiketa.degiro.model.DOrderHistoryRecord;
import cat.indiketa.degiro.model.DOrderTime;
import cat.indiketa.degiro.model.DOrderType;
import cat.indiketa.degiro.model.DProductDescription;
import cat.indiketa.degiro.model.DProductSearch;
import cat.indiketa.degiro.model.DProductType;
import cat.indiketa.degiro.model.DTransaction;
import cat.indiketa.degiro.session.DSession;
import cat.indiketa.degiro.utils.DCredentials;
import com.google.gson.Gson;
import org.apache.http.Header;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeGiroImplItTest {
    private List<ExpectRequestResponse> response;
    private DSession session;
    private DeGiro deGiro;

    @Test
    void loginFailed() throws DeGiroException {
        prepareResponse().setUri("/login/secure/login").setMethod("POST").andReply(400, "Invalid credentials");
        assertThrows(DInvalidCredentialsException.class, () -> deGiro.getAccountInfo());
    }

    @Test
    void getAccountInfoSuccess() throws DeGiroException {
        setupLoginResponse();
        deGiro.getAccountInfo();
    }

    @Test
    void getValidTransactions() throws DeGiroException {
        //given
        setupLoginResponse();
        prepareResponse()
                .setBase("https://trader.degiro.nl/reporting/secure/")
                .setUri("v4/transactions?orderId=&product=&fromDate=06%2F04%2F2020&toDate=07%2F04%2F2020&groupTransactionsByOrder=false&intAccount=6000001&sessionId=111111111111111111111111111111.prod_b_112_2")
                .andReply(200, "{\"data\":[{\"id\":1111111,\"productId\":222222,\"date\":\"2020-03-09T19:02:05+01:00\",\"buysell\":\"S\",\"price\":620.0000,\"quantity\":-1,\"total\":620.0000000000,\"orderTypeId\":0,\"counterParty\":\"MK\",\"transfered\":false,\"fxRate\":1.1476,\"totalInBaseCurrency\":540.23600800000000000,\"feeInBaseCurrency\":-0.50,\"totalPlusFeeInBaseCurrency\":539.73600800000000000}]}");

        //when
        final LocalDate from = LocalDate.parse("2020-04-06");
        final LocalDate to = LocalDate.parse("2020-04-07");
        final List<DTransaction> transactions1 = deGiro.getTransactions(from, to);

        //then
        assertEquals(1, transactions1.size());
        final DTransaction dTransaction = transactions1.get(0);
        assertEquals(1111111, dTransaction.getId());
        assertEquals(222222, dTransaction.getProductId());
        assertEquals(DOrderAction.SELL, dTransaction.getBuysell());
    }

    @Test
    void invalidTransactions() throws DeGiroException {
        //given
        setupLoginResponse();
        prepareResponse()
                .setBase("https://trader.degiro.nl/reporting/secure/")
                .setUri("v4/transactions?orderId=&product=&fromDate=06%2F04%2F2020&toDate=07%2F04%2F2020&groupTransactionsByOrder=false&intAccount=6000001&sessionId=111111111111111111111111111111.prod_b_112_2")
                .andReply(200, "{\"data\":\"hello\"}");

        //when
        final LocalDate from = LocalDate.parse("2020-04-06");
        final LocalDate to = LocalDate.parse("2020-04-07");
        Assertions.assertThrows(DeGiroException.class, () -> {
            deGiro.getTransactions(from, to);
        });
    }

    @Test
    void accoutOverview() throws DeGiroException {
        //given
        setupLoginResponse();
        prepareResponse()
                .setBase("https://trader.degiro.nl/reporting/secure/")
                .setUri("v6/accountoverview?fromDate=06%2F04%2F2020&toDate=07%2F04%2F2020&intAccount=6000001&sessionId=111111111111111111111111111111.prod_b_112_2")
                .andReply("{\"data\":{\"cashMovements\":[{\"date\":\"2020-04-09T12:05:53+02:00\",\"valueDate\":\"2020-04-08T23:59:59+02:00\",\"id\":346456,\"description\":" +
                        "\"Conversão do Fundo do Mercado Monetário: Vende 411,5786 @ 0,9862 EUR\",\"productId\":5173554,\"currency\":\"EUR\",\"balance\":{\"cashFund\":" +
                        "[{\"participation\":1204.653600,\"price\":0.9862,\"id\":45435345}],\"unsettledCash\":-0.0412144100,\"total\":1187.9881659100},\"type\":" +
                        "\"CASH_FUND_TRANSACTION\"},{\"date\":\"2020-04-08T00:00:00+02:00\",\"valueDate\":\"2020-04-07T00:00:00+02:00\",\"id\":0,\"description\":" +
                        "\"Alteração do preço do Fundo do Mercado Monetário (EUR)\",\"productId\":5173554,\"currency\":\"EUR\",\"change\":-0.1616232200,\"balance\":" +
                        "{\"cashFund\":[{\"participation\":1616.232200,\"price\":0.9862,\"id\":5173554}],\"unsettledCash\":-405.9400297300,\"total\":1187.9881659100}," +
                        "\"type\":\"CASH_FUND_NAV_CHANGE\"},{\"date\":\"2020-04-07T17:45:43+02:00\",\"valueDate\":\"2020-04-07T17:45:43+02:00\",\"id\":34564561," +
                        "\"orderId\":\"0334546-abf465-23523aab-2345\",\"description\":\"Crédito de divisa\",\"productId\":16600513,\"currency\":\"USD\",\"change\":441.00," +
                        "\"balance\":{\"unsettledCash\":0.0019000000,\"total\":0.0019000000},\"exchangeRate\":1.0880,\"type\":\"CASH_TRANSACTION\"},{\"date\":" +
                        "\"2020-04-07T17:45:43+02:00\",\"valueDate\":\"2020-04-07T17:45:43+02:00\",\"id\":27777222,\"orderId\":\"0334546-abf465-23523aab-2345\"," +
                        "\"description\":\"Compra 30 VIRGIN GALACTIC HOLDINGS INC. COMMON STOCK@14,7 USD (US92766K1060)\",\"productId\":16600513,\"currency\":\"USD\"," +
                        "\"change\":-441.0000000000,\"balance\":{\"unsettledCash\":-440.9981000000,\"total\":-440.9981000000},\"type\":\"TRANSACTION\"}]}}");

        //when
        final LocalDate from = LocalDate.parse("2020-04-06");
        final LocalDate to = LocalDate.parse("2020-04-07");
        final List<DCashMovement> transactions1 = deGiro.getAccountOverview(from, to);

        //then
        assertEquals(4, transactions1.size());
        final DCashMovement dTransaction = transactions1.get(0);
        assertFalse(dTransaction.isInvalid());
        assertEquals(OffsetDateTime.parse("2020-04-09T12:05:53+02:00"), dTransaction.getDate());
        assertEquals(OffsetDateTime.parse("2020-04-08T23:59:59+02:00"), dTransaction.getValueDate());
        assertEquals(346456, dTransaction.getId());
        assertEquals(5173554, dTransaction.getProductId());
        assertNotNull(dTransaction.getBalance());
    }

    @Test
    void getValidOrderHistory() throws DeGiroException {
        //given
        setupLoginResponse();
        prepareResponse()
                .setBase("https://trader.degiro.nl/reporting/secure/")
                .setUri("v4/order-history?fromDate=06%2F04%2F2020&toDate=07%2F04%2F2020&intAccount=6000001&sessionId=111111111111111111111111111111.prod_b_112_2")
                .andReply(200, "{\"data\":[{\"created\":\"2020-03-30T14:04:54+01:00\",\"orderId\":\"2b1b2b3d-8f02-41d3-984e-f22ac90bda2b\",\"productId\":181116,\"size\":240.0000,\"price\":2.0100,\"buysell\":\"S\"," +
                        "\"orderTypeId\":0,\"orderTimeTypeId\":1,\"stopPrice\":0.0000,\"currentTradedSize\":0,\"totalTradedSize\":0,\"type\":\"CREATE\",\"status\":\"CONFIRMED\",\"last\":\"2020-03-30T14:05:54+02:00\",\"isActive\":false}," +
                        "{\"created\":\"2020-03-30T14:05:30+02:00\",\"orderId\":\"2b4b1b2d-8f02-42d3-934e-f12ac30bca2b\",\"productId\":111116,\"size\":240.0000,\"price\":2.0100,\"buysell\":\"S\"," +
                        "\"orderTypeId\":0,\"orderTimeTypeId\":1,\"stopPrice\":0.0000,\"currentTradedSize\":0,\"totalTradedSize\":0,\"type\":\"DELETE\",\"status\":\"CONFIRMED\",\"last\":\"2020-03-30T14:04:30+02:00\",\"isActive\":false}]}");

        //when
        final LocalDate from = LocalDate.parse("2020-04-06");
        final LocalDate to = LocalDate.parse("2020-04-07");
        final List<DOrderHistoryRecord> transactions1 = deGiro.getOrdersHistory(from, to);

        //then
        assertEquals(2, transactions1.size());
        final DOrderHistoryRecord dTransaction = transactions1.get(0);
        assertEquals("2b1b2b3d-8f02-41d3-984e-f22ac90bda2b", dTransaction.getOrderId());
        assertEquals(181116, dTransaction.getProductId());
        assertEquals(DOrderAction.SELL, dTransaction.getBuysell());
        assertEquals(DOrderTime.DAY, dTransaction.getOrderTimeTypeId());
        assertEquals(DOrderType.LIMITED, dTransaction.getOrderTypeId());
        assertEquals(DOrderHistoryRecord.Status.CONFIRMED, dTransaction.getStatus());
        assertEquals(DOrderHistoryRecord.Type.CREATE, dTransaction.getType());
    }

    @Test
    void checkOrderFail() throws DeGiroException {
        //given
        setupLoginResponse();
        prepareResponse()
                .setBase("https://trader.degiro.nl/trading/secure/")
                .setUri("v5/checkOrder;jsessionid=111111111111111111111111111111.prod_b_112_2?intAccount=6000001&sessionId=111111111111111111111111111111.prod_b_112_2")
                .setMethod("POST")
                .setData(d -> new Gson().toJson(d).equals("{\"orderType\":0,\"buySell\":\"BUY\",\"productId\":\"16600513\",\"size\":15,\"price\":\"18.26\",\"timeType\":1}"))
                .andReply(400, "{\"errors\":[{\"text\":\"order.error.scheduledDownTime\"}]}");


        //then/when
        final DNewOrder dNewOrder = new DNewOrder(DOrderAction.BUY, DOrderType.LIMITED, DOrderTime.DAY, "16600513", 15, new BigDecimal("18.26"), null);
        assertThrows(DeGiroException.class, () -> deGiro.checkOrder(dNewOrder));

    }

    @Test
    void validSearch() throws DeGiroException {
        //given
        setupLoginResponse();
        prepareResponse()
                .setBase("https://trader.degiro.nl/product_search/secure/")
                .setUri("v5/products/lookup?intAccount=6000001&sessionId=111111111111111111111111111111.prod_b_112_2&searchText=VIAC&productTypeId=1&limit=1&offset=0")
                .setMethod("GET")
                .andReply(200, "{\"offset\": 0,\"products\": [{\"id\": \"d-129867\",\"name\": \"VIACOMCBS INC.B   DL-,001\",\"isin\": \"US92553P2011\"," +
                        "\"symbol\": \"VCX\",\"contractSize\": 1.000000,\"productType\": \"STOCK\",\"productTypeId\": 1,\"tradable\": true,\"category\": \"D\"," +
                        "\"currency\": \"EUR\",\"exchangeId\": \"195\",\"orderTimeTypes\": [\"DAY\",\"GTC\"],\"gtcAllowed\": true," +
                        "\"buyOrderTypes\": [\"LIMIT\",\"MARKET\",\"STOPLOSS\",\"STOPLIMIT\",\"TRAILINGSTOP\"]," +
                        "\"sellOrderTypes\": [\"LIMIT\",\"MARKET\",\"STOPLOSS\",\"STOPLIMIT\",\"TRAILINGSTOP\"],\"marketAllowed\": true," +
                        "\"limitHitOrderAllowed\": false,\"stoplossAllowed\": true,\"stopLimitOrderAllowed\": true,\"joinOrderAllowed\": false," +
                        "\"trailingStopOrderAllowed\": true,\"combinedOrderAllowed\": false,\"sellAmountAllowed\": false,\"isFund\": false," +
                        "\"feedQuality\": \"R\",\"orderBookDepth\": 0,\"vwdIdentifierType\": \"issueid\",\"vwdId\": \"150014099\"," +
                        "\"qualitySwitchable\": false,\"qualitySwitchFree\": false,\"vwdModuleId\": 25}]}");
        //when
        final DProductSearch us92553P2011 = deGiro.searchProducts("VIAC", DProductType.SHARES, 1, 0);
        //then
        assertEquals(1, us92553P2011.getProducts().size());
        final DProductDescription prod = us92553P2011.getProducts().get(0);
        assertEquals("VCX", prod.getSymbol());
        assertEquals(5, prod.getBuyOrderTypes().size());
    }

    @Test
    void emptySearch() throws DeGiroException {
        //given
        setupLoginResponse();
        prepareResponse()
                .setBase("https://trader.degiro.nl/product_search/secure/")
                .setUri("v5/products/lookup?intAccount=6000001&sessionId=111111111111111111111111111111.prod_b_112_2&searchText=VIAC&productTypeId=1&limit=1&offset=0")
                .setMethod("GET")
                .andReply(200, "{\"offset\":0}");
        //when
        final DProductSearch search = deGiro.searchProducts("VIAC", DProductType.SHARES, 1, 0);
        //then
        assertEquals(0, search.getProducts().size());
    }

    @Test
    void marketOrder() throws DeGiroException {
        //given
        setupLoginResponse();
        prepareResponse()
                .setUri("v5/checkOrder;jsessionid=111111111111111111111111111111.prod_b_112_2?intAccount=6000001&sessionId=111111111111111111111111111111.prod_b_112_2")
                .setMethod("POST")
                .setData(data -> new Gson().toJson(data).equals("{\"orderType\":2,\"buySell\":\"SELL\",\"productId\":\"11112222\",\"size\":1,\"timeType\":1}"))
                .andReply(200, "{\"data\":{\"confirmationId\":\"11caa4dd-c1f2-4c0a-b1c2-e6f21c04a4be\",\"transactionFees\":[{\"id\":2,\"amount\":0.04,\"currency\":\"USD\"},{\"id\":3,\"amount\":0.50,\"currency\":\"EUR\"}]}}");
        prepareResponse()
                .setUri("v5/checkOrder;jsessionid=111111111111111111111111111111.prod_b_112_2?intAccount=6000001&sessionId=111111111111111111111111111111.prod_b_112_2")
                .setMethod("POST")
                .setData(data -> new Gson().toJson(data).equals("{\"orderType\":2,\"buySell\":\"SELL\",\"productId\":\"999999\",\"size\":1,\"timeType\":1}"))
                .andReply(300, "");
        //when/then
        //this should fail because of return code 300
        assertThrows(DeGiroException.class, () ->  deGiro.checkOrder(new DNewOrder(DOrderAction.SELL,
                DOrderType.MARKET_ORDER,
                DOrderTime.DAY,
                "999999",
                1,
                null,
                null)));
        //when/then
        final DOrderConfirmation dOrderConfirmation = deGiro.checkOrder(new DNewOrder(DOrderAction.SELL,
                DOrderType.MARKET_ORDER,
                DOrderTime.DAY,
                "11112222",
                1,
                null,
                null));
        assertEquals("11caa4dd-c1f2-4c0a-b1c2-e6f21c04a4be", dOrderConfirmation.getConfirmationId());

    }

    private void setupLoginResponse() {
        prepareSuccessLogin();
        prepareSuccessSecureConfig();
        prepareSuccessSecureAccount();
        prepareSuccessAccountInfo();
    }

    private void prepareSuccessAccountInfo() {
        prepareResponse().setBase("https://trader.degiro.nl/trading/secure/").setUri("v5/account/info/6000001;jsessionid=111111111111111111111111111111.prod_b_112_2").setMethod("GET").andReply("{\n" +
                "        \"data\": {\n" +
                "          \"clientId\": 7771111,\n" +
                "          \"baseCurrency\": \"EUR\",\n" +
                "          \"currencyPairs\": {\n" +
                "            \"HRKEUR\": {\n" +
                "              \"id\": -1,\n" +
                "              \"price\": \"0.1345\"\n" +
                "            },\n" +
                "            \"USDRUB\": {\n" +
                "              \"id\": 1331862,\n" +
                "              \"price\": \"57.7546\"\n" +
                "            },\n" +
                "            \"EURNZD\": {\n" +
                "              \"id\": 1316471,\n" +
                "              \"price\": \"1.6978\"\n" +
                "            },\n" +
                "            \"EURCHF\": {\n" +
                "              \"id\": 714322,\n" +
                "              \"price\": \"1.0955\"\n" +
                "            },\n" +
                "            \"USDHUF\": {\n" +
                "              \"id\": 5292057,\n" +
                "              \"price\": \"298.9300\"\n" +
                "            },\n" +
                "            \"USDJPY\": {\n" +
                "              \"id\": 9420825,\n" +
                "              \"price\": \"108.8500\"\n" +
                "            },\n" +
                "            \"USDPLN\": {\n" +
                "              \"id\": 5194532,\n" +
                "              \"price\": \"3.8642\"\n" +
                "            },\n" +
                "            \"CLPEUR\": {\n" +
                "              \"id\": -1,\n" +
                "              \"price\": \"0.001141\"\n" +
                "            },\n" +
                "            \"EURESP\": {\n" +
                "              \"id\": -2,\n" +
                "              \"price\": \"166.3860\"\n" +
                "            },\n" +
                "            \"EURBEF\": {\n" +
                "              \"id\": -2,\n" +
                "              \"price\": \"40.3399\"\n" +
                "            },\n" +
                "            \"EURUSD\": {\n" +
                "              \"id\": 705366,\n" +
                "              \"price\": \"1.1079\"\n" +
                "            },\n" +
                "            \"EURDEM\": {\n" +
                "              \"id\": -2,\n" +
                "              \"price\": \"1.9558\"\n" +
                "            },\n" +
                "            \"USDINR\": {\n" +
                "              \"id\": 1788548,\n" +
                "              \"price\": \"61.0050\"\n" +
                "            },\n" +
                "            \"EURSGD\": {\n" +
                "              \"id\": 1316473,\n" +
                "              \"price\": \"1.5103\"\n" +
                "            },\n" +
                "            \"GBPUSD\": {\n" +
                "              \"id\": 1788982,\n" +
                "              \"price\": \"1.3104\"\n" +
                "            },\n" +
                "            \"TWDEUR\": {\n" +
                "              \"id\": -1,\n" +
                "              \"price\": \"0.02961\"\n" +
                "            },\n" +
                "            \"USCEUR\": {\n" +
                "              \"id\": -1,\n" +
                "              \"price\": \"0.009026\"\n" +
                "            },\n" +
                "            \"EUCEUR\": {\n" +
                "              \"id\": -1,\n" +
                "              \"price\": \"0.01000\"\n" +
                "            },\n" +
                "            \"KESEUR\": {\n" +
                "              \"id\": -1,\n" +
                "              \"price\": \"0.008800\"\n" +
                "            },\n" +
                "            \"GBPSEK\": {\n" +
                "              \"id\": 1396740,\n" +
                "              \"price\": \"12.4685\"\n" +
                "            },\n" +
                "            \"EURTRY\": {\n" +
                "              \"id\": 1316434,\n" +
                "              \"price\": \"6.3735\"\n" +
                "            },\n" +
                "            \"LTLEUR\": {\n" +
                "              \"id\": -1,\n" +
                "              \"price\": \"0.2896\"\n" +
                "            },\n" +
                "            \"EURHKD\": {\n" +
                "              \"id\": 1526689,\n" +
                "              \"price\": \"8.6730\"\n" +
                "            },\n" +
                "            \"USDSEK\": {\n" +
                "              \"id\": 1854033,\n" +
                "              \"price\": \"9.5143\"\n" +
                "            },\n" +
                "            \"GBPJPY\": {\n" +
                "              \"id\": 12153968,\n" +
                "              \"price\": \"142.6430\"\n" +
                "            },\n" +
                "            \"GBPPLN\": {\n" +
                "              \"id\": 1366267,\n" +
                "              \"price\": \"5.0254\"\n" +
                "            },\n" +
                "            \"EURMXN\": {\n" +
                "              \"id\": 4974639,\n" +
                "              \"price\": \"21.5517\"\n" +
                "            },\n" +
                "            \"USDCZK\": {\n" +
                "              \"id\": 5194528,\n" +
                "              \"price\": \"23.0533\"\n" +
                "            },\n" +
                "            \"ARSEUR\": {\n" +
                "              \"id\": -1,\n" +
                "              \"price\": \"0.01510\"\n" +
                "            },\n" +
                "            \"INREUR\": {\n" +
                "              \"id\": -1,\n" +
                "              \"price\": \"0.01260\"\n" +
                "            },\n" +
                "            \"EGPEUR\": {\n" +
                "              \"id\": -1,\n" +
                "              \"price\": \"0.05600\"\n" +
                "            },\n" +
                "            \"USDNOK\": {\n" +
                "              \"id\": 1854031,\n" +
                "              \"price\": \"9.1758\"\n" +
                "            },\n" +
                "            \"RONEUR\": {\n" +
                "              \"id\": -1,\n" +
                "              \"price\": \"0.2093\"\n" +
                "            },\n" +
                "            \"EURZAR\": {\n" +
                "              \"id\": 7095468,\n" +
                "              \"price\": \"16.1812\"\n" +
                "            },\n" +
                "            \"ILAEUR\": {\n" +
                "              \"id\": -1,\n" +
                "              \"price\": \"0.002597\"\n" +
                "            },\n" +
                "            \"EURAUD\": {\n" +
                "              \"id\": 1366262,\n" +
                "              \"price\": \"1.6181\"\n" +
                "            },\n" +
                "            \"VNDEUR\": {\n" +
                "              \"id\": -1,\n" +
                "              \"price\": \"0.03890\"\n" +
                "            },\n" +
                "            \"USDCAD\": {\n" +
                "              \"id\": 5466016,\n" +
                "              \"price\": \"1.3200\"\n" +
                "            },\n" +
                "            \"GBPDKK\": {\n" +
                "              \"id\": 12153808,\n" +
                "              \"price\": \"8.8383\"\n" +
                "            },\n" +
                "            \"EURCZK\": {\n" +
                "              \"id\": 1366266,\n" +
                "              \"price\": \"25.5754\"\n" +
                "            },\n" +
                "            \"NZDUSD\": {\n" +
                "              \"id\": 9420831,\n" +
                "              \"price\": \"0.6524\"\n" +
                "            },\n" +
                "            \"USDDKK\": {\n" +
                "              \"id\": 1854032,\n" +
                "              \"price\": \"6.7443\"\n" +
                "            },\n" +
                "            \"EURSEK\": {\n" +
                "              \"id\": 841087,\n" +
                "              \"price\": \"10.5374\"\n" +
                "            },\n" +
                "            \"EURGRD\": {\n" +
                "              \"id\": -2,\n" +
                "              \"price\": \"340.7500\"\n" +
                "            },\n" +
                "            \"DKKNOK\": {\n" +
                "              \"id\": 12182889,\n" +
                "              \"price\": \"1.3115\"\n" +
                "            },\n" +
                "            \"EURPTE\": {\n" +
                "              \"id\": -2,\n" +
                "              \"price\": \"200.4820\"\n" +
                "            },\n" +
                "            \"MYREUR\": {\n" +
                "              \"id\": -1,\n" +
                "              \"price\": \"0.2162\"\n" +
                "            },\n" +
                "            \"EURDKK\": {\n" +
                "              \"id\": 714325,\n" +
                "              \"price\": \"7.4738\"\n" +
                "            },\n" +
                "            \"GBPCZK\": {\n" +
                "              \"id\": 12153965,\n" +
                "              \"price\": \"30.2143\"\n" +
                "            },\n" +
                "            \"AEDEUR\": {\n" +
                "              \"id\": -1,\n" +
                "              \"price\": \"0.2457\"\n" +
                "            },\n" +
                "            \"PHPEUR\": {\n" +
                "              \"id\": -1,\n" +
                "              \"price\": \"0.01770\"\n" +
                "            },\n" +
                "            \"GBXEUR\": {\n" +
                "              \"id\": -1,\n" +
                "              \"price\": \"0.01183\"\n" +
                "            },\n" +
                "            \"EURCAD\": {\n" +
                "              \"id\": 841089,\n" +
                "              \"price\": \"1.4626\"\n" +
                "            },\n" +
                "            \"DKKSEK\": {\n" +
                "              \"id\": 10349045,\n" +
                "              \"price\": \"1.4103\"\n" +
                "            },\n" +
                "            \"USDZAR\": {\n" +
                "              \"id\": 1860946,\n" +
                "              \"price\": \"14.6033\"\n" +
                "            },\n" +
                "            \"NOKSEK\": {\n" +
                "              \"id\": 10350270,\n" +
                "              \"price\": \"1.0369\"\n" +
                "            },\n" +
                "            \"GBPNOK\": {\n" +
                "              \"id\": 12153970,\n" +
                "              \"price\": \"12.0250\"\n" +
                "            },\n" +
                "            \"EURNOK\": {\n" +
                "              \"id\": 841091,\n" +
                "              \"price\": \"10.1626\"\n" +
                "            },\n" +
                "            \"CHFPLN\": {\n" +
                "              \"id\": 11839953,\n" +
                "              \"price\": \"3.9085\"\n" +
                "            },\n" +
                "            \"CNYEUR\": {\n" +
                "              \"id\": -1,\n" +
                "              \"price\": \"0.1279\"\n" +
                "            },\n" +
                "            \"IDREUR\": {\n" +
                "              \"id\": -1,\n" +
                "              \"price\": \"0.00006390\"\n" +
                "            },\n" +
                "            \"USDMXN\": {\n" +
                "              \"id\": 9420829,\n" +
                "              \"price\": \"19.4367\"\n" +
                "            },\n" +
                "            \"BRLEUR\": {\n" +
                "              \"id\": -1,\n" +
                "              \"price\": \"0.2145\"\n" +
                "            },\n" +
                "            \"EURGBP\": {\n" +
                "              \"id\": 714324,\n" +
                "              \"price\": \"0.8455\"\n" +
                "            },\n" +
                "            \"ZACEUR\": {\n" +
                "              \"id\": -1,\n" +
                "              \"price\": \"0.0006180\"\n" +
                "            },\n" +
                "            \"EURPLN\": {\n" +
                "              \"id\": 1316435,\n" +
                "              \"price\": \"4.2790\"\n" +
                "            },\n" +
                "            \"GBPCHF\": {\n" +
                "              \"id\": 11839929,\n" +
                "              \"price\": \"1.2955\"\n" +
                "            },\n" +
                "            \"EURJPY\": {\n" +
                "              \"id\": 1316472,\n" +
                "              \"price\": \"120.5836\"\n" +
                "            },\n" +
                "            \"EURXXX\": {\n" +
                "              \"id\": -2,\n" +
                "              \"price\": \"1.0000\"\n" +
                "            },\n" +
                "            \"CHFNOK\": {\n" +
                "              \"id\": 11839949,\n" +
                "              \"price\": \"9.2803\"\n" +
                "            },\n" +
                "            \"EURHUF\": {\n" +
                "              \"id\": 5281657,\n" +
                "              \"price\": \"331.1258\"\n" +
                "            },\n" +
                "            \"USDCHF\": {\n" +
                "              \"id\": 714321,\n" +
                "              \"price\": \"0.9886\"\n" +
                "            },\n" +
                "            \"ISKEUR\": {\n" +
                "              \"id\": -1,\n" +
                "              \"price\": \"0.7429\"\n" +
                "            },\n" +
                "            \"NGNEUR\": {\n" +
                "              \"id\": -1,\n" +
                "              \"price\": \"0.2490\"\n" +
                "            },\n" +
                "            \"MADEUR\": {\n" +
                "              \"id\": -1,\n" +
                "              \"price\": \"0.09380\"\n" +
                "            },\n" +
                "            \"CHFDKK\": {\n" +
                "              \"id\": 11839952,\n" +
                "              \"price\": \"6.8206\"\n" +
                "            },\n" +
                "            \"AUDUSD\": {\n" +
                "              \"id\": 5466017,\n" +
                "              \"price\": \"0.6848\"\n" +
                "            },\n" +
                "            \"LVLEUR\": {\n" +
                "              \"id\": -1,\n" +
                "              \"price\": \"1.4229\"\n" +
                "            },\n" +
                "            \"EURRUB\": {\n" +
                "              \"id\": 1331861,\n" +
                "              \"price\": \"70.9220\"\n" +
                "            },\n" +
                "            \"CHFSEK\": {\n" +
                "              \"id\": 11839942,\n" +
                "              \"price\": \"9.6227\"\n" +
                "            },\n" +
                "            \"EURNLG\": {\n" +
                "              \"id\": -2,\n" +
                "              \"price\": \"2.2037\"\n" +
                "            },\n" +
                "            \"ILSEUR\": {\n" +
                "              \"id\": -1,\n" +
                "              \"price\": \"0.2597\"\n" +
                "            },\n" +
                "            \"KRWEUR\": {\n" +
                "              \"id\": -1,\n" +
                "              \"price\": \"0.0007555\"\n" +
                "            },\n" +
                "            \"BGNEUR\": {\n" +
                "              \"id\": -1,\n" +
                "              \"price\": \"0.5114\"\n" +
                "            },\n" +
                "            \"THBEUR\": {\n" +
                "              \"id\": -1,\n" +
                "              \"price\": \"0.02980\"\n" +
                "            },\n" +
                "            \"ROLEUR\": {\n" +
                "              \"id\": -1,\n" +
                "              \"price\": \"0.0002093\"\n" +
                "            }\n" +
                "          },\n" +
                "          \"marginType\": \"SPENDING_LIMIT\",\n" +
                "          \"cashFunds\": {\n" +
                "            \"CHF\": [\n" +
                "              {\n" +
                "                \"id\": 210,\n" +
                "                \"name\": \"FundShare Ucits CHF Cash Fund\",\n" +
                "                \"productIds\": [\n" +
                "                  4667925\n" +
                "                ]\n" +
                "              }\n" +
                "            ],\n" +
                "            \"PLN\": [\n" +
                "              {\n" +
                "                \"id\": 269,\n" +
                "                \"name\": \"FundShare Ucits PLN Cash Fund\",\n" +
                "                \"productIds\": [\n" +
                "                  5199327\n" +
                "                ]\n" +
                "              }\n" +
                "            ],\n" +
                "            \"EUR\": [\n" +
                "              {\n" +
                "                \"id\": 266,\n" +
                "                \"name\": \"FundShare Ucits EUR Cash Fund\",\n" +
                "                \"productIds\": [\n" +
                "                  5173554\n" +
                "                ]\n" +
                "              }\n" +
                "            ],\n" +
                "            \"GBP\": [\n" +
                "              {\n" +
                "                \"id\": 288,\n" +
                "                \"name\": \"FundShare Ucits GBP Cash Fund\",\n" +
                "                \"productIds\": [\n" +
                "                  5466008\n" +
                "                ]\n" +
                "              }\n" +
                "            ],\n" +
                "            \"DKK\": [\n" +
                "              {\n" +
                "                \"id\": 287,\n" +
                "                \"name\": \"FundShare Ucits DKK Cash Fund\",\n" +
                "                \"productIds\": [\n" +
                "                  5466007\n" +
                "                ]\n" +
                "              }\n" +
                "            ],\n" +
                "            \"CZK\": [\n" +
                "              {\n" +
                "                \"id\": 268,\n" +
                "                \"name\": \"FundShare Ucits CZK Cash Fund\",\n" +
                "                \"productIds\": [\n" +
                "                  5199326\n" +
                "                ]\n" +
                "              }\n" +
                "            ],\n" +
                "            \"USD\": [\n" +
                "              {\n" +
                "                \"id\": 209,\n" +
                "                \"name\": \"FundShare Ucits USD Cash Fund\",\n" +
                "                \"productIds\": [\n" +
                "                  4667924\n" +
                "                ]\n" +
                "              }\n" +
                "            ],\n" +
                "            \"SEK\": [\n" +
                "              {\n" +
                "                \"id\": 285,\n" +
                "                \"name\": \"FundShare Ucits SEK Cash Fund\",\n" +
                "                \"productIds\": [\n" +
                "                  5466005\n" +
                "                ]\n" +
                "              }\n" +
                "            ],\n" +
                "            \"HUF\": [\n" +
                "              {\n" +
                "                \"id\": 271,\n" +
                "                \"name\": \"FundShare Ucits HUF Cash Fund\",\n" +
                "                \"productIds\": [\n" +
                "                  5286155\n" +
                "                ]\n" +
                "              }\n" +
                "            ],\n" +
                "            \"NOK\": [\n" +
                "              {\n" +
                "                \"id\": 286,\n" +
                "                \"name\": \"FundShare Ucits NOK Cash Fund\",\n" +
                "                \"productIds\": [\n" +
                "                  5466006\n" +
                "                ]\n" +
                "              }\n" +
                "            ]\n" +
                "          }\n" +
                "        }\n" +
                "      }");
    }

    private void prepareSuccessSecureAccount() {
        prepareResponse().setBase("https://trader.degiro.nl/pa/secure/").setUri("client?sessionId=111111111111111111111111111111.prod_b_112_2").setMethod("GET").andReply("{" +
                "\"data\": {" +
                "\"id\": 55555666," +
                "\"intAccount\": 6000001," +
                "\"clientRole\": \"basic\"," +
                "\"effectiveClientRole\": \"basic\"," +
                "\"contractType\": \"PRIVATE\"," +
                "\"username\": \"degirouser\"," +
                "\"displayName\": \"client name here\"," +
                "\"email\": \"client.mail.here@gmail.com\"," +
                "\"firstContact\": {" +
                "\"firstName\": \"First Name\"," +
                "\"lastName\": \"Last Name\"," +
                "\"displayName\": \"First Name Last Name\"," +
                "\"nationality\": \"PT\"," +
                "\"gender\": \"MALE\"," +
                "\"dateOfBirth\": \"1900-02-01\"," +
                "\"placeOfBirth\": \"Portugal\"," +
                "\"countryOfBirth\": \"PT\"" +
                "}," +
                "\"address\": {" +
                "\"streetAddress\": \"Rua xxxx\"," +
                "\"streetAddressNumber\": \"12345\"," +
                "\"streetAddressExt\": \"1\"," +
                "\"zip\": \"1111-11\"," +
                "\"city\": \"LISBOA\"," +
                "\"country\": \"PT\"" +
                "}," +
                "\"cellphoneNumber\": \"+351999999999\"," +
                "\"locale\": \"pt_PT\"," +
                "\"language\": \"pt\"," +
                "\"culture\": \"PT\"," +
                "\"bankAccount\": {" +
                "\"bankAccountId\": 6767676," +
                "\"bic\": \"00000000\"," +
                "\"iban\": \"1111111111111111111111\"," +
                "\"status\": \"VERIFIED\"" +
                "}," +
                "\"memberCode\": \"111EAA3F\"," +
                "\"isWithdrawalAvailable\": true," +
                "\"isAllocationAvailable\": false," +
                "\"isIskClient\": false," +
                "\"isCollectivePortfolio\": false," +
                "\"isAmClientActive\": false," +
                "\"canUpgrade\": true" +
                "}" +
                "}");
    }

    private void prepareSuccessSecureConfig() {
        prepareResponse().setUri("/login/secure/config").setMethod("GET").andReply(200, "{\"data\":{" +
                "\"tradingUrl\": \"https://trader.degiro.nl/trading/secure/\"," +
                "\"paUrl\": \"https://trader.degiro.nl/pa/secure/\"," +
                "\"reportingUrl\": \"https://trader.degiro.nl/reporting/secure/\"," +
                "\"paymentServiceUrl\": \"https://trader.degiro.nl/payments/\"," +
                "\"productSearchUrl\": \"https://trader.degiro.nl/product_search/secure/\"," +
                "\"dictionaryUrl\": \"https://trader.degiro.nl/product_search/config/dictionary/\"," +
                "\"productTypesUrl\": \"https://trader.degiro.nl/product_search/config/productTypes/\"," +
                "\"companiesServiceUrl\": \"https://trader.degiro.nl/dgtbxdsservice/\"," +
                "\"i18nUrl\": \"https://trader.degiro.nl/i18n/\"," +
                "\"vwdQuotecastServiceUrl\": \"https://trader.degiro.nl/vwd-quotecast-service/\"," +
                "\"vwdNewsUrl\": \"https://solutions.vwdservices.com/customers/degiro.nl/news-feed/api/\"," +
                "\"vwdGossipsUrl\": \"https://solutions.vwdservices.com/customers/degiro.nl/news-feed/api/\"," +
                "\"firstLoginWizardUrl\": \"https://trader.degiro.nl/firstloginwizard/secure/\"," +
                "\"taskManagerUrl\": \"https://trader.degiro.nl/taskmanager/\"," +
                "\"landingPath\": \"/trader/\"," +
                "\"betaLandingPath\": \"/trader4/\"," +
                "\"mobileLandingPath\": \"/mobile/\"," +
                "\"loginUrl\": \"https://trader.degiro.nl/login/pt\"," +
                "\"sessionId\": \"111111111111111111111111111111.prod_b_112_2\"," +
                "\"clientId\": 123456789" +
                "}}");
    }

    private void prepareSuccessLogin() {
        prepareResponse()
                .setUri("/login/secure/login")
                .setMethod("POST")
                .andReply(200, "ok", () -> session.getCookies().add(new BasicClientCookie("JSESSIONID", "111111111111111111111111111111.prod_b_112_2")));
    }

    @BeforeEach
    void setUp() {
        response = new ArrayList<>();
        session = new DSession();
        session.setCookies(new ArrayList<>());
        deGiro = new DeGiroImpl(
                DeGiroHost.defaultEndPoint(),
                new DCredentials() {
                    @Override
                    public String getUsername() {
                        return "aa";
                    }

                    @Override
                    public String getPassword() {
                        return "bb";
                    }
                },
                session,
                (base, uri, data, headers, method) -> {
                    if (method == null) {
                        if (data == null) {
                            method = "GET";
                        } else {
                            method = "POST";
                        }
                    }
                    for (ExpectRequestResponse response1 : response) {
                        final DResponse urlData = response1.getUrlData(base, uri, data, headers, method);
                        if (urlData != null) {
                            return urlData;
                        }
                    }
                    final String s = data == null ? "null" : new Gson().toJson(data);
                    throw new RuntimeException("Unknow request base=" + base + ", uri:" + uri + ", data: "+ s +", headers:" + headers + ", method:" + method);
                }
        );
        prepareResponse().setBase("https://trader.degiro.nl").setUri("/login/secure/config ").setMethod("GET").andReply("{" +
                "tradingUrl: \"https://trader.degiro.nl/trading/secure/\"," +
                "paUrl: \"https://trader.degiro.nl/pa/secure/\"," +
                "reportingUrl: \"https://trader.degiro.nl/reporting/secure/\"," +
                "paymentServiceUrl: \"https://trader.degiro.nl/payments/\"," +
                "productSearchUrl: \"https://trader.degiro.nl/product_search/secure/\"," +
                "dictionaryUrl: \"https://trader.degiro.nl/product_search/config/dictionary/\"," +
                "productTypesUrl: \"https://trader.degiro.nl/product_search/config/productTypes/\"," +
                "companiesServiceUrl: \"https://trader.degiro.nl/dgtbxdsservice/\"," +
                "i18nUrl: \"https://trader.degiro.nl/i18n/\"," +
                "vwdQuotecastServiceUrl: \"https://trader.degiro.nl/vwd-quotecast-service/\"," +
                "vwdNewsUrl: \"https://solutions.vwdservices.com/customers/degiro.nl/news-feed/api/\"," +
                "vwdGossipsUrl: \"https://solutions.vwdservices.com/customers/degiro.nl/news-feed/api/\"," +
                "firstLoginWizardUrl: \"https://trader.degiro.nl/firstloginwizard/secure/\"," +
                "taskManagerUrl: \"https://trader.degiro.nl/taskmanager/\"," +
                "landingPath: \"/trader/\"," +
                "betaLandingPath: \"/trader4/\"," +
                "mobileLandingPath: \"/mobile/\"," +
                "loginUrl: \"https://trader.degiro.nl/login/pt\"," +
                "sessionId: \"34016CD5B74FECF42D93521A30F61548.prod_b_112_2\"," +
                "clientId: 629210" +
                "}");
    }


    public ExpectRequestResponse prepareResponse() {
        final ExpectRequestResponse expectRequestResponse = new ExpectRequestResponse();
        response.add(expectRequestResponse);
        return expectRequestResponse;
    }

    private static class ExpectRequestResponse implements IDCommunication {
        private Predicate<String> base = b -> true;
        private Predicate<String> uri = b -> true;
        private Predicate<Object> data = b -> true;
        private Predicate<String> method = b -> true;
        private Predicate<List<Header>> headers = b -> true;
        private BiFunction<String, String, DResponse> supplier = (url, method) -> new DResponse(401, url, method, "");

        public static ExpectRequestResponse create() {
            return new ExpectRequestResponse();
        }

        public ExpectRequestResponse setBase(String base) {
            this.base = t -> t.equals(base);
            return this;
        }

        public ExpectRequestResponse setUri(String uri) {
            this.uri = t -> t.equals(uri);
            return this;
        }

        public ExpectRequestResponse setData(Predicate<Object> data) {
            this.data = data;
            return this;
        }

        public ExpectRequestResponse setMethod(String method) {
            this.method = t -> t.equals(method);
            return this;
        }

        public ExpectRequestResponse setHeaders(Predicate<List<Header>> headers) {
            this.headers = headers;
            return this;
        }

        public ExpectRequestResponse setSupplier(BiFunction<String, String, DResponse> supplier) {
            this.supplier = supplier;
            return this;
        }

        @Override
        public DResponse getUrlData(String baseParam, String uriParam, Object dataParam, List<Header> headersParam, String methodParam) throws IOException {
            if (base.test(baseParam) && uri.test(uriParam) && data.test(dataParam) && headers.test(headersParam) && method.test(methodParam)) {
                return supplier.apply(baseParam + uriParam, methodParam);
            }
            return null;
        }

        public ExpectRequestResponse andReply(String response) {
            this.supplier = (url, method) -> new DResponse(200, url, method, response);
            return this;
        }

        public ExpectRequestResponse andReply(int status, String text) {
            return andReply(status, text, () -> {
            });
        }

        public ExpectRequestResponse andReply(int status, String text, Runnable onCall) {
            supplier = (url, method) -> {
                onCall.run();
                return new DResponse(status, url, method, text);
            };
            return this;
        }

        public ExpectRequestResponse andReply(BiFunction<String, String, DResponse> responseSuplier) {
            supplier = responseSuplier;
            return this;
        }
    }
}