package cat.indiketa.degiro.model;

import lombok.Data;

import java.util.List;

/**
 *
 * @author indiketa
 */
@Data
public class DTransactions implements IValidable {

    private List<DTransaction> data = null;
    private long status;
    private String statusText;

    @Override
    public boolean isInvalid() {
        return data == null;
    }

    @Data
    public static class DTransaction {

        private long id;
        private long productId;
        private String date;
        private DOrderAction buysell;
        private double price;
        private long quantity;
        private double total;
    }

}
