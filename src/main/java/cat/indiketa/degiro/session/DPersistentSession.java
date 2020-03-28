package cat.indiketa.degiro.session;

import cat.indiketa.degiro.log.DLog;
import cat.indiketa.degiro.model.DClient;
import cat.indiketa.degiro.model.DConfig;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 *
 * @author indiketa
 */
public class DPersistentSession extends DSession {

    private volatile File file;
    private volatile Gson gson;

    public DPersistentSession(String file) {
        this(Strings.isNullOrEmpty(file) ? null : new File(file));
    }

    public DPersistentSession(File file) {
        this.file = file;
        gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithModifiers(Modifier.VOLATILE, Modifier.STATIC).create();
        loadSession();
    }

    private void loadSession() {

        if (file == null) { // early exit
            return;
        }

        try {
            if (file.exists()) {
                try (FileReader fr = new FileReader(file)) {
                    DSession ds = gson.fromJson(fr, DSession.class);
                    if (ds != null) {
                        config = ds.config;
                        client = ds.client;
                        cookies = ds.cookies;
                        DLog.DEGIRO.info("Permanent session storage loaded (" + file.length() + " bytes).");
                    }
                }
            }
        } catch (IOException e) {
            DLog.DEGIRO.error("Error while loading persistent session data", e);
        }
    }

    private void saveSession() {

        if (file == null) { // early exit
            return;
        }

        try {
            try (FileWriter fw = new FileWriter(file)) {
                fw.write(gson.toJson(this));
            }
            DLog.DEGIRO.info("Permanent session storage updated (" + file.length() + " bytes).");
        } catch (IOException e) {
            DLog.DEGIRO.error("Error while saving persistent session data", e);
        }

    }

    @Override
    public void setCookies(List<BasicClientCookie> cookies) {
        super.setCookies(cookies);
        saveSession();
    }

    @Override
    public void clearSession() {
        super.clearSession();
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
