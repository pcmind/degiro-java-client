package cat.indiketa.degiro;

import lombok.Value;

@Value(staticConstructor = "of")
public final class DeGiroHost {
    // Extracted from: https://charting.vwdservices.com/hchart/v1/deGiro/api.js?culture=en&userToken=XXXXXX&tz=Europe/Madrid
    private static final String CHARTING_URL = "https://charting.vwdservices.com/hchart/v1/deGiro/data.js";
    private static final String BASE_TRADER_URL = "https://trader.degiro.nl";
    private static final String QUOTE_CAST = "https://degiro.quotecast.vwdservices.com/CORS";

    private static final DeGiroHost DEFAULT = new DeGiroHost(BASE_TRADER_URL, CHARTING_URL, QUOTE_CAST);
    private static final String TEST_URL = "http://localhost:4000";
    private static final DeGiroHost TEST = new DeGiroHost(TEST_URL, TEST_URL + "/hchart", TEST_URL + "/quotecast");

    private String baseUrl;
    private String chartingUrl;
    private String quoteCastUrl;

    public static DeGiroHost defaultEndPoint() {
        return DEFAULT;
    }

    public static DeGiroHost testEndPoint() {
        return TEST;
    }
}
