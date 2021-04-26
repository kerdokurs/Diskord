package diskord.client;

import java.io.*;

public class Utils {
    public static void writeXmlFromResourcesToFile(String resourceName, File dest){
        try (InputStream fis = Utils.class.getClassLoader().getResourceAsStream(resourceName);
             FileOutputStream fos = new FileOutputStream(dest)) {
            int len;
            byte[] buffer = new byte[4096];
            while ((len = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
