package zerobase.reserve.exception;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotExistsException extends RuntimeException{
    private ErrorCode errorCode;
    private String errorMessage;

    public NotExistsException(ErrorCode errorCode){
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
}
