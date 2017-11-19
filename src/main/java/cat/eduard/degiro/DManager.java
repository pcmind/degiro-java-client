package cat.eduard.degiro;

import cat.eduard.degiro.http.DCommunication;
import cat.eduard.degiro.http.DCommunication.DResponse;
import cat.eduard.degiro.model.DClient;
import cat.eduard.degiro.model.DConfig;
import cat.eduard.degiro.model.DLogin;
import cat.eduard.degiro.model.DPortfolio;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import java.io.IOException;

/**
 *
 * @author indiketa
 */
public class DManager {

    private final DCredentials credentials;
    private final DCommunication comm;
    private DConfig config;
    private DClient client;
    private final Gson gson;
    private static final String BASE_TRADER_URL = "https://trader.degiro.nl";

    public DManager(DCredentials credencials) {
        this.credentials = credencials;
        this.comm = new DCommunication();
        this.gson = new Gson();

    }

    public DPortfolio getPortfolio() throws DegiroException {

        DPortfolio portfolio = null;
        ensureLogged();

        return portfolio;
    }

    private void ensureLogged() throws DegiroException {
        if (Strings.isNullOrEmpty(comm.getJSessionId())) {
            login();
        }
    }

    private void login() throws DegiroException {

        try {
            DLogin login = new DLogin();
            login.setUsername(credentials.getUsername());
            login.setPassword(credentials.getPassword());

            DResponse response = comm.getData(BASE_TRADER_URL, "/login/secure/login", login);

            if (response.getStatus() != 200) {
                if (response.getStatus() == 400) {
                    throw new DInvalidCredentialsException();
                } else {
                    throw new DegiroException("Bad login HTTP status " + response.getStatus());
                }
            }

            response = comm.getData(BASE_TRADER_URL, "/login/secure/config", null);

            if (response.getStatus() != 200) {
                throw new DegiroException("Bad config HTTP status " + response.getStatus());
            } else {
                config = gson.fromJson(response.getText(), DConfig.class);
            }

            response = comm.getData(config.getPaUrl(), "client?sessionId=" + comm.getJSessionId(), null);

            if (response.getStatus() != 200) {
                throw new DegiroException("Bad client info HTTP status " + response.getStatus());
            } else {
                client = gson.fromJson(response.getText(), DClient.class);
            }

        } catch (IOException e) {
            throw new DegiroException("IOException while retrieving user information", e);
        }

    }

}
