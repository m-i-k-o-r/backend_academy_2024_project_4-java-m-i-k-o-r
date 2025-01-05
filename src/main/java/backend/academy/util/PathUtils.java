package backend.academy.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PathUtils {
    public static File createSafeFile(String filename) {
        Path baseDirectory = Paths.get(System.getProperty("user.dir"), "gen_image");
        Path safePath = baseDirectory.resolve(filename).normalize();

        try {
            Files.createDirectories(baseDirectory);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать директорию " + baseDirectory.toFile().getPath(), e);
        }

        return safePath.toFile();
    }
}
