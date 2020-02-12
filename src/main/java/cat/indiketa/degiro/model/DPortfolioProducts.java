package cat.indiketa.degiro.model;

import lombok.Data;

import java.util.*;

/**
 * @author indiketa
 */
@Data
public class DPortfolioProducts {
    private long lastUpdate;

    private List<DUpdate<DPortfolioProduct, String>> updates = new ArrayList<>();

    public boolean hasChanges() {
        return !updates.isEmpty();
    }


}
