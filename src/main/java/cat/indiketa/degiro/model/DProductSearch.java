/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.indiketa.degiro.model;

import java.util.List;

import lombok.Data;

/**
 *
 * @author indiketa
 */
@Data
public class DProductSearch {

    private int offset;
    private List<DProductDescription> products;

}
