/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.indiketa.degiro.model.raw;

import cat.indiketa.degiro.model.raw.DRawPortfolio.Value;
import java.util.List;

/**
 *
 * @author indiketa
 */
public class DRawCashFunds {

    public CashFunds cashFunds;

    public CashFunds getCashFunds() {
        return cashFunds;
    }

    public void setCashFunds(CashFunds cashFunds) {
        this.cashFunds = cashFunds;
    }

    
    
    public class CashFunds {

        public Long lastUpdated;
        public String name;
        public List<Value> value = null;

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
        
        

    }

}
