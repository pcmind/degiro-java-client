package cat.indiketa.degiro.model;

import java.util.Date;

import lombok.Data;

/**
 *
 * @author indiketa
 */
@Data
public class DPrice {

    private String issueId;
    private Double bid;
    private Double ask;
    private Double last;
    private Date lastTime;
    private String vwdProductName;

}
