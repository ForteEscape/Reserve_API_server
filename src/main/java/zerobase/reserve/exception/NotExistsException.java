package zerobase.reserve.exception;

import lombok.*;

/**
 * 찾는 데이터가 DB 또는 영속성 컨텍스트에 존재하고 있지 않는 경우 발생하는 예외
 */
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
