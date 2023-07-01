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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Slf4j
class ReserveRepositoryTest {

    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ReserveRepository reserveRepository;

    @Test
    @DisplayName("예약 데이터 저장")
    void save() {
        //given
        Member owner = memberRepository.save(
                Member.builder()
                        .name("kim")
                        .gender(Gender.MALE.getDescription())
                        .phoneNumber("010-0101-0101")
                        .build()
        );

        Member member = memberRepository.save(
                Member.builder()
                        .name("lee")
                        .gender(Gender.MALE.getDescription())
                        .phoneNumber("010-0101-0102")
                        .build()
        );

        Store store = storeRepository.save(
                Store.builder()
                        .storeName("storeA")
                        .address(new Address("경상남도", "김해시", "삼계로", "50898"))
                        .owner(owner)
                        .description("라면가계")
                        .build()
        );

        Reserve reserve = reserveRepository.save(
                Reserve.builder()
                        .member(member)
                        .reserveTime(LocalDateTime.now())
                        .store(store)
                        .reserveStatus(ReserveStatus.VALID)
                        .build()
        );

        //when
        Reserve findReserve = reserveRepository.findById(reserve.getId()).get();

        // then
        assertThat(findReserve).isEqualTo(reserve);
    }

    @Test
    @DisplayName("매점 id를 통한 예약 조회 - 점장용")
    void findByStoreId() {
        // given
        Member owner = memberRepository.save(
                Member.builder()
                        .name("kim")
                        .gender(Gender.MALE.getDescription())
                        .phoneNumber("010-0101-0101")
                        .build()
        );

        Member member = memberRepository.save(
                Member.builder()
                        .name("lee")
                        .gender(Gender.MALE.getDescription())
                        .phoneNumber("010-0101-0102")
                        .build()
        );

        Member member2 = memberRepository.save(
                Member.builder()
                        .name("park")
                        .gender(Gender.FEMALE.getDescription())
                        .phoneNumber("010-0101-0103")
                        .build()
        );

        Store store1 = storeRepository.save(
                Store.builder()
                        .storeName("storeA")
                        .address(new Address("경상남도", "김해시", "삼계로", "50898"))
                        .owner(owner)
                        .description("라면가계")
                        .build()
        );

        Store store2 = storeRepository.save(
                Store.builder()
                        .storeName("storeB")
                        .address(new Address("경상남도", "김해시", "삼계로", "51234"))
                        .owner(owner)
                        .description("일식집")
                        .build()
        );

        Reserve reserve = reserveRepository.save(
                Reserve.builder()
                        .member(member)
                        .reserveTime(LocalDateTime.now())
                        .store(store1)
                        .reserveStatus(ReserveStatus.VALID)
                        .build()
        );

        Reserve reserve2 = reserveRepository.save(
                Reserve.builder()
                        .member(member2)
                        .reserveTime(LocalDateTime.now())
                        .store(store1)
                        .reserveStatus(ReserveStatus.VALID)
                        .build()
        );

        Reserve reserve3 = reserveRepository.save(
                Reserve.builder()
                        .member(member2)
                        .reserveTime(LocalDateTime.now())
                        .store(store2)
                        .reserveStatus(ReserveStatus.VALID)
                        .build()
        );

        Reserve reserve4 = reserveRepository.save(
                Reserve.builder()
                        .member(member)
                        .reserveTime(LocalDateTime.now())
                        .store(store2)
                        .reserveStatus(ReserveStatus.VALID)
                        .build()
        );

        // when
        List<Reserve> store1ReserveList = reserveRepository.findByStoreId(store1.getId());
        List<Reserve> store2ReserveList = reserveRepository.findByStoreId(store2.getId());

        // then
        assertThat(store1ReserveList.size()).isEqualTo(2);
        assertThat(store1ReserveList).containsExactly(reserve, reserve2);
        assertThat(store2ReserveList.size()).isEqualTo(2);
        assertThat(store2ReserveList).containsExactly(reserve3, reserve4);
    }

    @Test
    @DisplayName("점장 id를 통한 예약 조회 - 점장용")
    void findByStoreOwnerId() {
        // given
        Member owner = memberRepository.save(
                Member.builder()
                        .name("kim")
                        .gender(Gender.MALE.getDescription())
                        .phoneNumber("010-0101-0101")
                        .build()
        );

        Member member = memberRepository.save(
                Member.builder()
                        .name("lee")
                        .gender(Gender.MALE.getDescription())
                        .phoneNumber("010-0101-0102")
                        .build()
        );

        Member member2 = memberRepository.save(
                Member.builder()
                        .name("park")
                        .gender(Gender.FEMALE.getDescription())
                        .phoneNumber("010-0101-0103")
                        .build()
        );

        Store store1 = storeRepository.save(
                Store.builder()
                        .storeName("storeA")
                        .address(new Address("경상남도", "김해시", "삼계로", "50898"))
                        .owner(owner)
                        .description("라면가계")
                        .build()
        );

        Store store2 = storeRepository.save(
                Store.builder()
                        .storeName("storeB")
                        .address(new Address("경상남도", "김해시", "삼계로", "51234"))
                        .owner(owner)
                        .description("일식집")
                        .build()
        );

        Reserve reserve = reserveRepository.save(
                Reserve.builder()
                        .member(member)
                        .reserveTime(LocalDateTime.now())
                        .store(store1)
                        .reserveStatus(ReserveStatus.VALID)
                        .build()
        );

        Reserve reserve2 = reserveRepository.save(
                Reserve.builder()
                        .member(member2)
                        .reserveTime(LocalDateTime.now())
                        .store(store1)
                        .reserveStatus(ReserveStatus.VALID)
                        .build()
        );

        Reserve reserve3 = reserveRepository.save(
                Reserve.builder()
                        .member(member2)
                        .reserveTime(LocalDateTime.now())
                        .store(store2)
                        .reserveStatus(ReserveStatus.VALID)
                        .build()
        );

        Reserve reserve4 = reserveRepository.save(
                Reserve.builder()
                        .member(member)
                        .reserveTime(LocalDateTime.now())
                        .store(store2)
                        .reserveStatus(ReserveStatus.VALID)
                        .build()
        );

        // when
        List<Reserve> ownerReserveList = reserveRepository.findByStoreOwnerEmail(owner.getEmail());

        // then

        for (Reserve data : ownerReserveList) {
            log.info("reserve store = {}, reserve member = {}",
                    data.getStore().getStoreName(), data.getMember().getName());
        }

        assertThat(ownerReserveList.size()).isEqualTo(4);
        assertThat(ownerReserveList).containsExactly(reserve, reserve2, reserve3, reserve4);
    }

    @Test
    @DisplayName("회원 id를 통한 예약 조회 - 회원용")
    void findByMemberId() {
        // given
        Member owner = memberRepository.save(
                Member.builder()
                        .name("kim")
                        .gender(Gender.MALE.getDescription())
                        .phoneNumber("010-0101-0101")
                        .build()
        );

        Member member = memberRepository.save(
                Member.builder()
                        .name("lee")
                        .gender(Gender.MALE.getDescription())
                        .phoneNumber("010-0101-0102")
                        .build()
        );

        Member member2 = memberRepository.save(
                Member.builder()
                        .name("park")
                        .gender(Gender.FEMALE.getDescription())
                        .phoneNumber("010-0101-0103")
                        .build()
        );

        Store store1 = storeRepository.save(
                Store.builder()
                        .storeName("storeA")
                        .address(new Address("경상남도", "김해시", "삼계로", "50898"))
                        .owner(owner)
                        .description("라면가계")
                        .build()
        );

        Store store2 = storeRepository.save(
                Store.builder()
                        .storeName("storeB")
                        .address(new Address("경상남도", "김해시", "삼계로", "51234"))
                        .owner(owner)
                        .description("일식집")
                        .build()
        );

        Reserve reserve = reserveRepository.save(
                Reserve.builder()
                        .member(member)
                        .reserveTime(LocalDateTime.now())
                        .store(store1)
                        .reserveStatus(ReserveStatus.VALID)
                        .build()
        );

        Reserve reserve2 = reserveRepository.save(
                Reserve.builder()
                        .member(member2)
                        .reserveTime(LocalDateTime.now())
                        .store(store1)
                        .reserveStatus(ReserveStatus.VALID)
                        .build()
        );

        Reserve reserve3 = reserveRepository.save(
                Reserve.builder()
                        .member(member2)
                        .reserveTime(LocalDateTime.now())
                        .store(store2)
                        .reserveStatus(ReserveStatus.VALID)
                        .build()
        );

        Reserve reserve4 = reserveRepository.save(
                Reserve.builder()
                        .member(member)
                        .reserveTime(LocalDateTime.now())
                        .store(store2)
                        .reserveStatus(ReserveStatus.VALID)
                        .build()
        );

        // when
        List<Reserve> memberReserveList = reserveRepository.findByMemberId(member.getId());
        List<Reserve> member2ReserveList = reserveRepository.findByMemberId(member2.getId());

        // then
        assertThat(memberReserveList.size()).isEqualTo(2);
        assertThat(memberReserveList).containsExactly(reserve, reserve4);
        assertThat(member2ReserveList.size()).isEqualTo(2);
        assertThat(member2ReserveList).containsExactly(reserve2, reserve3);
    }

    @Test
    @DisplayName("회원 도착 시 매장의 이름과 회원 이메일을 통해 예약 조회")
    void findByStoreNameAndMemberEmailTest(){
        // given
        Member owner = memberRepository.save(
                Member.builder()
                        .name("kim")
                        .email("sehun5515@naver.com")
                        .password("1234")
                        .gender(Gender.MALE.getDescription())
                        .phoneNumber("010-0101-0101")
                        .build()
        );

        Member member = memberRepository.save(
                Member.builder()
                        .name("lee")
                        .email("sehun8631@naver.com")
                        .password("1234")
                        .gender(Gender.MALE.getDescription())
                        .phoneNumber("010-0101-0102")
                        .build()
        );

        Member member2 = memberRepository.save(
                Member.builder()
                        .name("park")
                        .email("sehun5216@naver.com")
                        .password("1234")
                        .gender(Gender.FEMALE.getDescription())
                        .phoneNumber("010-0101-0103")
                        .build()
        );

        Store store1 = storeRepository.save(
                Store.builder()
                        .storeName("storeA")
                        .address(new Address("경상남도", "김해시", "삼계로", "50898"))
                        .owner(owner)
                        .description("라면가계")
                        .build()
        );

        Store store2 = storeRepository.save(
                Store.builder()
                        .storeName("storeB")
                        .address(new Address("경상남도", "김해시", "삼계로", "51234"))
                        .owner(owner)
                        .description("일식집")
                        .build()
        );

        Reserve reserve = reserveRepository.save(
                Reserve.builder()
                        .member(member)
                        .reserveTime(LocalDateTime.now().plusMinutes(30))
                        .store(store1)
                        .reserveStatus(ReserveStatus.VALID)
                        .build()
        );

        Reserve reserve2 = reserveRepository.save(
                Reserve.builder()
                        .member(member)
                        .reserveTime(LocalDateTime.now().plusMinutes(50))
                        .store(store2)
                        .reserveStatus(ReserveStatus.VALID)
                        .build()
        );

        // when
        Reserve result = reserveRepository.findByStoreNameAndMemberEmail(store2.getStoreName(), member.getEmail())
                .orElseThrow(() -> new NotExistsException(ErrorCode.RESERVE_NOT_EXISTS));

        // then
        assertThat(result).isEqualTo(reserve2);
    }
}