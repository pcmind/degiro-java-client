package cat.indiketa.degiro.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@With
public class DAlert implements DCopyable<DAlert> {
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

    @Override
    public DAlert copy() {
        return withId(id);
    }
}
