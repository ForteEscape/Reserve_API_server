package zerobase.reserve.exception;

import lombok.*;

/**
 * 잘못된 리뷰에 대해 어떤 연산을 수행하려고 하는 경우 발생하는 예외
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvalidReviewException extends RuntimeException{
    private ErrorCode errorCode;
    private String errorMessage;

    public InvalidReviewException(ErrorCode errorCode){
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
}
