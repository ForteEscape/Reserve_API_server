package zerobase.reserve.exception;

import lombok.*;

/**
 * 동일해야하는 데이터가 동일하지 않는 등의 이유로 발생하는 예외
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotMatchException extends RuntimeException{
    private ErrorCode errorCode;
    private String errorMessage;

    public NotMatchException(ErrorCode errorCode){
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
}
