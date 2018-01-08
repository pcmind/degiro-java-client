package cat.indiketa.degiro.model;

import java.util.Map;

/**
 *
 * @author indiketa
 */
public class DProductDescriptions  {

    private Map<Long, DProductDescription> data;

    public Map<Long, DProductDescription> getData() {
        return data;
    }

    public void setData(Map<Long, DProductDescription> data) {
        this.data = data;
    }

}
