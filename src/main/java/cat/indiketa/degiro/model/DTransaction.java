package cat.indiketa.degiro.model;

import lombok.Data;

@Data
public class DTransaction implements IValidable {

    private long id;
    private long productId;
    private String date;
    private DOrderAction buysell;
    private double price;
    private long quantity;
    private double total;
    private long orderTypeId;
    private String counterParty;
    private boolean transfered;
    private double fxRate;
    private double totalInBaseCurrency;
    private double feeInBaseCurrency;
    private double totalPlusFeeInBaseCurrency;

    @Override
    public boolean isInvalid() {
        return id == 0 || productId == 0 || date == null;
    }
}
