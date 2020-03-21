package cat.indiketa.degiro.model;

import lombok.Data;

import java.util.Date;

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
    private Double open;
    private Double low;
    private Double high;
    private Double previousClose;
    private Double bidVolume;
    private Double askVolume;
    private Double cumulativeVolume;
    private String lastDate;
    private String combinedLastDateTime;
    private String fullName;
}
