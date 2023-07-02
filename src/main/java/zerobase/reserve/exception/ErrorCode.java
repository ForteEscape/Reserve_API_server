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
    FIELD_MUST_NOT_EMPTY("해당 부분은 필수적으로 기입해주셔야 합니다."),
    REVIEW_NOT_EXISTS("해당 리뷰는 존재하지 않는 리뷰입니다."),
    RESERVE_NOT_COMPLETE("방문이 완료된 예약만 리뷰 생성이 가능합니다."),
    CANNOT_CREATE_REVIEW_FROM_REVIEW("예약한지 1주일 전까지만 리뷰를 작성할 수 있습니다."),
    ALREADY_REVIEWED("이미 리뷰한 예약을 다시 리뷰할 수 없습니다."),
    ILLEGAL_ACCESS("잘못된 접근입니다.");

    private final String description;
}
