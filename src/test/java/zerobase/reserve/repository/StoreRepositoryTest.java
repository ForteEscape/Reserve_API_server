package zerobase.reserve.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import zerobase.reserve.domain.*;
import zerobase.reserve.dto.StoreSearchCond;

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
                        .address(new Address("경상남도", "김해시", "삼계로", "50989"))
                        .owner(member2)
                        .description("두부가계")
                        .build()
        );

        // when
        StoreSearchCond cond = new StoreSearchCond();
        List<Store> storeList = storeRepository.findAll(cond);

        // then
        assertThat(storeList.size()).isEqualTo(3);
        assertThat(storeList).containsExactly(store1, store2, store3);
    }

    @Test
    @DisplayName("매장 키워드로 검색")
    void findAllWithSearchKeyword(){
        Member member = memberRepository.save(
                Member.builder()
                        .name("kim")
                        .email("sehun5515@naver.com")
                        .password("1234")
                        .gender(Gender.MALE)
                        .phoneNumber("010-0101-0101")
                        .build()
        );

        Store store1 = storeRepository.save(
                Store.builder()
                        .storeName("참새정")
                        .address(new Address("경상남도", "김해시", "삼계로", "50898"))
                        .owner(member)
                        .description("라면가계")
                        .build()
        );

        Store store2 = storeRepository.save(
                Store.builder()
                        .storeName("까치정")
                        .address(new Address("경상남도", "김해시", "삼계로", "50900"))
                        .owner(member)
                        .description("맛있는 술집")
                        .build()
        );

        Store store3 = storeRepository.save(
                Store.builder()
                        .storeName("두루미정")
                        .address(new Address("경상남도", "김해시", "삼계로", "50989"))
                        .owner(member)
                        .description("맛있는 밥집")
                        .build()
        );

        // when
        StoreSearchCond cond = new StoreSearchCond();
        cond.setKeyword("까치");
        List<Store> storeList = storeRepository.findAll(cond);

        cond.setKeyword("두루미");
        List<Store> storeList2 = storeRepository.findAll(cond);

        cond.setKeyword("정");
        List<Store> storeList3 = storeRepository.findAll(cond);

        // then
        assertThat(storeList.size()).isEqualTo(1);
        assertThat(storeList).containsExactly(store2);
        assertThat(storeList2.size()).isEqualTo(1);
        assertThat(storeList2).containsExactly(store3);
        assertThat(storeList3.size()).isEqualTo(3);
        assertThat(storeList3).containsExactly(store1, store2, store3);
    }
}