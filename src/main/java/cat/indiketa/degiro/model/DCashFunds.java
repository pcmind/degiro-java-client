package cat.indiketa.degiro.model;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author indiketa
 */
public class DCashFunds {

    private List<DCashFund> cashFunds;

    public List<DCashFund> getCashFunds() {
        return cashFunds;
    }

    public void setCashFunds(List<DCashFund> cashFunds) {
        this.cashFunds = cashFunds;
    }

    public static class DCashFund {

        private int id;
        private String currencyCode;
        private BigDecimal value;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getCurrencyCode() {
            return currencyCode;
        }

        public void setCurrencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
        }

        public BigDecimal getValue() {
            return value;
        }

        public void setValue(BigDecimal value) {
            this.value = value;
        }

    }

}
