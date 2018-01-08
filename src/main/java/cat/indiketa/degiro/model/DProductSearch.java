/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.indiketa.degiro.model;

import java.util.List;

/**
 *
 * @author indiketa
 */
public class DProductSearch {

    private int offset;
    private List<DProductDescription> products;

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public List<DProductDescription> getProducts() {
        return products;
    }

    public void setProducts(List<DProductDescription> products) {
        this.products = products;
    }

}
