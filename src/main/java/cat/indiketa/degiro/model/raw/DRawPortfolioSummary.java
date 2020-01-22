package cat.indiketa.degiro.model.raw;

import lombok.Data;

import java.util.List;

/**
 *
 * @author indiketa
 */
@Data
public class DRawPortfolioSummary {

    private TotalPortfolio totalPortfolio;

    @Data
    public static class TotalPortfolio {

        private long lastUpdated;
        /**
         * Always filled with "totalPortfolio" string, so we assume only one entry may exist
         */
        private String name;
        private List<DFieldValue> value = null;
        private Boolean isAdded;

        public boolean getIsAdded() {
            return isAdded != null && isAdded;
        }

    }
}
