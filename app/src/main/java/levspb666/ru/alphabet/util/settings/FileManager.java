package levspb666.ru.alphabet.util.settings;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static levspb666.ru.alphabet.Settings.USER_FON_NAME;

public class FileManager {

    public static void deleteFile(String inputPath) {
        try {
            // delete the original file
            new File(inputPath).delete();
        } catch (Exception e) {
            Log.e("file", e.getMessage());
        }
    }

    public static void copyFile(String inputPath, String outputPath) {
        File dir = new File(outputPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try (InputStream in = new FileInputStream(inputPath);
             OutputStream out = new FileOutputStream(outputPath + USER_FON_NAME)) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            out.flush();
        } catch (Exception e) {
            Log.e("file", e.getMessage());
        }
    }
}
