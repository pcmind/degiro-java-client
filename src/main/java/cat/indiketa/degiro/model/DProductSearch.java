/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.indiketa.degiro.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author indiketa
 */
@Data
public class DProductSearch implements IValidable {

    private int offset;
    private List<DProductDescription> products = new ArrayList<>();

    @Override
    public boolean isInvalid() {
        //no products field is present if no data is found
        return false;
    }
}
