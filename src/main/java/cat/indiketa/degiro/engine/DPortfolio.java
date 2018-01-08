/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.indiketa.degiro.engine;

import java.util.List;

/**
 *
 * @author indiketa
 */
public class DPortfolio {

    private final DEngine engine;
    private List<DProduct> products;

    public DPortfolio(DEngine engine) {
        this.engine = engine;
    }

    @Override
    public String toString() {
        String format = "%-20s | %d | %s";
        String msg = "";

        if (products != null) {
            for (DProduct product : products) {

                msg = String.format(format, product.getDescription().getName()) + "\n";

            }
        }
        return msg;
    }

    private String getDescription() {
        return null;
    }

}
