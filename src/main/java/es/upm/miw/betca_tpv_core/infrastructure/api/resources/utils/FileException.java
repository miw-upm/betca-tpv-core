package es.upm.miw.betca_tpv_core.infrastructure.api.resources.utils;

public class FileException extends RuntimeException {

    private static final String DESCRIPTION = "File exception";

    public FileException(String detail) {
        super(DESCRIPTION + ". " + detail);
    }

}
