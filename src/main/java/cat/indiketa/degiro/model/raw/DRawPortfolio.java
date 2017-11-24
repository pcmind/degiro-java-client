package cat.eduard.degiro.model.raw;

import java.util.List;

/**
 *
 * @author indiketa
 */
public class DRawPortfolio {

    private Portfolio portfolio;

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public static class Portfolio {

        private Long lastUpdated;
        private String name;
        private List<Value> value = null;
        private Boolean isAdded;

        public Long getLastUpdated() {
            return lastUpdated;
        }

        public void setLastUpdated(Long lastUpdated) {
            this.lastUpdated = lastUpdated;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Value> getValue() {
            return value;
        }

        public void setValue(List<Value> value) {
            this.value = value;
        }

        public Boolean getIsAdded() {
            return isAdded;
        }

        public void setIsAdded(Boolean isAdded) {
            this.isAdded = isAdded;
        }

    }

    public static class Value {

        private String name;
        private long id;
        private List<Value_> value = null;
        private boolean isAdded;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
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

    public static class Value_ {

        private String name;
        private Object value;
        private boolean isAdded;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
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
