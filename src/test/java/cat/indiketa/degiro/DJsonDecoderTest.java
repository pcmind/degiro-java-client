package cat.indiketa.degiro;

import cat.indiketa.degiro.model.DHistoricalOrder;
import cat.indiketa.degiro.model.DOrder;
import cat.indiketa.degiro.model.DOrderAction;
import cat.indiketa.degiro.model.DOrderConfirmation;
import cat.indiketa.degiro.model.DOrderTime;
import cat.indiketa.degiro.model.DOrderType;
import cat.indiketa.degiro.model.DPortfolioProduct;
import cat.indiketa.degiro.model.updates.DLastUpdate;
import cat.indiketa.degiro.model.updates.DUpdate;
import cat.indiketa.degiro.model.updates.DUpdates;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DJsonDecoderTest {
    DJsonDecoder d = new DJsonDecoder();

    @Test
    void decodeErrorMessage() throws IOException {
        final DOrderConfirmation result = d.fromJsonData(
                "{\"data\":{\"confirmationId\":\"12caf3da-c2f2-4c0a-b5c2-e5f42c04a4be\",\"transactionFees\":[{\"id\":2,\"amount\":0.04,\"currency\":\"USD\"},{\"id\":3,\"amount\":0.50,\"currency\":\"EUR\"}]}}"
                , DOrderConfirmation.class
        );
        assertEquals("12caf3da-c2f2-4c0a-b5c2-e5f42c04a4be", result.getConfirmationId());
    }

    @Test
    void emptyUpdateResponse() throws IOException {
        //given
        String response = "{\"orders\": {\"lastUpdated\":33,\"name\":\"orderTable\",\"value\":[]}, " +
                "\"historicalOrders\": {\"lastUpdated\":345,\"name\":\"historicalOrderTable\",\"value\":[]}, " +
                "\"transactions\": {\"lastUpdated\":111,\"name\":\"transTable\",\"value\":[]}, " +
                "\"portfolio\": {\"lastUpdated\":643,\"value\":[]}, " +
                "\"totalPortfolio\": {\"lastUpdated\":496,\"value\":[]}, " +
                "\"alerts\": {\"lastUpdated\":24,\"value\":[]}}";

        //when
        final DUpdates dUpdates = d.fromJson(response, DUpdates.class);

        //then
        assertEquals(33, dUpdates.getOrders().getToken().getLastUpdated());
        assertEquals(345, dUpdates.getHistoricalOrders().getToken().getLastUpdated());
        //assertEquals(111, dUpdates.getTransactions().getLastUpdate().getLastUpdated());
        assertEquals(643, dUpdates.getPortfolio().getToken().getLastUpdated());
        assertEquals(496, dUpdates.getPortfolioSummary().getToken().getLastUpdated());
        assertEquals(24, dUpdates.getAlerts().getToken().getLastUpdated());
    }

    @Test
    void withHistoricalOrder() throws IOException {
        //given
        String response = "{" +
                "\"orders\": {\"lastUpdated\":32,\"value\":[]}, " +
                "\"historicalOrders\": {\"lastUpdated\":14,\"name\":\"historicalOrderTable\",\"value\":[{\"name\":\"order\",\"id\":\"5a6993d7-5baa-4641-89aa-acf20825b371\",\"isAdded\":true,\"value\":[" +
                "{\"name\":\"id\",\"isAdded\":true,\"value\":\"5a6993d7-5baa-4641-89aa-acf20825b371\"}," +
                "{\"name\":\"date\",\"isAdded\":true,\"value\":\"14:57\"}," +
                "{\"name\":\"productId\",\"isAdded\":true,\"value\":1143516}," +
                "{\"name\":\"product\",\"isAdded\":true,\"value\":\"Infinera Corp\"}," +
                "{\"name\":\"currency\",\"isAdded\":true,\"value\":\"USD\"}," +
                "{\"name\":\"buysell\",\"isAdded\":true,\"value\":\"S\"}," +
                "{\"name\":\"size\",\"isAdded\":true,\"value\":530.000000}," +
                "{\"name\":\"quantity\",\"isAdded\":true,\"value\":0.000000}," +
                "{\"name\":\"price\",\"isAdded\":true,\"value\":6.3400}," +
                "{\"name\":\"orderType\",\"isAdded\":true,\"value\":\"LIMIT\"}," +
                "{\"name\":\"retainedOrder\",\"isAdded\":true,\"value\":false}," +
                "{\"name\":\"sentToExchange\",\"isAdded\":true,\"value\":true}]}]}, " +
                "\"transactions\": {\"lastUpdated\":0,\"name\":\"transTable\",\"value\":[]}, " +
                "\"portfolio\": {\"lastUpdated\":357,\"value\":[]}, \"totalPortfolio\": {\"lastUpdated\":40,\"value\":[]}, " +
                "\"alerts\": {\"lastUpdated\":0,\"name\":\"alerts\",\"isAdded\":true,\"value\":[]}" +
                "}";

        //when
        final DUpdates dUpdates = d.fromJson(response, DUpdates.class);

        //then
        final DLastUpdate<List<DUpdate<DHistoricalOrder>>> historicalOrders = dUpdates.getHistoricalOrders();
        final List<DUpdate<DHistoricalOrder>> updates = historicalOrders.getUpdates();
        assertThat(updates).hasSize(1);
        final DHistoricalOrder aNew = updates.get(0).getNew();
        assertThat(aNew.getId()).isEqualTo("5a6993d7-5baa-4641-89aa-acf20825b371");
    }

    @Test
    void withPortfolio() throws IOException {
        //given
        String response = "{" +
                "\"portfolio\": {\"lastUpdated\":357,\"name\":\"portfolio\",\"isAdded\":true," +
                "\"value\":[{\"name\":\"positionrow\",\"id\":\"6275096\",\"isAdded\":true,\"value\":[{\"name\":\"id\",\"isAdded\":true,\"value\":\"6275096\"}," +
                "{\"name\":\"positionType\",\"isAdded\":true,\"value\":\"PRODUCT\"},{\"name\":\"size\",\"isAdded\":true,\"value\":0}," +
                "{\"name\":\"price\",\"isAdded\":true,\"value\":3.4300},{\"name\":\"value\",\"isAdded\":true,\"value\":0E-44},{\"name\":\"accruedInterest\",\"isAdded\":true}," +
                "{\"name\":\"plBase\",\"isAdded\":true,\"value\":{\"EUR\":-70.35}},{\"name\":\"todayPlBase\",\"isAdded\":true,\"value\":{\"EUR\":0E-44}}," +
                "{\"name\":\"portfolioValueCorrection\",\"isAdded\":true,\"value\":0},{\"name\":\"breakEvenPrice\",\"isAdded\":true,\"value\":0}," +
                "{\"name\":\"averageFxRate\",\"isAdded\":true,\"value\":1},{\"name\":\"realizedProductPl\",\"isAdded\":true,\"value\":-68.69305987500000000}," +
                "{\"name\":\"realizedFxPl\",\"isAdded\":true,\"value\":-1.65694012500000000000},{\"name\":\"todayRealizedProductPl\",\"isAdded\":true,\"value\":0E-17}," +
                "{\"name\":\"todayRealizedFxPl\",\"isAdded\":true,\"value\":0E-20}]}]}" +
                "}";

        //when
        final DUpdates dUpdates = d.fromJson(response, DUpdates.class);

        //then
        assertEquals(357, dUpdates.getPortfolio().getToken().getLastUpdated());

        final List<DUpdate<DPortfolioProduct>> updates = dUpdates.getPortfolio().getUpdates();
        assertThat(updates).hasSize(1);
        final DUpdate<DPortfolioProduct> product = updates.get(0);

        assertEquals(DUpdate.Kind.CREATED, product.getType());
        assertThat(product.getId()).isEqualTo("6275096");
        final DPortfolioProduct dPortfolioProduct = new DPortfolioProduct();
        dPortfolioProduct.setId("6275096");
        dPortfolioProduct.setPositionType("PRODUCT");
        dPortfolioProduct.setPrice(new BigDecimal("3.4300"));
        dPortfolioProduct.setValue(new BigDecimal("0E-44"));
        dPortfolioProduct.setPlBase(ImmutableMap.of("EUR", new BigDecimal("-70.35")));
        dPortfolioProduct.setTodayPlBase(ImmutableMap.of("EUR", new BigDecimal("0E-44")));
        dPortfolioProduct.setAverageFxRate(new BigDecimal("1"));
        dPortfolioProduct.setPortfolioValueCorrection(new BigDecimal("0"));
        dPortfolioProduct.setBreakEvenPrice(new BigDecimal("0"));
        dPortfolioProduct.setRealizedProductPl(new BigDecimal("-68.69305987500000000"));
        dPortfolioProduct.setRealizedFxPl(new BigDecimal("-1.65694012500000000000"));
        dPortfolioProduct.setTodayRealizedProductPl(new BigDecimal("0E-17"));
        dPortfolioProduct.setTodayRealizedFxPl(new BigDecimal("0E-20"));

        assertEquals(dPortfolioProduct, product.getNew());
    }

    @Test
    void decodeOrder() throws IOException {
        //given
        String response = "{" +
                "\"orders\": {\"lastUpdated\": 21,\"name\": \"orderTable\",\"value\": " +
                "[{\"name\": \"order\",\"id\": \"cffc2834-3d41-47e8-cb12-7ba549c85b83\",\"isAdded\": true,\"value\": " +
                "[{\"name\": \"id\",\"isAdded\": true,\"value\": \"cffc2834-3d41-47e8-cb12-7ba549c85b83\"}," +
                "{\"name\": \"date\",\"isAdded\": true,\"value\": \"31/10\"}," +
                "{\"name\": \"productId\",\"isAdded\": true,\"value\": 181116}," +
                "{\"name\": \"product\",\"isAdded\": true,\"value\": \"Mota-Engil\"}," +
                "{\"name\": \"contractType\",\"isAdded\": true,\"value\": 1}," +
                "{\"name\": \"contractSize\",\"isAdded\": true,\"value\": 1.000000}," +
                "{\"name\": \"currency\",\"isAdded\": true,\"value\": \"EUR\"}," +
                "{\"name\": \"buysell\",\"isAdded\": true,\"value\": \"S\"}," +
                "{\"name\": \"size\",\"isAdded\": true,\"value\": 240.000000}," +
                "{\"name\": \"quantity\",\"isAdded\": true,\"value\": 240.000000}," +
                "{\"name\": \"price\",\"isAdded\": true,\"value\": 2.0400}," +
                "{\"name\": \"stopPrice\",\"isAdded\": true,\"value\": 0.0000}," +
                "{\"name\": \"totalOrderValue\",\"isAdded\": true,\"value\": 489.6000000000000000}," +
                "{\"name\": \"orderTypeId\",\"isAdded\": true,\"value\": 0}," +
                "{\"name\": \"orderTimeTypeId\",\"isAdded\": true,\"value\": 3}," +
                "{\"name\": \"orderType\",\"isAdded\": true,\"value\": \"LIMIT\"}," +
                "{\"name\": \"orderTimeType\",\"isAdded\": true,\"value\": \"GTC\"}," +
                "{\"name\": \"isModifiable\",\"isAdded\": true,\"value\": true}," +
                "{\"name\": \"isDeletable\",\"isAdded\": true,\"value\": true}]}]" +
                "}" +
                "}";

        //when
        final DUpdates dUpdates = d.fromJson(response, DUpdates.class);

        //then
        assertThat(dUpdates.getOrders().getToken().getLastUpdated()).isEqualTo(21);
        final DUpdate<DOrder> orderCreator = dUpdates.getOrders().getUpdates().get(0);
        assertThat(orderCreator.getType()).isEqualTo(DUpdate.Kind.CREATED);
        final DOrder aNew = orderCreator.getNew();
        assertThat(aNew.getId()).isEqualTo("cffc2834-3d41-47e8-cb12-7ba549c85b83");
        assertThat(aNew.getOrderType()).isEqualTo(DOrderType.LIMITED);
        assertThat(aNew.getOrderTimeType()).isEqualTo(DOrderTime.PERMANENT);
        assertThat(aNew.getBuysell()).isEqualTo(DOrderAction.SELL);


    }

    @Test
    void portfolioDecoding() throws IOException {
        //given
        String response = "{\"portfolio\": " +
                "{\"lastUpdated\": 221,\"name\": \"portfolio\",\"isAdded\": true,\"value\": [" +
                "{\"name\": \"positionrow\",\"id\": \"14915790\",\"isAdded\": true,\"value\": [" +
                "{\"name\": \"id\",\"isAdded\": true,\"value\": \"14915790\"}," +
                "{\"name\": \"positionType\",\"isAdded\": true,\"value\": \"PRODUCT\"}," +
                "{\"name\": \"size\",\"isAdded\": true,\"value\": 0}," +
                "{\"name\": \"price\",\"isAdded\": true,\"value\": 8.0100}," +
                "{\"name\": \"value\",\"isAdded\": true,\"value\": 0E-44}," +
                "{\"name\": \"accruedInterest\",\"isAdded\": true}," +
                "{\"name\": \"plBase\",\"isAdded\": true,\"value\": {\"EUR\": 11.11000000000000000}}," +
                "{\"name\": \"todayPlBase\",\"isAdded\": true,\"value\": {\"EUR\": 0E-44}}," +
                "{\"name\": \"portfolioValueCorrection\",\"isAdded\": true,\"value\": 0}," +
                "{\"name\": \"breakEvenPrice\",\"isAdded\": true,\"value\": 0}," +
                "{\"name\": \"averageFxRate\",\"isAdded\": true,\"value\": 1}," +
                "{\"name\": \"realizedProductPl\",\"isAdded\": true,\"value\": 11.76223200000000000}," +
                "{\"name\": \"realizedFxPl\",\"isAdded\": true,\"value\": -0.65223200000000000}," +
                "{\"name\": \"todayRealizedProductPl\",\"isAdded\": true,\"value\": 0E-17}," +
                "{\"name\": \"todayRealizedFxPl\",\"isAdded\": true,\"value\": 0E-17}" +
                "]}, {\"name\": \"positionrow\",\"id\": \"5544444\",\"isRemoved\": true,\"value\": []}" +
                "]}" +
                "}";

        //when
        final DUpdates dUpdates = d.fromJson(response, DUpdates.class);

        //then
        assertThat(dUpdates.getPortfolio().getToken().getLastUpdated()).isEqualTo(221);
        final DUpdate<DPortfolioProduct> portfolioProductUpdate = dUpdates.getPortfolio().getUpdates().get(0);
        assertThat(portfolioProductUpdate.getType()).isEqualTo(DUpdate.Kind.CREATED);
        final DPortfolioProduct aNew = portfolioProductUpdate.getNew();
        assertThat(aNew.getId()).isEqualTo("14915790");
        assertThat(aNew.getPositionType()).isEqualTo("PRODUCT");
        final DUpdate<DPortfolioProduct> deleteted = dUpdates.getPortfolio().getUpdates().get(1);
        assertThat(deleteted.getType()).isEqualTo(DUpdate.Kind.DELETED);
        assertThat(deleteted.getId()).isEqualTo("5544444");

        //simulated update
        String r2 = response.replaceAll(",\"isAdded\": true", "");
        final DUpdates updates2 = d.fromJson(r2, DUpdates.class);
        final DPortfolioProduct objectToUpdate = new DPortfolioProduct();
        //delta of all original field should happen to create the exact same object
        final DUpdate<DPortfolioProduct> dUpdate = updates2.getPortfolio().getUpdates().get(0);
        assertThat(dUpdate.getType()).isEqualTo(DUpdate.Kind.UPDATED);
        dUpdate.update(objectToUpdate);
        assertThat(objectToUpdate).isEqualTo(aNew);

    }
}