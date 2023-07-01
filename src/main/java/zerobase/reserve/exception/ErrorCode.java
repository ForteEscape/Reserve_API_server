package zerobase.reserve.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    DUPLICATE_STORE_NAME("동일한 상호의 매장이 이미 존재합니다."),
    DUPLICATE_EMAIL("동일한 이메일이 이미 존재합니다."),
    MEMBER_NOT_EXISTS("해당 회원이 존재하지 않습니다."),
    STORE_NOT_EXISTS("해당 매장이 존재하지 않습니다."),
    RESERVE_NOT_EXISTS("해당 매장에 대한 예약이 존재하지 않습니다."),
    PASSWORD_NOT_MATCH("비밀번호가 일치하지 않습니다.");

    private final String description;
}
