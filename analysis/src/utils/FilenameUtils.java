package utils;

/**
 *
 */
public class FilenameUtils {

    public static String getBaseName(String filename) {
        int dot = filename.lastIndexOf('.');
        String base = (dot == -1) ? filename : filename.substring(0, dot);
        return base;
    }

    public static String getExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        String extension = (dot == -1) ? "" : filename.substring(dot+1);
        return extension;
    }

}
