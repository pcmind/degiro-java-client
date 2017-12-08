package cat.indiketa.degiro.model;

/**
 *
 * @author indiketa
 */
public enum DOrderTime {
    DAY(1, "DAY"),
    PERMANENT(3, "GTC");

    private final int value;
    private final String strValue;

    private DOrderTime(int value, String strValue) {
        this.value = value;
        this.strValue = strValue;
    }

    public int getValue() {
        return value;
    }

    public String getStrValue() {
        return strValue;
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

    public static DOrderTime getOrderByValue(String value) {
        DOrderTime type = null;
        int i = 0;
        DOrderTime[] values = DOrderTime.values();
        while (i < values.length && !values[i].strValue.equals(value)) {
            i++;
        }
        if (i < values.length) {
            type = values[i];
        }

        return type;
    }
}
