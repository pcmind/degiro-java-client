package cat.indiketa.degiro.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author indiketa
 */
@Data
public class DOrders {

    private long lastUpdate;

    public boolean hasChanges() {
        return !added.isEmpty() || !updates.isEmpty() || !removed.isEmpty();
    }

    private List<DOrder> added = new ArrayList<>();
    private List<DOrder> updates = new ArrayList<>();
    private Set<String> removed = new LinkedHashSet<>();


}
