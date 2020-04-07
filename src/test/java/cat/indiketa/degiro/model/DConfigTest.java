package cat.indiketa.degiro.model;

import cat.indiketa.degiro.DJsonDecoder;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DConfigTest {
    public static final String VALID_JSON = "{\"data\":{\"tradingUrl\":\"https://trader.degiro.nl/trading/secure/\",\"paUrl\":\"https://trader.degiro.nl/pa/secure/\",\"reportingUrl\":\"https://trader.degiro.nl/reporting/secure/\",\"paymentServiceUrl\":\"https://trader.degiro.nl/payments/\",\"cashSolutionsUrl\":\"\",\"productSearchUrl\":\"https://trader.degiro.nl/product_search/secure/\",\"dictionaryUrl\":\"https://trader.degiro.nl/product_search/config/dictionary/\",\"productTypesUrl\":\"https://trader.degiro.nl/product_search/config/productTypes/\",\"companiesServiceUrl\":\"https://trader.degiro.nl/dgtbxdsservice/\",\"i18nUrl\":\"https://trader.degiro.nl/i18n/\",\"vwdQuotecastServiceUrl\":\"https://trader.degiro.nl/vwd-quotecast-service/\",\"vwdNewsUrl\":\"https://solutions.vwdservices.com/customers/degiro.nl/news-feed/api/\",\"vwdGossipsUrl\":\"https://solutions.vwdservices.com/customers/degiro.nl/news-feed/api/\",\"firstLoginWizardUrl\":\"https://trader.degiro.nl/firstloginwizard/secure/\",\"taskManagerUrl\":\"https://trader.degiro.nl/taskmanager/\",\"landingPath\":\"/trader/\",\"betaLandingPath\":\"/trader4/\",\"mobileLandingPath\":\"/mobile/\",\"loginUrl\":\"https://trader.degiro.nl/login/pt\",\"sessionId\":\"AD828010FBB1450A9C59E7DD9162C428.prod_b_312_1\",\"clientId\":123456}}";

    @Test
    void canLoadConfig() throws IOException {
        //given
        final DJsonDecoder d = new DJsonDecoder();
        //when
        final DConfig dClientData = d.fromJsonData(VALID_JSON, DConfig.class);

        //then
        assertNotNull(dClientData);
        assertEquals("https://trader.degiro.nl/trading/secure/", dClientData.getTradingUrl());
        assertEquals(123456, dClientData.getClientId());
        assertFalse(dClientData.isInvalid());
    }
}