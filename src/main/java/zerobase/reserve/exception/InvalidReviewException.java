package zerobase.reserve.exception;

import lombok.*;

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
