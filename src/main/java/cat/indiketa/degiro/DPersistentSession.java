package cat.indiketa.degiro;

import cat.indiketa.degiro.log.DLog;
import cat.indiketa.degiro.model.DClient;
import cat.indiketa.degiro.model.DConfig;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.List;
import org.apache.http.impl.cookie.BasicClientCookie;

/**
 *
 * @author indiketa
 */
public class DPersistentSession extends DSession {

    private volatile String file;
    private volatile Gson gson;

    public DPersistentSession(String file) {
        this.file = file;
        gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithModifiers(Modifier.VOLATILE, Modifier.STATIC).create();
        loadSession();
    }

    private void loadSession() {

        if (Strings.isNullOrEmpty(file)) { // early exit
            return;
        }

        try {
            if (new File(file).exists()) {
                try (FileReader fr = new FileReader(file)) {
                    DSession ds = gson.fromJson(fr, DSession.class);
                    if (ds != null) {
                        config = ds.config;
                        client = ds.client;
                        vwdSession = ds.vwdSession;
                        cookies = ds.cookies;
                        DLog.SESSION.info("Permanent session storage loaded (" + new File(file).length() + " bytes).");
                    }
                }
            }
        } catch (IOException e) {
            DLog.SESSION.error("Error while loading persistent session data", e);
        }
    }

    private void saveSession() {

        if (Strings.isNullOrEmpty(file)) { // early exit
            return;
        }

        try {
            try (FileWriter fw = new FileWriter(file)) {
                fw.write(gson.toJson(this));
            }
            DLog.SESSION.info("Permanent session storage updated (" + new File(file).length() + " bytes).");
        } catch (IOException e) {
            DLog.SESSION.error("Error while saving persistent session data", e);
        }

    }

    @Override
    public void setCookies(List<BasicClientCookie> cookies) {
        super.setCookies(cookies);
        saveSession();
    }

    @Override
    public void setVwdSession(String vwdSession) {
        super.setVwdSession(vwdSession);
        saveSession();
    }

    @Override
    public void setClient(DClient client) {
        super.setClient(client);
        saveSession();
    }

    @Override
    public void setConfig(DConfig config) {
        super.setConfig(config);
        saveSession();
    }

}
