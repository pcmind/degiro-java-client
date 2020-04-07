package cat.indiketa.degiro.model;

import lombok.Data;

import java.util.Collection;
import java.util.List;

@Data
public class DConfigDictionary implements IValidable {
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

    @Override
    public boolean isInvalid() {
        return isInvalid(stockCountries) || isInvalid(exchanges) || isInvalid(regions) || isInvalid(productTypes);
    }

    private boolean isInvalid(Collection<? extends IValidable> v) {
        if (v == null || v.isEmpty()) {
            return true;
        }
        for (IValidable iValidable : v) {
            if (iValidable.isInvalid()) {
                return true;
            }
        }
        return false;
    }

    @Data
    public static class DExchange implements IValidable {
        long id;
        String name;
        String code;
        String hiqAbbr;
        String country;
        String city;
        String micCode;

        @Override
        public boolean isInvalid() {
            return id == 0 || name == null;
        }
    }

    @Data
    public static class DRegion implements IValidable {
        long id;
        String name;
        String translation;

        @Override
        public boolean isInvalid() {
            return id == 0 || name == null;
        }
    }

    /**
     *
     */
    @Data
    public static class DProductType implements IValidable {
        long id;
        String name;
        String translation;
        String briefTranslation;
        String contractType;

        @Override
        public boolean isInvalid() {
            return id == 0 || name == null || contractType == null;
        }
    }

    @Data
    public static class DStockCountry implements IValidable {
        long id;
        long country;
        long[] indices;

        @Override
        public boolean isInvalid() {
            return id == 0 || country == 0;
        }
    }


}
