package cat.indiketa.degiro.model.raw;

import cat.indiketa.degiro.model.raw.DRawPortfolio.Value_;
import java.util.List;

/**
 *
 * @author indiketa
 */
public class DRawPortfolioSummary {

    private TotalPortfolio totalPortfolio;

    public TotalPortfolio getTotalPortfolio() {
        return totalPortfolio;
    }

    public void setTotalPortfolio(TotalPortfolio totalPortfolio) {
        this.totalPortfolio = totalPortfolio;
    }

    public static class TotalPortfolio {

        private long lastUpdated;
        private String name;
        private List<Value_> value = null;
        private boolean isAdded;

        public long getLastUpdated() {
            return lastUpdated;
        }

        public void setLastUpdated(long lastUpdated) {
            this.lastUpdated = lastUpdated;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Value_> getValue() {
            return value;
        }

        public void setValue(List<Value_> value) {
            this.value = value;
        }

        public boolean isIsAdded() {
            return isAdded;
        }

        public void setIsAdded(boolean isAdded) {
            this.isAdded = isAdded;
        }

    }
}
