/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.indiketa.degiro.engine;

import com.google.common.base.Strings;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author indiketa
 */
public class PathManager {

    private final File dataDirectory;
    private File sessionPath;
    private File configPath;

    public PathManager(File dataDirectory) throws IOException {
        this.dataDirectory = dataDirectory;
        if (!dataDirectory.isDirectory()) {
            throw new IOException("dataDirectory is not a directory");
        }
    }

    public File getSessionPath() {
        if (sessionPath == null) {
            sessionPath = new File(dataDirectory, "session");
            sessionPath.mkdirs();
        }

        return sessionPath;
    }

    public File getSessionFile(String username) {
        username = Strings.nullToEmpty(username);
        return new File(getSessionPath(), username.hashCode() + ".json");
    }

    public File getConfigPath() {
        if (configPath == null) {
            configPath = new File(dataDirectory, "config");
            configPath.mkdirs();
        }

        return configPath;
    }

}
