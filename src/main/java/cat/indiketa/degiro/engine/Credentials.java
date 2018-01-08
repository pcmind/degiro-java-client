/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.indiketa.degiro.engine;

import cat.indiketa.degiro.log.DLog;
import cat.indiketa.degiro.utils.DCredentials;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author indiketa
 */
public class Credentials implements DCredentials {

    private Properties props = null;

    public Credentials(File file) {
        props = new Properties();
        try {
            try (InputStream is = new FileInputStream(file)) {
                props.load(is);
            }
        } catch (IOException e) {
            DLog.ENGINE.error("Error loading credentials", e);
        }
    }

    @Override
    public String getUsername() {
        return props.getProperty("username");
    }

    @Override
    public String getPassword() {
        return props.getProperty("password");
    }

}
