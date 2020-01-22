package cat.indiketa.degiro.model;

import java.util.Map;

import lombok.Data;

/**
 *
 * @author indiketa
 */
@Data
public class DProductDescriptions  {

    private Map<Long, DProductDescription> data;
}
