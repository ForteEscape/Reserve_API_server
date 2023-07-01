package zerobase.reserve;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import zerobase.reserve.domain.*;
import zerobase.reserve.repository.MemberRepository;
import zerobase.reserve.repository.ReserveRepository;
import zerobase.reserve.repository.StoreRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@SpringBootTest
@Slf4j
@Transactional
public class UpdateTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private ReserveRepository reserveRepository;

    @Test
    void updateTest(){
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

        Member findMember = memberRepository.findById(owner.getId())
                .orElseThrow(NoSuchElementException::new);
        List<Store> originalData = storeRepository.findByMemberId(findMember.getId());
        List<Reserve> originalReserveData = reserveRepository.findByStoreOwnerEmail(member.getEmail());

        for (Store element : originalData) {
            log.info("store data : store name = {}, store address = {}, owner name = {}"
                    , element.getStoreName(), element.getAddress(), element.getOwner().getName());
        }

        for (Reserve element : originalReserveData) {
            log.info("reserve data : store name = {}, reservedDate = {}, owner name = {}"
                    , element.getStore().getStoreName(), element.getReserveTime(), element.getStore().getOwner().getName());
        }

        findMember.changeMemberInfo("John", findMember.getPhoneNumber(), findMember.getGender());

        Member modifyMember = memberRepository.findById(owner.getId())
                .orElseThrow(NoSuchElementException::new);
        List<Store> resultData = storeRepository.findByMemberId(modifyMember.getId());
        List<Reserve> resultReserveData = reserveRepository.findByStoreOwnerEmail(modifyMember.getEmail());

        for (Store element : resultData) {
            log.info("store data : store name = {}, store address = {}, owner name = {}"
                    , element.getStoreName(), element.getAddress(), element.getOwner().getName());
        }

        for (Reserve element : resultReserveData) {
            log.info("reserve data : store name = {}, reservedDate = {}, owner name = {}"
                    , element.getStore().getStoreName(), element.getReserveTime(), element.getStore().getOwner().getName());
        }

        Assertions.assertThat(resultData.size()).isEqualTo(2);
        Assertions.assertThat(resultData).containsExactly(store1, store2);
        Assertions.assertThat(resultReserveData.size()).isEqualTo(4);
        Assertions.assertThat(resultReserveData).containsExactly(
                reserve, reserve2, reserve3, reserve4
        );
    }
}
