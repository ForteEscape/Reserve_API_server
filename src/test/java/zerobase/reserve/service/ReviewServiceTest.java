package zerobase.reserve.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import zerobase.reserve.domain.Reserve;
import zerobase.reserve.domain.ReserveStatus;
import zerobase.reserve.domain.Review;
import zerobase.reserve.dto.*;
import zerobase.reserve.exception.ErrorCode;
import zerobase.reserve.exception.InvalidReviewException;
import zerobase.reserve.exception.NotExistsException;
import zerobase.reserve.exception.NotMatchException;
import zerobase.reserve.repository.ReserveRepository;
import zerobase.reserve.repository.ReviewRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
@Transactional
class ReviewServiceTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private ReserveService reserveService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ReserveRepository reserveRepository;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    @DisplayName("리뷰 생성 성공")
    void createReview() {
        //given
        MemberDto ownerMember = memberService.createPartnerMember(
                CreateMemberDto.SignUp.builder()
                        .name("kim")
                        .email("sehun5515@naver.com")
                        .password("1234")
                        .gender("Male")
                        .phoneNumber("010-0101-0101")
                        .build()
        );

        MemberDto userMember = memberService.createUserMember(
                CreateMemberDto.SignUp.builder()
                        .name("lee")
                        .email("sehun8631@naver.com")
                        .password("1234")
                        .gender("Male")
                        .phoneNumber("010-0101-0102")
                        .build()
        );

        CreateStoreDto.Response storeDto = storeService.createStore(
                CreateStoreDto.Request.builder()
                        .storeName("참새정")
                        .legion("경상남도")
                        .city("김해시")
                        .street("삼계로")
                        .zipcode("12345")
                        .description("맛있는 밥집")
                        .build(), ownerMember.getEmail()
        );

        CreateReserveDto.Response reserve = reserveService.createReserveFromStore(
                CreateReserveDto.StoreRequest.builder()
                        .reserveTime(LocalDateTime.now().plusMinutes(15).format(formatter))
                        .build(), storeDto.getStoreId(), userMember.getEmail()
        );

        ReserveDto reserveDto = reserveService.arriveCheck(reserve.getId(), userMember.getEmail());
        log.info("reserve status = {}", reserveDto.getReserveStatus());

        CreateReviewDto.Response review = reviewService.createReview(
                CreateReviewDto.Request.builder()
                        .rating(4)
                        .reviewContent("맛있는 밥집이었습니다.")
                        .build(), reserveDto.getId(), userMember.getEmail()
        );

        // when
        Review findReview = reviewRepository.findById(review.getId())
                .orElseThrow(() -> new NotExistsException(ErrorCode.REVIEW_NOT_EXISTS));

        Reserve findReserve = reserveRepository.findById(reserveDto.getId())
                .orElseThrow(() -> new NotExistsException(ErrorCode.RESERVE_NOT_EXISTS));

        // then
        assertThat(findReview.getRating()).isEqualTo(4);
        assertThat(findReview.getReviewContent()).isEqualTo("맛있는 밥집이었습니다.");
        assertThat(findReview.getStore().getStoreName()).isEqualTo("참새정");
        assertThat(findReview.getMember().getEmail()).isEqualTo("sehun8631@naver.com");
        assertThat(findReserve.getReserveStatus()).isEqualTo(ReserveStatus.REVIEWED);
    }

    @Test
    @DisplayName("리뷰 생성 실패 - 방문하지 않은 예약에서 리뷰 작성 시도")
    void createReviewFailedReserveStatusNotMatched(){
        //given
        MemberDto ownerMember = memberService.createPartnerMember(
                CreateMemberDto.SignUp.builder()
                        .name("kim")
                        .email("sehun5515@naver.com")
                        .password("1234")
                        .gender("Male")
                        .phoneNumber("010-0101-0101")
                        .build()
        );

        MemberDto userMember = memberService.createUserMember(
                CreateMemberDto.SignUp.builder()
                        .name("lee")
                        .email("sehun8631@naver.com")
                        .password("1234")
                        .gender("Male")
                        .phoneNumber("010-0101-0102")
                        .build()
        );

        CreateStoreDto.Response storeDto = storeService.createStore(
                CreateStoreDto.Request.builder()
                        .storeName("참새정")
                        .legion("경상남도")
                        .city("김해시")
                        .street("삼계로")
                        .zipcode("12345")
                        .description("맛있는 밥집")
                        .build(), ownerMember.getEmail()
        );

        CreateReserveDto.Response reserve = reserveService.createReserveFromStore(
                CreateReserveDto.StoreRequest.builder()
                        .reserveTime(LocalDateTime.now().plusMinutes(15).format(formatter))
                        .build(), storeDto.getStoreId(), userMember.getEmail()
        );

        CreateReviewDto.Request reviewRequest = CreateReviewDto.Request.builder()
                .rating(4)
                .reviewContent("맛있는 밥집이었습니다.")
                .build();

        // then
        NotMatchException notMatchException = assertThrows(NotMatchException.class,
                () -> reviewService.createReview(reviewRequest, reserve.getId(), userMember.getEmail()));
        assertThat(notMatchException.getErrorCode()).isEqualTo(ErrorCode.RESERVE_NOT_COMPLETE);
    }

    @Test
    @DisplayName("리뷰 생성 실패 - 이미 리뷰를 수행한 예약에서 다시 리뷰")
    void createReviewFailedAlreadyReviewed(){
        //given
        MemberDto ownerMember = memberService.createPartnerMember(
                CreateMemberDto.SignUp.builder()
                        .name("kim")
                        .email("sehun5515@naver.com")
                        .password("1234")
                        .gender("Male")
                        .phoneNumber("010-0101-0101")
                        .build()
        );

        MemberDto userMember = memberService.createUserMember(
                CreateMemberDto.SignUp.builder()
                        .name("lee")
                        .email("sehun8631@naver.com")
                        .password("1234")
                        .gender("Male")
                        .phoneNumber("010-0101-0102")
                        .build()
        );

        CreateStoreDto.Response storeDto = storeService.createStore(
                CreateStoreDto.Request.builder()
                        .storeName("참새정")
                        .legion("경상남도")
                        .city("김해시")
                        .street("삼계로")
                        .zipcode("12345")
                        .description("맛있는 밥집")
                        .build(), ownerMember.getEmail()
        );

        CreateReserveDto.Response reserve = reserveService.createReserveFromStore(
                CreateReserveDto.StoreRequest.builder()
                        .reserveTime(LocalDateTime.now().plusMinutes(15).format(formatter))
                        .build(), storeDto.getStoreId(), userMember.getEmail()
        );

        reserveService.arriveCheck(reserve.getId(), userMember.getEmail());

        CreateReviewDto.Request reviewRequest = CreateReviewDto.Request.builder()
                .rating(4)
                .reviewContent("맛있는 밥집이었습니다.")
                .build();

        CreateReviewDto.Request reviewRequest2 = CreateReviewDto.Request.builder()
                .rating(5)
                .reviewContent("맛있는 밥집이었습니다.")
                .build();

        // then
        reviewService.createReview(reviewRequest, reserve.getId(), userMember.getEmail());

        InvalidReviewException invalidReviewException = assertThrows(InvalidReviewException.class,
                () -> reviewService.createReview(reviewRequest2, reserve.getId(), userMember.getEmail()));
        assertThat(invalidReviewException.getErrorCode()).isEqualTo(ErrorCode.ALREADY_REVIEWED);
    }
}