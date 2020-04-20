package cat.indiketa.degiro.model.updates;

import lombok.Value;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Value(staticConstructor = "of")
public class DUpdateToken {
    DUpdateSection section;
    long lastUpdated;

    public String encode() {
        return section.name() + "=" + lastUpdated;
    }

    public static List<DUpdateToken> allSections() {
        return createInitial(DUpdateSection.values());
    }

    public static List<DUpdateToken> createInitial(DUpdateSection... sections) {
        return Stream.of(sections).map(e -> DUpdateToken.of(e, 0)).collect(Collectors.toList());
    }
}
