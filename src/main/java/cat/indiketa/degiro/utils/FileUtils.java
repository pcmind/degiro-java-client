package cat.indiketa.degiro.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class FileUtils {
    public static JsonElement readJson(File file) throws IOException {
        if (file == null || !file.exists()) { // early exit
            throw new FileNotFoundException("File " + file + " not found");
        }
        try (FileReader fr = new FileReader(file)) {
            return new JsonParser().parse(fr);
        }
    }
}
