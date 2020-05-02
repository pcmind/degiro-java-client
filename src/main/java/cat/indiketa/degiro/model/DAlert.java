package cat.indiketa.degiro.model;

import lombok.Data;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
public class DAlert {
    private long id;
    private String title;
    private String text;
    private LocalDate insertionDate;
    private OffsetDateTime modificationDate;

    /**
     * 1: 'urgent',
     * 2: 'high',
     * 3: 'medium',
     * 4: 'low',
     * 5: 'info'
     */
    private int type;

}
