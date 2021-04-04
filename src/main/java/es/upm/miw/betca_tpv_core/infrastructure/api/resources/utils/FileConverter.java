package es.upm.miw.betca_tpv_core.infrastructure.api.resources.utils;

import org.springframework.http.codec.multipart.FilePart;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileConverter {

    private static final String USER_HOME = "user.home";

    private File file;

    public FileConverter(String path, String name, String extension) {
        path = System.getProperty(USER_HOME) + path;
        this.file = new File(path + name + extension);
        if (!file.exists()) {
            try {
                Files.createDirectories(Paths.get(path));
            } catch (IOException e) {
                throw new FileException(String.format("FileConverter::prepareDocument. Error when creating directory (%s). %s", path, e));
            }
        }
    }

    public File convert(FilePart filePart) {
        filePart.transferTo(this.file);
        return this.file;
    }

    public static byte[] getBytes(File file) {
        byte[] agreement;
        try {
            agreement = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new FileException(String.format("FileConverter::getBytes. Error when reading file bytes. %s", e));
        }
        return agreement;
    }

}
