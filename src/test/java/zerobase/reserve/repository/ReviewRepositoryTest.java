package zerobase.reserve.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import zerobase.reserve.domain.*;
import zerobase.reserve.exception.ErrorCode;
import zerobase.reserve.exception.NotExistsException;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Slf4j
@Transactional
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private StoreRepository storeRepository;

    @Test
    @DisplayName("리뷰 저장")
    void save() {
        // given
        Member ownerMember = memberRepository.save(
                Member.builder()
                        .name("kim")
                        .email("sehun5515@naver.com")
                        .password("1234")
                        .gender(Gender.MALE)
                        .roles(Arrays.asList("ROLE_PARTNER", "ROLE_USER"))
                        .phoneNumber("010-0101-0101")
                        .build()
        );

        Member userMember = memberRepository.save(
                Member.builder()
                        .name("lee")
                        .email("sehun8631@naver.com")
                        .password("1234")
                        .gender(Gender.MALE)
                        .roles(List.of("ROLE_USER"))
                        .phoneNumber("010-0101-0102")
                        .build()
        );

        Store store1 = storeRepository.save(
                Store.builder()
                        .storeName("참새정")
                        .address(new Address("경상남도", "김해시", "삼계로", "12345"))
                        .description("맛있는 밥집")
                        .owner(ownerMember)
                        .build()
        );

        Review review = reviewRepository.save(
                Review.builder()
                        .rating(5)
                        .store(store1)
                        .member(userMember)
                        .reviewContent("매우 맛있는 밥집입니다.")
                        .build()
        );

        // when
        Review findReview = reviewRepository.findById(review.getId())
                .orElseThrow(() -> new NotExistsException(ErrorCode.REVIEW_NOT_EXISTS));

        // then
        assertThat(findReview).isEqualTo(review);
    }

    @Test
    @DisplayName("매점 이름으로 리뷰 조회")
    void findByStoreName() {
        // given
        Member ownerMember = memberRepository.save(
                Member.builder()
                        .name("kim")
                        .email("sehun5515@naver.com")
                        .password("1234")
                        .gender(Gender.MALE)
                        .roles(Arrays.asList("ROLE_PARTNER", "ROLE_USER"))
                        .phoneNumber("010-0101-0101")
                        .build()
        );

        Member userMember = memberRepository.save(
                Member.builder()
                        .name("lee")
                        .email("sehun8631@naver.com")
                        .password("1234")
                        .gender(Gender.MALE)
                        .roles(List.of("ROLE_USER"))
                        .phoneNumber("010-0101-0102")
                        .build()
        );

        Member userMember2 = memberRepository.save(
                Member.builder()
                        .name("park")
                        .email("sehun5216@naver.com")
                        .password("1234")
                        .gender(Gender.FEMALE)
                        .roles(List.of("ROLE_USER"))
                        .phoneNumber("010-0101-0103")
                        .build()
        );

        Member userMember3 = memberRepository.save(
                Member.builder()
                        .name("song")
                        .email("sehun1234@naver.com")
                        .password("1234")
                        .gender(Gender.MALE)
                        .roles(List.of("ROLE_USER"))
                        .phoneNumber("010-0101-0104")
                        .build()
        );

        Store store1 = storeRepository.save(
                Store.builder()
                        .storeName("참새정")
                        .address(new Address("경상남도", "김해시", "삼계로", "12345"))
                        .description("맛있는 밥집")
                        .owner(ownerMember)
                        .build()
        );

        Review review = reviewRepository.save(
                Review.builder()
                        .rating(5)
                        .store(store1)
                        .member(userMember)
                        .reviewContent("매우 맛있는 밥집입니다.")
                        .build()
        );

        Review review2 = reviewRepository.save(
                Review.builder()
                        .rating(3)
                        .store(store1)
                        .member(userMember2)
                        .reviewContent("매우 맛있었지만 서비스가 조금 아쉬웠습니다.")
                        .build()
        );

        Review review3 = reviewRepository.save(
                Review.builder()
                        .rating(4)
                        .store(store1)
                        .member(userMember3)
                        .reviewContent("괜찮은 밥집이었습니다.")
                        .build()
        );

        // when
        List<Review> result = reviewRepository.findByStoreId(store1.getId());

        // then
        assertThat(result.size()).isEqualTo(3);
        assertThat(result).containsExactly(review, review2, review3);
    }

    @Test
    @DisplayName("유저의 계정으로 리뷰 조회")
    void findByUserEmail() {
        // given
        Member ownerMember = memberRepository.save(
                Member.builder()
                        .name("kim")
                        .email("sehun5515@naver.com")
                        .password("1234")
                        .gender(Gender.MALE)
                        .roles(Arrays.asList("ROLE_PARTNER", "ROLE_USER"))
                        .phoneNumber("010-0101-0101")
                        .build()
        );

        Member userMember = memberRepository.save(
                Member.builder()
                        .name("lee")
                        .email("sehun8631@naver.com")
                        .password("1234")
                        .gender(Gender.MALE)
                        .roles(List.of("ROLE_USER"))
                        .phoneNumber("010-0101-0102")
                        .build()
        );

        Store store1 = storeRepository.save(
                Store.builder()
                        .storeName("참새정")
                        .address(new Address("경상남도", "김해시", "삼계로", "12345"))
                        .description("맛있는 밥집")
                        .owner(ownerMember)
                        .build()
        );

        Review review = reviewRepository.save(
                Review.builder()
                        .rating(5)
                        .store(store1)
                        .member(userMember)
                        .reviewContent("매우 맛있는 밥집입니다.")
                        .build()
        );

        Review review2 = reviewRepository.save(
                Review.builder()
                        .rating(3)
                        .store(store1)
                        .member(userMember)
                        .reviewContent("매우 맛있었지만 서비스가 조금 아쉬웠습니다.")
                        .build()
        );

        Review review3 = reviewRepository.save(
                Review.builder()
                        .rating(4)
                        .store(store1)
                        .member(userMember)
                        .reviewContent("괜찮은 밥집이었습니다.")
                        .build()
        );

        // when
        List<Review> result = reviewRepository.findByUserEmail(userMember.getEmail());

        // then
        assertThat(result.size()).isEqualTo(3);
        assertThat(result).containsExactly(review, review2, review3);
    }

    @Test
    @DisplayName("점장이 가진 가계들에 대한 리뷰 조회")
    void findByOwnerEmail(){
        // given
        Member ownerMember = memberRepository.save(
                Member.builder()
                        .name("kim")
                        .email("sehun5515@naver.com")
                        .password("1234")
                        .gender(Gender.MALE)
                        .roles(Arrays.asList("ROLE_PARTNER", "ROLE_USER"))
                        .phoneNumber("010-0101-0101")
                        .build()
        );

        Member userMember = memberRepository.save(
                Member.builder()
                        .name("lee")
                        .email("sehun8631@naver.com")
                        .password("1234")
                        .gender(Gender.MALE)
                        .roles(List.of("ROLE_USER"))
                        .phoneNumber("010-0101-0102")
                        .build()
        );

        Store store1 = storeRepository.save(
                Store.builder()
                        .storeName("참새정")
                        .address(new Address("경상남도", "김해시", "삼계로", "12345"))
                        .description("맛있는 밥집")
                        .owner(ownerMember)
                        .build()
        );

        Store store2 = storeRepository.save(
                Store.builder()
                        .storeName("까치정")
                        .address(new Address("경상남도", "김해시", "삼계로", "12355"))
                        .description("맛있는 술집")
                        .owner(ownerMember)
                        .build()
        );

        Review review = reviewRepository.save(
                Review.builder()
                        .rating(5)
                        .store(store1)
                        .member(userMember)
                        .reviewContent("매우 맛있는 밥집입니다.")
                        .build()
        );

        Review review2 = reviewRepository.save(
                Review.builder()
                        .rating(3)
                        .store(store2)
                        .member(userMember)
                        .reviewContent("매우 맛있었지만 서비스가 조금 아쉬웠습니다.")
                        .build()
        );

        Review review3 = reviewRepository.save(
                Review.builder()
                        .rating(4)
                        .store(store1)
                        .member(userMember)
                        .reviewContent("괜찮은 밥집이었습니다.")
                        .build()
        );

        // when
        List<Review> result = reviewRepository.findByOwnerEmail(ownerMember.getEmail());

        // then
        assertThat(result.size()).isEqualTo(3);
        assertThat(result).containsExactly(review, review2, review3);
    }
}