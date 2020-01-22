package cat.indiketa.degiro.model;

import lombok.Data;

import java.util.List;

/**
 *
 * @author indiketa
 */
@Data
public class DOrderConfirmation {
    //"{\"data\":{\"confirmationId\":\"15caf4dd-c2f2-4c0a-b5c2-e5f41c04a4be\",\"transactionFees\":[{\"id\":2,\"amount\":0.04,\"currency\":\"USD\"},{\"id\":3,\"amount\":0.50,\"currency\":\"EUR\"}]}}"

    private String confirmationId;
    private List<TransactionFee> transactionFees;

    @Data
    public static class TransactionFee {
        private long id;
        private Double amount;
        private String currency;
    }
}
