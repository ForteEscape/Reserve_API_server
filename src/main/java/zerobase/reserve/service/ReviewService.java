package zerobase.reserve.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zerobase.reserve.domain.Reserve;
import zerobase.reserve.domain.ReserveStatus;
import zerobase.reserve.domain.Review;
import zerobase.reserve.dto.CreateReviewDto;
import zerobase.reserve.exception.ErrorCode;
import zerobase.reserve.exception.InvalidReviewException;
import zerobase.reserve.exception.NotExistsException;
import zerobase.reserve.exception.NotMatchException;
import zerobase.reserve.repository.ReserveRepository;
import zerobase.reserve.repository.ReviewRepository;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReserveRepository reserveRepository;

    /**
     * 리뷰 생성 - 리뷰는 생성 후 방문이 완료된 예약만 생성이 가능하다. 따라서 다음과 같은 검증을 거쳐야 한다.
     * 1. 해당 예약이 이미 리뷰가 수행되었는지 확인
     * 2. 해당 예약이 방문이 완료되었는지 확인
     * 3. 리뷰 생성에 대한 날짜 제약 - 리뷰 생성 날짜가 방문이 완료된 날로부터 1주일 초과로 진행된 경우에 생성할 수 없다.
     * @param request 리뷰 생성 요청 DTO
     * @return 생성된 리뷰 응답 DTO
     */
    @Transactional
    public CreateReviewDto.Response createReview(CreateReviewDto.Request request, Long reserveId, String memberEmail){
        Reserve reserve = reserveRepository.findById(reserveId)
                .orElseThrow(() -> new NotExistsException(ErrorCode.RESERVE_NOT_EXISTS));

        if (checkIllegalAccess(memberEmail, reserve.getMember().getEmail())){
            throw new NotMatchException(ErrorCode.ILLEGAL_ACCESS);
        }

        if (checkReserveAlreadyReviewed(reserve.getReserveStatus())){
            throw new InvalidReviewException(ErrorCode.ALREADY_REVIEWED);
        }

        if (checkReserveNotComplete(reserve.getReserveStatus())){
            throw new NotMatchException(ErrorCode.RESERVE_NOT_COMPLETE);
        }

        if (checkReserveDateTime(reserve.getLastModifiedDate())){
            throw new InvalidReviewException(ErrorCode.CANNOT_CREATE_REVIEW_FROM_REVIEW);
        }

        reserve.changeReserveStatus(ReserveStatus.REVIEWED);

        return CreateReviewDto.Response.fromEntity(
                reviewRepository.save(
                        Review.builder()
                                .store(reserve.getStore())
                                .member(reserve.getMember())
                                .rating(request.getRating())
                                .reviewContent(request.getReviewContent())
                                .build()
                )
        );
    }

    // 다른 회원의 예약을 조회하는지 검증
    private static boolean checkIllegalAccess(String memberEmail, String reserveEmail) {
        return !reserveEmail.equals(memberEmail);
    }

    // 이미 리뷰된 예약인지 검증
    private static boolean checkReserveAlreadyReviewed(ReserveStatus reserveStatus) {
        return reserveStatus == ReserveStatus.REVIEWED;
    }

    // 리뷰 가능 기간이 지났는지 검증
    private static boolean checkReserveDateTime(LocalDateTime reserveTime) {
        return !LocalDateTime.now().isBefore(reserveTime.plusDays(7));
    }

    // 해당 예약이 리뷰 가능한 상태인지 검증
    private static boolean checkReserveNotComplete(ReserveStatus reserveStatus) {
        return reserveStatus != ReserveStatus.COMPLETE;
    }
}
