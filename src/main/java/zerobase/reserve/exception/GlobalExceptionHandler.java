package zerobase.reserve.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import zerobase.reserve.dto.ErrorResponseDto;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateException.class)
    public ErrorResponseDto handleDuplicateException(DuplicateException e){
        log.error("error occurred error message = {}", e.getErrorMessage());

        return ErrorResponseDto.builder()
                .errorCode(e.getErrorCode())
                .errorMessage(e.getErrorMessage())
                .build();
    }

    @ExceptionHandler(LoginException.class)
    public ErrorResponseDto handleLoginException(LoginException e){
        log.error("error occurred error message = {}", e.getErrorMessage());

        return ErrorResponseDto.builder()
                .errorCode(e.getErrorCode())
                .errorMessage(e.getErrorMessage())
                .build();
    }

    @ExceptionHandler(NotExistsException.class)
    public ErrorResponseDto handleNotExistsException(NotExistsException e){
        log.error("error occurred error message = {}", e.getErrorMessage());

        return ErrorResponseDto.builder()
                .errorCode(e.getErrorCode())
                .errorMessage(e.getErrorMessage())
                .build();
    }

    @ExceptionHandler(NotMatchException.class)
    public ErrorResponseDto handleNotMatchException(NotMatchException e){
        log.error("error occurred error message = {}", e.getErrorMessage());

        return ErrorResponseDto.builder()
                .errorCode(e.getErrorCode())
                .errorMessage(e.getErrorMessage())
                .build();
    }

    @ExceptionHandler(InvalidReserveException.class)
    public ErrorResponseDto handleInvalidReserveException(InvalidReserveException e){
        log.error("error occurred error message = {}", e.getErrorMessage());

        return ErrorResponseDto.builder()
                .errorCode(e.getErrorCode())
                .errorMessage(e.getErrorMessage())
                .build();
    }
}
