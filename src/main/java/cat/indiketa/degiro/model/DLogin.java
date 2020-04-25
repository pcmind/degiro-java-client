package cat.indiketa.degiro.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author indiketa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DLogin {
    private String username;
    private String password;
}
