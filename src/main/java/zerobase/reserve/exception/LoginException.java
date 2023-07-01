package zerobase.reserve.exception;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginException extends RuntimeException{

    private ErrorCode errorCode;
    private String errorMessage;

    public LoginException(ErrorCode errorCode){
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
}
