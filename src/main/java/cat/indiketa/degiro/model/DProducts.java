package cat.indiketa.degiro.model;

import java.util.HashMap;

/**
 *
 * @author indiketa
 */
public class DProducts {

    private HashMap<String, DProduct> data;

    public HashMap<String, DProduct> getData() {
        return data;
    }

    public void setData(HashMap<String, DProduct> data) {
        this.data = data;
    }

}
