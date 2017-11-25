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
    LIMITED(0),
    MARKET_ORDER(2),
    STOP_LOSS(3),
    LIMITED_STOP_LOSS(1);

    private int value;

    private DOrderType(int value) {
        this.value = value;
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

}
