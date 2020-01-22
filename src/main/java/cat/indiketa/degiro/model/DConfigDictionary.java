package cat.indiketa.degiro.model;

import lombok.Data;

import java.util.List;

@Data
public class DConfigDictionary {
    private List<DStockCountry> stockCountries;
    //private List<?> bondExchanges;
    //private List<?> bondIssuerTypes;
    //private List<?> eurexCountries;
    //private List<?> futureExchanges;
    //private List<?> optionExchanges;
    //private List<?> combinationExchanges;
    //private List<?> cfdExchanges;
    private List<DExchange> exchanges;
    //private List<?> indices;
    private List<DRegion> regions;
    //private List<?> countries;
    private List<DProductType> productTypes;
    //private List<?> etfFeeTypes;
    //private List<?> investmentFundFeeTypes;
    //private List<?> optionAggregateTypes;
    //private List<?> leveragedAggregateTypes;
    //private List<?> etfAggregateTypes;
    //private List<?> investmentFundAggregateTypes;
    //private List<?> warrantAggregateTypes;
    //private List<?> lookupSortColumns;
    //private List<?> stockSortColumns;
    //private List<?> bondSortColumns;
    //private List<?> cfdSortColumns;
    //private List<?> etfSortColumns;
    //private List<?> futureSortColumns;
    //private List<?> investmentFundSortColumns;
    //private List<?> leveragedSortColumns;
    //private List<?> optionSortColumns;
    //private List<?> warrantSortColumns;

    @Data
    public static class DExchange {
        long id;
        String name;
        String code;
        String hiqAbbr;
        String country;
        String city;
        String micCode;
    }

    @Data
    public static class DRegion {
        long id;
        String name;
        String translation;
    }

    /**
     *
     */
    @Data
    public static class DProductType {
        long id;
        String name;
        String translation;
        String briefTranslation;
        String contractType;
    }

    @Data
    public static class DStockCountry {
        long id;
        long country;
        long[] indices;
    }
}
