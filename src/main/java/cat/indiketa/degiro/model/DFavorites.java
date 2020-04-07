package cat.indiketa.degiro.model;

import java.util.ArrayList;

public class DFavorites extends ArrayList<Long> implements IValidable {

    @Override
    public boolean isInvalid() {
        return true;
    }
}
