package cat.indiketa.degiro.model.raw;

import lombok.Data;

import java.util.List;

@Data
public class DRawAlerts {
    private Alerts alerts;

    @Data
    public static class Alerts {

        public Long lastUpdated;
        public String name;
        public List<DRawPortfolio.Value> value = null;
        private Boolean isAdded;

        public boolean getIsAdded() {
            return isAdded != null && isAdded;
        }
    }
}
