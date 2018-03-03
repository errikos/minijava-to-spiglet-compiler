package utils;

import java.io.File;

/**
 *
 */
public class FileUtils {

    public static boolean deleteDir(File dir) {
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (File file: files) {
                file.delete();
            }
        }
        return dir.isDirectory() && dir.delete();
    }

}
