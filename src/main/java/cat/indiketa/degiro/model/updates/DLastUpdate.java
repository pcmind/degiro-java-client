package cat.indiketa.degiro.model.updates;

import lombok.Value;

@Value(staticConstructor = "of")
public class DLastUpdate<T> {
    DUpdateToken token;
    T updates;
}
