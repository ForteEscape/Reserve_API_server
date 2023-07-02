package zerobase.reserve.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import zerobase.reserve.domain.*;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Slf4j
class StoreRepositoryTest {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("매장 등록 테스트")
    void save() {
        // given
        Member member = memberRepository.save(
                Member.builder()
                        .name("kim")
                        .gender(Gender.MALE)
                        .phoneNumber("010-0101-0101")
                        .build()
        );

        Store store = storeRepository.save(
                Store.builder()
                        .storeName("storeA")
                        .address(new Address("경상남도", "김해시", "삼계로", "50898"))
                        .owner(member)
                        .description("라면가계")
                        .build()
        );

        // when
        Store findStore = storeRepository.findById(store.getId()).get();

        // then
        assertThat(findStore).isEqualTo(store);
    }


    @Test
    @DisplayName("매장 전체 조회 테스트")
    void findAll() {
        // given
        Member member = memberRepository.save(
                Member.builder()
                        .name("kim")
                        .gender(Gender.MALE)
                        .phoneNumber("010-0101-0101")
                        .build()
        );

        Member member2 = memberRepository.save(
                Member.builder()
                        .name("park")
                        .gender(Gender.MALE)
                        .phoneNumber("010-0101-0102")
                        .build()
        );

        Store store1 = storeRepository.save(
                Store.builder()
                        .storeName("storeA")
                        .address(new Address("경상남도", "김해시", "삼계로", "50898"))
                        .owner(member)
                        .description("라면가계")
                        .build()
        );

        Store store2 = storeRepository.save(
                Store.builder()
                        .storeName("storeB")
                        .address(new Address("경상남도", "김해시", "삼계로", "50900"))
                        .owner(member)
                        .description("두부가계")
                        .build()
        );

        Store store3 = storeRepository.save(
                Store.builder()
                        .storeName("storeB")
                        .address(new Address("경상남도", "진영시", "봉하마을", "50523"))
                        .owner(member2)
                        .description("두부가계")
                        .build()
        );

        // when
        List<Store> storeList = storeRepository.findAll();

        // then
        assertThat(storeList.size()).isEqualTo(3);
        assertThat(storeList).containsExactly(store1, store2, store3);

        for (Store store : storeList) {
            log.info("store name = {}, store address = {}, store owner = {}, store description = {}",
                    store.getStoreName(), store.getAddress(), store.getOwner().getName(), store.getDescription());
            log.info("store createdAt = {}", store.getCreatedDate());
        }
    }

    @Test
    @DisplayName("점장 id를 통한 소속 매장 검색")
    void findByMemberId() {
        // given
        Member member = memberRepository.save(
                Member.builder()
                        .name("kim")
                        .gender(Gender.MALE)
                        .phoneNumber("010-0101-0101")
                        .build()
        );

        Member member2 = memberRepository.save(
                Member.builder()
                        .name("park")
                        .gender(Gender.MALE)
                        .phoneNumber("010-0101-0102")
                        .build()
        );

        Store store1 = storeRepository.save(
                Store.builder()
                        .storeName("storeA")
                        .address(new Address("경상남도", "김해시", "삼계로", "50898"))
                        .owner(member)
                        .description("라면가계")
                        .build()
        );

        Store store2 = storeRepository.save(
                Store.builder()
                        .storeName("storeB")
                        .address(new Address("경상남도", "김해시", "삼계로", "50900"))
                        .owner(member)
                        .description("두부가계")
                        .build()
        );

        Store store3 = storeRepository.save(
                Store.builder()
                        .storeName("storeB")
                        .address(new Address("경상남도", "진영시", "봉하마을", "50523"))
                        .owner(member2)
                        .description("두부가계")
                        .build()
        );

        // when
        List<Store> memberStoreList = storeRepository.findByMemberId(member.getId());
        List<Store> member2StoreList = storeRepository.findByMemberId(member2.getId());

        // then
        assertThat(memberStoreList.size()).isEqualTo(2);
        assertThat(member2StoreList.size()).isEqualTo(1);
        assertThat(memberStoreList).containsExactly(store1, store2);
        assertThat(member2StoreList).containsExactly(store3);
    }
}