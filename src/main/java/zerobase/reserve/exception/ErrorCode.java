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
    RESERVE_NOT_EXISTS("해당 예약이 존재하지 않습니다."),
    PASSWORD_NOT_MATCH("비밀번호가 일치하지 않습니다."),
    RESERVE_NO_LONGER_AVAILABLE("예약은 10분 전까지 도착해야 합니다. 다음에 다시 와주세요"),
    RESERVE_CANCELED("해당 예약은 취소된 예약입니다."),
    ILLEGAL_ACCESS("잘못된 접근입니다.");

    private final String description;
}
