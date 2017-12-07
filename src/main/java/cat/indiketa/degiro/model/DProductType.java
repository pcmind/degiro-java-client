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
public enum DProductType {
    ALL(0),
    SHARES(1),
    BONDS(2),
    FUTURES(7),
    OPTIONS(8),
    INVESTMENT_FUNDS(13),
    LEVERAGED_PRODUCTS(14),
    ETF(131),
    CFD(535),
    WARRANTS(536);

    private final int typeCode;

    private DProductType(int typeCode) {
        this.typeCode = typeCode;
    }

    public int getTypeCode() {
        return typeCode;
    }

}
