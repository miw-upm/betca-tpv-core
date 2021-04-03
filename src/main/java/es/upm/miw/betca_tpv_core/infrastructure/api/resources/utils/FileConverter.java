package es.upm.miw.betca_tpv_core.infrastructure.api.resources.utils;

import org.springframework.http.codec.multipart.FilePart;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileConverter {

    private static final String USER_HOME = "user.home";

    private File file;

    public FileConverter(String path, String name, String extension) {
        path = System.getProperty(USER_HOME) + path;
        this.file = new File(path + name + extension);
    }

    public File convert(FilePart filePart) {
        filePart.transferTo(this.file);
        return this.file;
    }

    public static byte[] getBytes(File file) {
        byte[] agreement = new byte[0];
        try {
            agreement = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new FileException("FileConverter::getBytes. Error when reading file bytes.");
        }
        return agreement;
    }

}
