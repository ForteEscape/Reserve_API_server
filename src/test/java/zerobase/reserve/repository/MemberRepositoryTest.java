package zerobase.reserve.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import zerobase.reserve.domain.Gender;
import zerobase.reserve.domain.Member;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Slf4j
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원 레포지토리 저장 테스트")
    void save() {
        // given
        Member member = memberRepository.save(
                Member.builder()
                        .name("kim")
                        .email("sehun5515@naver.com")
                        .password("1234")
                        .gender(Gender.MALE)
                        .phoneNumber("010-0101-0101")
                        .build()
        );

        // when
        Member findMember = memberRepository.findById(member.getId())
                .orElseThrow(NoSuchElementException::new);

        // then
        log.info("member_id = {}, member_name = {}, member_gender = {}, member_phone_number = {}",
                findMember.getId(), findMember.getName(), findMember.getGender(), findMember.getPhoneNumber());
        log.info("member_createdAt = {}", member.getCreatedDate());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    @DisplayName("회원 리스트 조회 테스트")
    void findAll() {
        //given
        Member member1 = memberRepository.save(
                Member.builder()
                        .name("kim")
                        .email("sehun5515@naver.com")
                        .password("1234")
                        .gender(Gender.MALE)
                        .phoneNumber("010-0101-0101")
                        .build()
        );

        Member member2 = memberRepository.save(
                Member.builder()
                        .name("park")
                        .email("sehun8631@naver.com")
                        .password("1234")
                        .gender(Gender.MALE)
                        .phoneNumber("010-0101-0102")
                        .build()
        );

        Member member3 = memberRepository.save(
                Member.builder()
                        .name("lee")
                        .email("sehun5216@naver.com")
                        .password("1234")
                        .gender(Gender.FEMALE)
                        .phoneNumber("010-0101-0103")
                        .build()
        );

        // when
        List<Member> resultList = memberRepository.findAll();

        // then
        assertThat(resultList.size()).isEqualTo(3);
        assertThat(resultList).containsExactly(member1, member2, member3);
    }
}