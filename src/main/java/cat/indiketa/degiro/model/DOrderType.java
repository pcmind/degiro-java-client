/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.indiketa.degiro.model;

/**
 *
 * @author indiketa
 */
public enum DOrderType {
    LIMITED(0, "LIMIT"),
    MARKET_ORDER(2, "MARKET"),
    STOP_LOSS(3, "STOPLOSS"),
    LIMITED_STOP_LOSS(1, "STOPLIMIT");

    private final int value;
    private final String strValue;

    private DOrderType(int value, String strValue) {
        this.value = value;
        this.strValue = strValue;
    }

    public int getValue() {
        return value;
    }

    public String getStrValue() {
        return strValue;
    }

    public static DOrderType getOrderByValue(int value) {
        DOrderType type = null;
        int i = 0;
        DOrderType[] values = DOrderType.values();
        while (i < values.length && values[i].value != value) {
            i++;
        }
        if (i < values.length) {
            type = values[i];
        }

        return type;
    }

    public static DOrderType getOrderByValue(String value) {
        DOrderType type = null;
        int i = 0;
        DOrderType[] values = DOrderType.values();
        while (i < values.length && !values[i].strValue.equals(value)) {
            i++;
        }
        if (i < values.length) {
            type = values[i];
        }

        return type;
    }

}
