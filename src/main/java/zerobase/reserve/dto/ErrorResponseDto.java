package zerobase.reserve.dto;

import lombok.*;
import zerobase.reserve.exception.ErrorCode;

/**
 * 예외 반환 규격 DTO
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponseDto {

    private ErrorCode errorCode;
    private String errorMessage;
}
