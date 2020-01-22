/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.indiketa.degiro.model.raw;

import lombok.Data;

import java.util.List;

/**
 *
 * @author indiketa
 */
@Data
public class DRawVwdPrice {

    public String m;
    public List<String> v = null;

}
