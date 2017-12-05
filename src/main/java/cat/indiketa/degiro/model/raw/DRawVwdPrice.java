/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.indiketa.degiro.model.raw;

import java.util.List;

/**
 *
 * @author indiketa
 */
public class DRawVwdPrice {

    public String m;
    public List<String> v = null;

    public String getM() {
        return m;
    }

    public void setM(String m) {
        this.m = m;
    }

    public List<String> getV() {
        return v;
    }

    public void setV(List<String> v) {
        this.v = v;
    }

}
