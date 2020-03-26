package cat.indiketa.degiro.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import java.time.LocalDateTime;

/**
 * @author indiketa
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@With
public class DPrice {
    private String issueId;
    private Double bid;
    private Double ask;
    private Double last;
    private String lastTime;
    private String vwdProductName;
    private Double open;
    private Double low;
    private Double high;
    private Double previousClose;
    private Double bidVolume;
    private Double askVolume;
    private Double cumulativeVolume;
    private String lastDate;
    //Format expected: 2020-01-24 21:59:58
    private String combinedLastDateTime;
    private String fullName;
    private LocalDateTime lastDateTime;
}
