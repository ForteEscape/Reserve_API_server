package zerobase.reserve.exception;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DuplicateException extends RuntimeException{
    private ErrorCode errorCode;
    private String errorMessage;

    public DuplicateException(ErrorCode errorCode){
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
}
