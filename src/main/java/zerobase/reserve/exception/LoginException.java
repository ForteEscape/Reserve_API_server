package zerobase.reserve.exception;

import lombok.*;

/**
 * 로그인 실패 시 발생하는 예외
 */
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
