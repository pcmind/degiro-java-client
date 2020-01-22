package cat.indiketa.degiro.model;

import lombok.Data;

import java.util.List;

/**
 *
 * @author indiketa
 */
@Data
public class DTransactions {

    private List<DTransaction> data = null;
    private long status;
    private String statusText;

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
