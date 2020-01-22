package cat.indiketa.degiro.model;

import lombok.Data;

/**
 *
 * @author indiketa
 */
@Data
public class DLogin {

    private String username;
    private String password;
    private boolean isRedirectToMobile;
    private String loginButtonUniversal;
}
