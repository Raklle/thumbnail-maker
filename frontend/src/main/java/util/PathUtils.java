package util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class PathUtils {

    public static String goUpPath(String path) {
        Path inputPath = Paths.get(path);

        Path parentPath = inputPath.getParent();

        if (parentPath != null) {
            String newPath = parentPath.toString();
            if (Objects.equals(newPath, "/") || Objects.equals(newPath, "\\") ) return "";
            return newPath;
        } else {
            return path;
        }
    }
}
