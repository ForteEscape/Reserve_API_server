package zerobase.reserve.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import zerobase.reserve.domain.ReserveStatus;
import zerobase.reserve.dto.*;
import zerobase.reserve.exception.ErrorCode;
import zerobase.reserve.exception.InvalidReserveException;
import zerobase.reserve.exception.NotExistsException;
import zerobase.reserve.exception.NotMatchException;
import zerobase.reserve.repository.ReserveRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
@Transactional
class ReserveServiceTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private ReserveService reserveService;
    @Autowired
    private ReserveRepository reserveRepository;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    @DisplayName("가계를 거치지 않고 직접 예약 등록")
    void createReserve() {
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

        CreateReserveDto.Request requestReserveDto = CreateReserveDto.Request.builder()
                .reserveTime("2023-07-02 19:30:00")
                .storeName("참새정")
                .build();

        //when
        CreateReserveDto.Response reserve = reserveService.createReserve(requestReserveDto, userMember.getEmail());

        //then
        assertThat(reserve.getReserveStatus()).isEqualTo(ReserveStatus.VALID);
        assertThat(reserve.getStoreName()).isEqualTo("참새정");
        assertThat(reserve.getMemberEmail()).isEqualTo("sehun8631@naver.com");
        assertThat(reserve.getReserveDateTime()).isEqualTo(LocalDateTime.parse("2023-07-02 19:30:00", formatter));
    }

    @Test
    @DisplayName("예약 직접 생성 실패 - 잘못된 회원 정보가 들어올 때")
    void createReserveFailedMemberNotExists(){
        // given
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

        CreateReserveDto.Request requestReserveDto = CreateReserveDto.Request.builder()
                .reserveTime("2023-07-02 19:30:00")
                .storeName("참새정")
                .build();

        // when

        // then
        NotExistsException notExistsException = assertThrows(NotExistsException.class,
                () -> reserveService.createReserve(requestReserveDto, "sehun8631@kakao.com"));
        assertThat(notExistsException.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_EXISTS);
    }

    @Test
    @DisplayName("예약 직접 생성 실패 - 존재하지 않는 가계에 예약")
    void createReserveFailedStoreNotExists(){
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

        CreateReserveDto.Request requestReserveDto = CreateReserveDto.Request.builder()
                .reserveTime("2023-07-02 19:30:00")
                .storeName("까치정")
                .build();

        // then
        NotExistsException notExistsException = assertThrows(NotExistsException.class,
                () -> reserveService.createReserve(requestReserveDto, "sehun8631@naver.com"));
        assertThat(notExistsException.getErrorCode()).isEqualTo(ErrorCode.STORE_NOT_EXISTS);
    }

    @Test
    @DisplayName("매장 상세 페이지에서 예약 성공")
    void createReserveFromStore() {
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

        CreateReserveDto.StoreRequest requestDto = CreateReserveDto.StoreRequest.builder()
                .reserveTime("2023-07-02 15:00:05")
                .build();

        // when
        CreateReserveDto.Response reserve = reserveService.createReserveFromStore(requestDto, storeDto.getStoreId(), userMember.getEmail());

        //then
        assertThat(reserve.getReserveStatus()).isEqualTo(ReserveStatus.VALID);
        assertThat(reserve.getStoreName()).isEqualTo("참새정");
        assertThat(reserve.getMemberEmail()).isEqualTo("sehun8631@naver.com");
        assertThat(reserve.getReserveDateTime()).isEqualTo(LocalDateTime.parse("2023-07-02 15:00:05", formatter));
    }

    @Test
    @DisplayName("점장에게 들어온 예약 리스트 조회 성공")
    void getOwnerReserveList() {
        // given
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

        MemberDto userMember2 = memberService.createUserMember(
                CreateMemberDto.SignUp.builder()
                        .name("park")
                        .email("sehun5216@naver.com")
                        .password("1234")
                        .gender("Female")
                        .phoneNumber("010-0101-0103")
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

        CreateStoreDto.Response storeDto2 = storeService.createStore(
                CreateStoreDto.Request.builder()
                        .storeName("까치정")
                        .legion("경상남도")
                        .city("김해시")
                        .street("삼계로")
                        .zipcode("12355")
                        .description("맛있는 술집")
                        .build(), ownerMember.getEmail()
        );

        CreateReserveDto.StoreRequest requestDto = CreateReserveDto.StoreRequest.builder()
                .reserveTime("2023-07-02 15:00:05")
                .build();

        CreateReserveDto.StoreRequest requestDto2 = CreateReserveDto.StoreRequest.builder()
                .reserveTime("2023-07-02 18:30:00")
                .build();

        CreateReserveDto.Request requestDto3 = CreateReserveDto.Request.builder()
                .storeName("참새정")
                .reserveTime("2023-07-02 18:50:00")
                .build();
        // when
        CreateReserveDto.Response reserve1 = reserveService.createReserve(requestDto3, userMember2.getEmail());
        CreateReserveDto.Response reserve2 = reserveService.createReserveFromStore(requestDto, storeDto.getStoreId(), userMember.getEmail());
        CreateReserveDto.Response reserve3 = reserveService.createReserveFromStore(requestDto2, storeDto2.getStoreId(), userMember2.getEmail());

        List<CreateReserveDto.Response> reserves = reserveRepository.findByStoreOwnerEmail(ownerMember.getEmail()).stream()
                .map(CreateReserveDto.Response::fromEntity)
                .collect(Collectors.toList());

        // then
        assertThat(reserves.size()).isEqualTo(3);
        assertThat(reserves.get(0)).isEqualTo(reserve1);
        assertThat(reserves.get(1)).isEqualTo(reserve2);
        assertThat(reserves.get(2)).isEqualTo(reserve3);
    }

    @Test
    @DisplayName("점장에게 들어온 예약 단건 조회 성공")
    void getOwnerReserveInfo(){
        // given
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

        CreateReserveDto.StoreRequest requestDto = CreateReserveDto.StoreRequest.builder()
                .reserveTime("2023-07-02 15:00:05")
                .build();



        CreateReserveDto.Response reserve1 = reserveService.createReserveFromStore(requestDto, storeDto.getStoreId(), userMember.getEmail());

        // when
        ReserveDto findReserve = reserveService.getOwnerReserveInfo(reserve1.getId(), ownerMember.getEmail());

        // then
        assertThat(findReserve.getStoreName()).isEqualTo("참새정");
        assertThat(findReserve.getReserveStatus()).isEqualTo(ReserveStatus.VALID);
        assertThat(findReserve.getReserveDateTime()).isEqualTo(LocalDateTime.parse("2023-07-02 15:00:05", formatter));
        assertThat(findReserve.getMemberEmail()).isEqualTo("sehun8631@naver.com");
    }

    @Test
    @DisplayName("예약 단건 조회 실패 - 다른 점장의 예약 조회 시도")
    void getOwnerReserveInfoFailedIllegalAccess(){
        // given
        MemberDto ownerMember = memberService.createPartnerMember(
                CreateMemberDto.SignUp.builder()
                        .name("kim")
                        .email("sehun5515@naver.com")
                        .password("1234")
                        .gender("Male")
                        .phoneNumber("010-0101-0101")
                        .build()
        );

        MemberDto ownerMember2 = memberService.createPartnerMember(
                CreateMemberDto.SignUp.builder()
                        .name("han")
                        .email("sehun1234@naver.com")
                        .password("1234")
                        .gender("Male")
                        .phoneNumber("010-0101-0103")
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

        CreateStoreDto.Response storeDto2 = storeService.createStore(
                CreateStoreDto.Request.builder()
                        .storeName("까치정")
                        .legion("경상남도")
                        .city("김해시")
                        .street("삼계로")
                        .zipcode("12345")
                        .description("맛있는 밥집")
                        .build(), ownerMember2.getEmail()
        );

        CreateReserveDto.StoreRequest requestDto = CreateReserveDto.StoreRequest.builder()
                .reserveTime("2023-07-02 15:00:05")
                .build();

        CreateReserveDto.StoreRequest requestDto2 = CreateReserveDto.StoreRequest.builder()
                .reserveTime("2023-07-02 15:00:05")
                .build();

        CreateReserveDto.Response reserve1 = reserveService.createReserveFromStore(requestDto, storeDto.getStoreId(), userMember.getEmail());
        CreateReserveDto.Response reserve2 = reserveService.createReserveFromStore(requestDto2, storeDto2.getStoreId(), userMember.getEmail());

        // when

        // then
        NotMatchException notMatchException = assertThrows(NotMatchException.class,
                () -> reserveService.getOwnerReserveInfo(reserve2.getId(), ownerMember.getEmail()));

        assertThat(notMatchException.getErrorCode()).isEqualTo(ErrorCode.ILLEGAL_ACCESS);
    }

    @Test
    @DisplayName("점장 예약 단건 조회 실패 - 존재하지 않는 예약 조회")
    void getOwnerReserveInfoFailedReserveNotExists(){
        // given
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

        CreateReserveDto.StoreRequest requestDto = CreateReserveDto.StoreRequest.builder()
                .reserveTime("2023-07-02 15:00:05")
                .build();

        reserveService.createReserveFromStore(requestDto, storeDto.getStoreId(), userMember.getEmail());

        // when

        // then
        NotExistsException notExistsException = assertThrows(NotExistsException.class,
                () -> reserveService.getOwnerReserveInfo(10L, ownerMember.getEmail()));
        assertThat(notExistsException.getErrorCode()).isEqualTo(ErrorCode.RESERVE_NOT_EXISTS);
    }

    @Test
    @DisplayName("예약 취소 성공 - 점장측")
    void cancelUserReserveFromOwner() {
        // given
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

        CreateReserveDto.StoreRequest requestDto = CreateReserveDto.StoreRequest.builder()
                .reserveTime("2023-07-02 15:00:05")
                .build();

        CreateReserveDto.Response reserve = reserveService.createReserveFromStore(requestDto, storeDto.getStoreId(), userMember.getEmail());

        // when
        reserveService.cancelUserReserveFromOwner(reserve.getId(), ownerMember.getEmail());
        ReserveDto canceledReserve = reserveService.getOwnerReserveInfo(reserve.getId(), ownerMember.getEmail());

        // then
        assertThat(canceledReserve.getStoreName()).isEqualTo(reserve.getStoreName());
        assertThat(canceledReserve.getReserveStatus()).isEqualTo(ReserveStatus.CANCEL);
        assertThat(canceledReserve.getMemberEmail()).isEqualTo(reserve.getMemberEmail());
        assertThat(canceledReserve.getReserveDateTime()).isEqualTo(LocalDateTime.parse("2023-07-02 15:00:05", formatter));
    }

    @Test
    @DisplayName("점장 측 예약 취소 실패 - 잘못된 예약 접근")
    void cancelUserReserveFromOwnerFailedIllegalReserveAccess(){
        MemberDto ownerMember = memberService.createPartnerMember(
                CreateMemberDto.SignUp.builder()
                        .name("kim")
                        .email("sehun5515@naver.com")
                        .password("1234")
                        .gender("Male")
                        .phoneNumber("010-0101-0101")
                        .build()
        );

        MemberDto ownerMember2 = memberService.createPartnerMember(
                CreateMemberDto.SignUp.builder()
                        .name("han")
                        .email("sehun1234@naver.com")
                        .password("1234")
                        .gender("Male")
                        .phoneNumber("010-0101-0103")
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

        CreateStoreDto.Response storeDto2 = storeService.createStore(
                CreateStoreDto.Request.builder()
                        .storeName("까치정")
                        .legion("경상남도")
                        .city("김해시")
                        .street("삼계로")
                        .zipcode("12345")
                        .description("맛있는 밥집")
                        .build(), ownerMember2.getEmail()
        );

        CreateReserveDto.StoreRequest requestDto = CreateReserveDto.StoreRequest.builder()
                .reserveTime("2023-07-02 15:00:05")
                .build();

        CreateReserveDto.StoreRequest requestDto2 = CreateReserveDto.StoreRequest.builder()
                .reserveTime("2023-07-02 15:00:05")
                .build();

        CreateReserveDto.Response reserve1 = reserveService.createReserveFromStore(requestDto, storeDto.getStoreId(), userMember.getEmail());

        // when

        // then
        NotMatchException notMatchException = assertThrows(NotMatchException.class,
                () -> reserveService.cancelUserReserveFromOwner(reserve1.getId(), ownerMember2.getEmail()));
        assertThat(notMatchException.getErrorCode()).isEqualTo(ErrorCode.ILLEGAL_ACCESS);
    }

    @Test
    @DisplayName("점장 측 예약 취소 실패 - 존재하지 않는 예약 취소")
    void cancelUserReserveFromOwnerFailedReserveNotExists(){
        // given
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

        CreateReserveDto.StoreRequest requestDto = CreateReserveDto.StoreRequest.builder()
                .reserveTime("2023-07-02 15:00:05")
                .build();

        reserveService.createReserveFromStore(requestDto, storeDto.getStoreId(), userMember.getEmail());

        // when

        // then
        NotExistsException notExistsException = assertThrows(NotExistsException.class,
                () -> reserveService.cancelUserReserveFromOwner(10L, ownerMember.getEmail()));
        assertThat(notExistsException.getErrorCode()).isEqualTo(ErrorCode.RESERVE_NOT_EXISTS);
    }

    @Test
    @DisplayName("예약 리스트 조회 성공 - 회원 측")
    void getReserveListFromUser(){
        // given
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

        CreateStoreDto.Response storeDto2 = storeService.createStore(
                CreateStoreDto.Request.builder()
                        .storeName("까치정")
                        .legion("경상남도")
                        .city("김해시")
                        .street("삼계로")
                        .zipcode("12355")
                        .description("맛있는 술집")
                        .build(), ownerMember.getEmail()
        );

        CreateReserveDto.StoreRequest requestDto = CreateReserveDto.StoreRequest.builder()
                .reserveTime("2023-07-02 18:00:05")
                .build();

        CreateReserveDto.StoreRequest requestDto2 = CreateReserveDto.StoreRequest.builder()
                .reserveTime("2023-07-02 22:00:05")
                .build();

        CreateReserveDto.Response reserve1 = reserveService.createReserveFromStore(requestDto, storeDto.getStoreId(), userMember.getEmail());
        CreateReserveDto.Response reserve2 = reserveService.createReserveFromStore(requestDto2, storeDto2.getStoreId(), userMember.getEmail());

        // when
        List<CreateReserveDto.Response> result = reserveRepository.findByMemberEmail(userMember.getEmail()).stream()
                .map(CreateReserveDto.Response::fromEntity)
                .collect(Collectors.toList());

        // then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0)).isEqualTo(reserve1);
        assertThat(result.get(1)).isEqualTo(reserve2);
    }

    @Test
    @DisplayName("예약 조회 성공 - 회원 측")
    void getReserveFromUser() {
        // given
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

        CreateReserveDto.StoreRequest requestDto = CreateReserveDto.StoreRequest.builder()
                .reserveTime("2023-07-02 15:00:05")
                .build();

        CreateReserveDto.Response reserve = reserveService.createReserveFromStore(requestDto, storeDto.getStoreId(), userMember.getEmail());

        // when
        ReserveDto result = reserveService.getReserveFromUser(reserve.getId(), userMember.getEmail());

        // then
        assertThat(result.getReserveStatus()).isEqualTo(ReserveStatus.VALID);
        assertThat(result.getMemberEmail()).isEqualTo("sehun8631@naver.com");
        assertThat(result.getStoreName()).isEqualTo("참새정");
        assertThat(result.getReserveDateTime()).isEqualTo(LocalDateTime.parse("2023-07-02 15:00:05", formatter));
    }

    @Test
    @DisplayName("회원 예약 단건 조회 실패 - 다른 회원의 예약 조회")
    void getReserveFromUserFailedIllegalReserveAccess(){
        // given
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

        MemberDto userMember2 = memberService.createUserMember(
                CreateMemberDto.SignUp.builder()
                        .name("park")
                        .email("sehun5216@naver.com")
                        .password("1234")
                        .gender("Female")
                        .phoneNumber("010-0101-0103")
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

        CreateReserveDto.StoreRequest requestDto = CreateReserveDto.StoreRequest.builder()
                .reserveTime("2023-07-02 15:00:05")
                .build();

        CreateReserveDto.Response reserve = reserveService.createReserveFromStore(requestDto, storeDto.getStoreId(), userMember2.getEmail());
        // when

        // then
        NotMatchException notMatchException = assertThrows(NotMatchException.class,
                () -> reserveService.getReserveFromUser(reserve.getId(), userMember.getEmail()));
        assertThat(notMatchException.getErrorCode()).isEqualTo(ErrorCode.ILLEGAL_ACCESS);
    }

    @Test
    @DisplayName("회원 예약 단건 조회 실패 - 존재하지 않는 예약 조회")
    void getReserveFromUserFailedReserveNotExists(){
        // given
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

        CreateReserveDto.StoreRequest requestDto = CreateReserveDto.StoreRequest.builder()
                .reserveTime("2023-07-02 15:00:05")
                .build();

        CreateReserveDto.Response reserve = reserveService.createReserveFromStore(requestDto, storeDto.getStoreId(), userMember.getEmail());
        // when

        // then
        NotExistsException notExistsException = assertThrows(NotExistsException.class,
                () -> reserveService.getReserveFromUser(10L, userMember.getEmail()));
        assertThat(notExistsException.getErrorCode()).isEqualTo(ErrorCode.RESERVE_NOT_EXISTS);
    }

    @Test
    @DisplayName("유저 자신이 진행한 예약 취소 성공")
    void cancelUserReserveFromUser() {
        // given
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

        CreateReserveDto.StoreRequest requestDto = CreateReserveDto.StoreRequest.builder()
                .reserveTime("2023-07-02 15:00:05")
                .build();

        CreateReserveDto.Response reserve = reserveService.createReserveFromStore(requestDto, storeDto.getStoreId(), userMember.getEmail());

        // when
        ReserveDto reserveDto = reserveService.cancelUserReserveFromUser(reserve.getId(), userMember.getEmail());

        // then
        assertThat(reserveDto.getReserveStatus()).isEqualTo(ReserveStatus.CANCEL);
        assertThat(reserveDto.getMemberEmail()).isEqualTo("sehun8631@naver.com");
        assertThat(reserveDto.getStoreName()).isEqualTo("참새정");
    }

    @Test
    @DisplayName("유저 자신이 진행한 예약 취소 실패 - 타 유저의 예약 취소 시도")
    void cancelUserReserveFromUserFailedIllegalReserveAccess(){
        // given
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

        MemberDto userMember2 = memberService.createUserMember(
                CreateMemberDto.SignUp.builder()
                        .name("park")
                        .email("sehun5216@naver.com")
                        .password("1234")
                        .gender("Female")
                        .phoneNumber("010-0101-0103")
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

        CreateReserveDto.StoreRequest requestDto = CreateReserveDto.StoreRequest.builder()
                .reserveTime("2023-07-02 15:00:05")
                .build();

        CreateReserveDto.Response reserve = reserveService.createReserveFromStore(requestDto, storeDto.getStoreId(), userMember2.getEmail());

        // when

        // then
        NotMatchException notMatchException = assertThrows(NotMatchException.class,
                () -> reserveService.cancelUserReserveFromUser(reserve.getId(), userMember.getEmail()));
        assertThat(notMatchException.getErrorCode()).isEqualTo(ErrorCode.ILLEGAL_ACCESS);
    }

    @Test
    @DisplayName("유저 자신이 진행한 예약 취소 실패 - 존재하지 않는 예약 접근")
    void cancelUserReserveFromUserFailedReserveNotExists(){
        // given
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

        CreateReserveDto.StoreRequest requestDto = CreateReserveDto.StoreRequest.builder()
                .reserveTime("2023-07-02 15:00:05")
                .build();

        reserveService.createReserveFromStore(requestDto, storeDto.getStoreId(), userMember.getEmail());

        // when

        // then
        NotExistsException notExistsException = assertThrows(NotExistsException.class,
                () -> reserveService.cancelUserReserveFromUser(10L, userMember.getEmail()));
        assertThat(notExistsException.getErrorCode()).isEqualTo(ErrorCode.RESERVE_NOT_EXISTS);
    }

    @Test
    @DisplayName("방문 체크 성공")
    void arriveCheck() {
        // given
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

        String reserveTime = LocalDateTime.now().plusMinutes(15).format(formatter);

        log.info("reserve time = {}", reserveTime);

        CreateReserveDto.StoreRequest requestDto = CreateReserveDto.StoreRequest.builder()
                .reserveTime(reserveTime)
                .build();

        CreateReserveDto.Response reserve = reserveService.createReserveFromStore(requestDto, storeDto.getStoreId(), userMember.getEmail());

        // when
        ReserveDto reserveDto = reserveService.arriveCheck(reserve.getId(), userMember.getEmail());

        // then
        assertThat(reserveDto.getReserveStatus()).isEqualTo(ReserveStatus.COMPLETE);
        assertThat(reserveDto.getStoreName()).isEqualTo("참새정");
        assertThat(reserveDto.getMemberEmail()).isEqualTo("sehun8631@naver.com");
        assertThat(reserveDto.getReserveDateTime()).isEqualTo(LocalDateTime.parse(reserveTime, formatter));
    }

    @Test
    @DisplayName("방문 체크 실패 - 예약 시간보다 10분 일찍 방문하지 못한 경우")
    void arriveCheckFailedReserveNoLongerAvailable(){
        // given
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

        CreateReserveDto.StoreRequest requestDto = CreateReserveDto.StoreRequest.builder()
                .reserveTime("2023-07-02 19:10:00")
                .build();

        CreateReserveDto.Response reserve = reserveService.createReserveFromStore(requestDto, storeDto.getStoreId(), userMember.getEmail());

        // when

        // then
        InvalidReserveException invalidReserveException = assertThrows(InvalidReserveException.class,
                () -> reserveService.arriveCheck(reserve.getId(), userMember.getEmail()));
        assertThat(invalidReserveException.getErrorCode()).isEqualTo(ErrorCode.RESERVE_NO_LONGER_AVAILABLE);
    }

    @Test
    @DisplayName("방문 체크 실패 - 이미 취소된 예약인 경우")
    void arriveCheckFailedReserveAlreadyCanceled(){
        // given
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

        CreateReserveDto.StoreRequest requestDto = CreateReserveDto.StoreRequest.builder()
                .reserveTime("2023-07-02 19:40:00")
                .build();

        CreateReserveDto.Response reserve = reserveService.createReserveFromStore(requestDto, storeDto.getStoreId(), userMember.getEmail());

        // when
        reserveService.cancelUserReserveFromUser(reserve.getId(), userMember.getEmail());

        // then
        InvalidReserveException invalidReserveException = assertThrows(InvalidReserveException.class,
                () -> reserveService.arriveCheck(reserve.getId(), userMember.getEmail()));
        assertThat(invalidReserveException.getErrorCode()).isEqualTo(ErrorCode.RESERVE_CANCELED);
    }

    @Test
    @DisplayName("방문 체크 실패 - 잘못된 예약에 접근")
    void arriveCheckFailedIllegalReserveAccess(){
        // given
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

        MemberDto userMember2 = memberService.createUserMember(
                CreateMemberDto.SignUp.builder()
                        .name("park")
                        .email("sehun5216@naver.com")
                        .password("1234")
                        .gender("Female")
                        .phoneNumber("010-0101-0103")
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

        CreateReserveDto.StoreRequest requestDto = CreateReserveDto.StoreRequest.builder()
                .reserveTime("2023-07-02 15:00:05")
                .build();

        CreateReserveDto.Response reserve = reserveService.createReserveFromStore(requestDto, storeDto.getStoreId(), userMember2.getEmail());

        // when

        // then
        NotMatchException notMatchException = assertThrows(NotMatchException.class,
                () -> reserveService.arriveCheck(reserve.getId(), userMember.getEmail()));
        assertThat(notMatchException.getErrorCode()).isEqualTo(ErrorCode.ILLEGAL_ACCESS);
    }

    @Test
    @DisplayName("방문 체크 실패 - 존재하지 않는 예약에 접근")
    void arriveCheckFailedReserveNotExists(){
        // given
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

        CreateReserveDto.StoreRequest requestDto = CreateReserveDto.StoreRequest.builder()
                .reserveTime("2023-07-02 15:00:05")
                .build();

        reserveService.createReserveFromStore(requestDto, storeDto.getStoreId(), userMember.getEmail());

        // when

        // then
        NotExistsException notExistsException = assertThrows(NotExistsException.class,
                () -> reserveService.arriveCheck(10L, userMember.getEmail()));
        assertThat(notExistsException.getErrorCode()).isEqualTo(ErrorCode.RESERVE_NOT_EXISTS);
    }
}