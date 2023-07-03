package zerobase.reserve.exception;

import lombok.*;

/**
 * 저장할 엔티티의 유니크 속성이 DB 또는 영속성 컨텍스트에 이미 존재하고 있는 경우 발생시키는 예외
 */
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
