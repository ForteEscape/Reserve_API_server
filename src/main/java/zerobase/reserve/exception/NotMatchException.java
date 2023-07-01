package zerobase.reserve.exception;

import lombok.*;

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
