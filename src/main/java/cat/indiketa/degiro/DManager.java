package cat.indiketa.degiro;

import cat.indiketa.degiro.http.DCommunication;
import cat.indiketa.degiro.http.DCommunication.DResponse;
import cat.indiketa.degiro.model.DCashFunds;
import cat.indiketa.degiro.model.DClient;
import cat.indiketa.degiro.model.DConfig;
import cat.indiketa.degiro.model.DLogin;
import cat.indiketa.degiro.model.DPortfolio;
import cat.indiketa.degiro.model.raw.DRawCashFunds;
import cat.indiketa.degiro.model.raw.DRawPortfolio;
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

        try {

            DResponse response = comm.getData(client, config, "portfolio=0", null);

            if (response.getStatus() != 200 && response.getStatus() != 201) {
                throw new DegiroException("Bad portfolio HTTP status " + response.getStatus());
            }

            DRawPortfolio rawPortfolio = gson.fromJson(response.getText(), DRawPortfolio.class);
            portfolio = DUtils.convert(rawPortfolio);

        } catch (IOException e) {
            throw new DegiroException("IOException while retrieving portfolio", e);
        }
        return portfolio;
    }

    public DCashFunds getCashFunds() throws DegiroException {

        DCashFunds cashFunds = null;
        ensureLogged();

        try {

            DResponse response = comm.getData(client, config, "cashFunds=0", null);

            if (response.getStatus() != 200 && response.getStatus() != 201) {
                throw new DegiroException("Bad cash funds HTTP status " + response.getStatus());
            }

            System.out.println(response.getText());

            DRawCashFunds rawCashFunds = gson.fromJson(response.getText(), DRawCashFunds.class);
            cashFunds = DUtils.convert(rawCashFunds);

        } catch (IOException e) {
            throw new DegiroException("IOException while retrieving cash funds", e);
        }
        return cashFunds;
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

            DResponse response = comm.getUrlData(BASE_TRADER_URL, "/login/secure/login", login);

            if (response.getStatus() != 200) {
                if (response.getStatus() == 400) {
                    throw new DInvalidCredentialsException();
                } else {
                    throw new DegiroException("Bad login HTTP status " + response.getStatus());
                }
            }

            response = comm.getUrlData(BASE_TRADER_URL, "/login/secure/config", null);

            if (response.getStatus() != 200) {
                throw new DegiroException("Bad config HTTP status " + response.getStatus());
            } else {
                config = gson.fromJson(response.getText(), DConfig.class);
            }

            response = comm.getUrlData(config.getPaUrl(), "client?sessionId=" + comm.getJSessionId(), null);

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
