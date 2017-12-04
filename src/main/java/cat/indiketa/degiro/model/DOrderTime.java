package cat.indiketa.degiro.model;

/**
 *
 * @author indiketa
 */
public enum DOrderTime {
    DAY(1),
    PERMANENT(3);

    private int value;

    private DOrderTime(int value) {
        this.value = value;
    }

    public static DOrderTime getOrderByValue(int value) {
        DOrderTime type = null;
        int i = 0;
        DOrderTime[] values = DOrderTime.values();
        while (i < values.length && values[i].value != value) {
            i++;
        }
        if (i < values.length) {
            type = values[i];
        }

        return type;
    }
}
