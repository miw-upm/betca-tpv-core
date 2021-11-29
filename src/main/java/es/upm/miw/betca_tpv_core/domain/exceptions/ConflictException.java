package es.upm.miw.betca_tpv_core.domain.exceptions;

public class ConflictException extends RuntimeException {
    private static final String DESCRIPTION = "Conflict Exception";

    public ConflictException(String detail) {
        super(DESCRIPTION + ". " + detail);
    }

}
