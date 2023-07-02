package zerobase.reserve.service;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import zerobase.reserve.domain.Address;
import zerobase.reserve.domain.Gender;
import zerobase.reserve.domain.Member;
import zerobase.reserve.domain.Store;
import zerobase.reserve.dto.CreateStoreDto;
import zerobase.reserve.exception.DuplicateException;
import zerobase.reserve.exception.ErrorCode;
import zerobase.reserve.exception.NotExistsException;
import zerobase.reserve.repository.MemberRepository;
import zerobase.reserve.repository.StoreRepository;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Slf4j
class StoreServiceTest {

    @Autowired
    private StoreService storeService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private StoreRepository storeRepository;

    @Test
    @DisplayName("상점 추가 성공")
    void createStore() {
        //given
        Member owner = memberRepository.save(
                Member.builder()
                        .name("kim")
                        .email("sehun5515@naver.com")
                        .password("1234")
                        .phoneNumber("010-0101-0101")
                        .roles(Arrays.asList("ROLE_PARTNER", "ROLE_USER"))
                        .gender(Gender.MALE)
                        .build()
        );

        CreateStoreDto.Response store = storeService.createStore(
                CreateStoreDto.Request.builder()
                        .storeName("참새정")
                        .city("김해시")
                        .legion("경상남도")
                        .street("삼계로")
                        .zipcode("12345")
                        .description("맛있는 밥집")
                        .build(), owner.getEmail()
        );

        // when
        Store resultStore = storeRepository.findById(store.getStoreId())
                .orElseThrow(() -> new NotExistsException(ErrorCode.STORE_NOT_EXISTS));

        // then
        assertThat(resultStore.getStoreName()).isEqualTo(store.getStoreName());
        assertThat(resultStore.getOwner()).isEqualTo(owner);
        assertThat(resultStore.getAddress().getCity()).isEqualTo(store.getCity());
        assertThat(resultStore.getAddress().getLegion()).isEqualTo(store.getLegion());
        assertThat(resultStore.getAddress().getStreet()).isEqualTo(store.getStreet());
        assertThat(resultStore.getAddress().getZipcode()).isEqualTo(store.getZipcode());
        assertThat(resultStore.getDescription()).isEqualTo(store.getDescription());
    }

    @Test
    @DisplayName("상점 추가 실패 - 동일한 이름의 상점 존재")
    void createStoreFailedDuplicateStoreName(){
        //given
        Member owner1 = memberRepository.save(
                Member.builder()
                        .name("kim")
                        .email("sehun5515@naver.com")
                        .password("1234")
                        .phoneNumber("010-0101-0101")
                        .roles(Arrays.asList("ROLE_PARTNER", "ROLE_USER"))
                        .gender(Gender.MALE)
                        .build()
        );

        Member owner2 = memberRepository.save(
                Member.builder()
                        .name("lee")
                        .email("sehun8631@naver.com")
                        .password("1234")
                        .phoneNumber("010-0101-0102")
                        .roles(Arrays.asList("ROLE_PARTNER", "ROLE_USER"))
                        .gender(Gender.MALE)
                        .build()
        );

        CreateStoreDto.Request store1 = CreateStoreDto.Request.builder()
                .storeName("참새정")
                .city("김해시")
                .legion("경상남도")
                .street("삼계로")
                .zipcode("12345")
                .description("맛있는 밥집")
                .build();

        CreateStoreDto.Request store2 = CreateStoreDto.Request.builder()
                .storeName("참새정")
                .city("진영시")
                .legion("경상남도")
                .street("진영로")
                .zipcode("25214")
                .description("맛있는 술집")
                .build();

        //when
        storeService.createStore(store1, owner1.getEmail());
        DuplicateException duplicateException = assertThrows(DuplicateException.class,
                () -> storeService.createStore(store2, owner2.getEmail()));

        //then
        assertThat(duplicateException.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_STORE_NAME);
    }
}