package es.upm.miw.betca_tpv_core.infrastructure.api.http_errors;


import es.upm.miw.betca_tpv_core.domain.exceptions.*;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ApiExceptionHandler {

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({
            org.springframework.security.access.AccessDeniedException.class
    })
    @ResponseBody
    public void unauthorizedRequest(Exception exception) {
        LogManager.getLogger(this.getClass()).debug(() -> "Unauthorized: " + exception.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({
            NotFoundException.class
    })
    @ResponseBody
    public ErrorMessage notFoundRequest(Exception exception) {
        return new ErrorMessage(exception, HttpStatus.NOT_FOUND.value());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            BadRequestException.class,
            org.springframework.dao.DuplicateKeyException.class,
            org.springframework.web.bind.support.WebExchangeBindException.class,
            org.springframework.http.converter.HttpMessageNotReadableException.class
    })
    @ResponseBody
    public ErrorMessage badRequest(Exception exception) {
        return new ErrorMessage(exception, HttpStatus.BAD_REQUEST.value());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({
            ConflictException.class
    })
    @ResponseBody
    public ErrorMessage conflict(Exception exception) {
        return new ErrorMessage(exception, HttpStatus.CONFLICT.value());
    }


    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler({
            ForbiddenException.class
    })
    @ResponseBody
    public ErrorMessage forbidden(Exception exception) {
        return new ErrorMessage(exception, HttpStatus.FORBIDDEN.value());
    }

    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    @ExceptionHandler({
            BadGatewayException.class
    })
    @ResponseBody
    public ErrorMessage badGateway(Exception exception) {
        return new ErrorMessage(exception, HttpStatus.BAD_GATEWAY.value());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({
            Exception.class
    })
    @ResponseBody
    public ErrorMessage exception(Exception exception) { // The error must be corrected
        return new ErrorMessage(exception, HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

}
