package cat.indiketa.degiro.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DCashFund {

    private long id;
    private String currencyCode;
    private BigDecimal value;
    private String handling;
}
