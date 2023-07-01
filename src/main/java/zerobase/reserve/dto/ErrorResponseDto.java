package zerobase.reserve.dto;

import lombok.*;
import zerobase.reserve.exception.ErrorCode;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponseDto {

    private ErrorCode errorCode;
    private String errorMessage;
}
