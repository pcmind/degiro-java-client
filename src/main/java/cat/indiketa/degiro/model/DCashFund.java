package cat.indiketa.degiro.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@With
public class DCashFund implements DCopyable<DCashFund> {

    private long id;
    private String currencyCode;
    private BigDecimal value;
    private String handling;

    @Override
    public DCashFund copy() {
        return withId(id);
    }
}
