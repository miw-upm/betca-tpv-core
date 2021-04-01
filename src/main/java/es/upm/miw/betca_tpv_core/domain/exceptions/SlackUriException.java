package es.upm.miw.betca_tpv_core.domain.exceptions;

public class SlackUriException extends RuntimeException{
    private static final String DESCRIPTION = "Slack URI error";

    public SlackUriException(String detail) {
        super(DESCRIPTION + ". " + detail);
    }
}
