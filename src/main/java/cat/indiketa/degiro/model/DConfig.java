package cat.indiketa.degiro.model;

import lombok.Data;

/**
 *
 * @author indiketa
 */
@Data
public class DConfig {

    private String tradingUrl;
    private Integer clientId;
    private String i18nUrl;
    private String paymentServiceUrl;
    private String reportingUrl;
    private String paUrl;
    private String vwdQuotecastServiceUrl;
    private String sessionId;
    private String productSearchUrl;
    private String dictionaryUrl;
    private String taskManagerUrl;
    private String firstLoginWizardUrl;
    private String loginUrl;
    private String vwdGossipsUrl;
    private String companiesServiceUrl;
    private String productTypesUrl;
    private String vwdNewsUrl;

}
