package util;

import lombok.NonNull;
import lombok.val;
import lombok.var;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileHelper {
    
    private static final Logger LOGGER = Logger.getLogger(FileHelper.class);

    public static void unzipFile(@NonNull Path pathToZip) {
        if (!Files.isRegularFile(pathToZip)) {
            LOGGER.error("Method: unzipFile. Zip with path " + pathToZip.toString() + " not found.");
            return;
        }

        try(val zis = new ZipArchiveInputStream(Files.newInputStream(pathToZip))) {
            var zipEntry = zis.getNextEntry();


            while (zipEntry != null) {
                Path fileFullName = pathToZip.getParent().resolve(zipEntry.getName());

                createDirs(fileFullName.getParent());

                try(val os = Files.newOutputStream(fileFullName)) {
                    copy(zis, os);
                }
                zipEntry = zis.getNextEntry();
            }
        } catch (IOException e) {
            LOGGER.error("Exception when try unzip file " + pathToZip.toString(), e);
        }
    }

    public static void deleteDir(Path path) {
        if (Files.exists(path)) {
            try {
                FileUtils.forceDelete(path.toFile());
            } catch (IOException e) {
                LOGGER.error("Exception when deleting dir " + path.toString(), e);
            }
        }
    }

    public static void copy(@NonNull InputStream in, @NonNull OutputStream os) throws IOException {
        byte[] buf = new byte[1024 * 8];
        int length;
        while ((length = in.read(buf)) > 0) {
            os.write(buf, 0, length);
        }
    }

    public static void createDirs(Path... paths) {
        for (Path path : paths) {
            if (!Files.exists(path)) {
                try {
                    Files.createDirectories(path);
                } catch (IOException e) {
                    LOGGER.error("Exception when creating directories with path " + path.toString(), e);
                }
            }
        }
    }
}
