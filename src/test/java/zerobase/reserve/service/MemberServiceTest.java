package zerobase.reserve.service;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import zerobase.reserve.domain.Authority;
import zerobase.reserve.domain.Member;
import zerobase.reserve.dto.CreateMemberDto;
import zerobase.reserve.dto.MemberDto;
import zerobase.reserve.exception.DuplicateException;
import zerobase.reserve.exception.ErrorCode;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Slf4j
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("일반 회원 가입 - 성공")
    void createUserMember() {
        // given
        CreateMemberDto.SignUp memberDto = CreateMemberDto.SignUp.builder()
                .name("kim")
                .email("sehun5515@naver.com")
                .password("1234")
                .phoneNumber("010-8631-8187")
                .gender("MALE")
                .build();

        // when
        MemberDto userMember = memberService.createUserMember(memberDto);

        // then
        assertThat(userMember.getEmail()).isEqualTo("sehun5515@naver.com");
        assertThat(userMember.getName()).isEqualTo("kim");
        assertThat(passwordEncoder.matches("1234", userMember.getPassword())).isTrue();
        assertThat(userMember.getGender()).isEqualTo("MALE");
        assertThat(userMember.getRoles()).contains("ROLE_USER");
    }

    @Test
    @DisplayName("일반 회원 가입 실패 - 중복된 이메일 감지")
    void createUserMemberFailedDuplicatedEmail(){
        // given
        CreateMemberDto.SignUp memberDto = CreateMemberDto.SignUp.builder()
                .name("kim")
                .email("sehun5515@naver.com")
                .password("1234")
                .phoneNumber("010-8631-8187")
                .gender("MALE")
                .build();

        CreateMemberDto.SignUp memberDto2 = CreateMemberDto.SignUp.builder()
                .name("park")
                .email("sehun5515@naver.com")
                .password("1234")
                .phoneNumber("010-5213-8631")
                .gender("FEMALE")
                .build();

        // when
        memberService.createUserMember(memberDto);

        // then
        DuplicateException duplicateException = assertThrows(DuplicateException.class,
                () -> memberService.createUserMember(memberDto2));
        assertThat(duplicateException.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_EMAIL);
    }

    @Test
    @DisplayName("파트너 회원 가입 - 성공")
    void createPartnerMember() {
        // given
        CreateMemberDto.SignUp memberDto = CreateMemberDto.SignUp.builder()
                .name("kim")
                .email("sehun5515@naver.com")
                .password("1234")
                .phoneNumber("010-8631-8187")
                .gender("MALE")
                .build();

        // when
        MemberDto userMember = memberService.createPartnerMember(memberDto);

        // then
        assertThat(userMember.getEmail()).isEqualTo("sehun5515@naver.com");
        assertThat(userMember.getName()).isEqualTo("kim");
        assertThat(passwordEncoder.matches("1234", userMember.getPassword())).isTrue();
        assertThat(userMember.getGender()).isEqualTo("MALE");
        assertThat(userMember.getRoles()).contains("ROLE_USER", "ROLE_PARTNER");
    }

    @Test
    @DisplayName("파트너 회원 가입 실패 - 중복된 이메일 존재")
    void createPartnerMemberFailedDuplicateEmail(){
        // given
        CreateMemberDto.SignUp memberDto = CreateMemberDto.SignUp.builder()
                .name("kim")
                .email("sehun5515@naver.com")
                .password("1234")
                .phoneNumber("010-8631-8187")
                .gender("MALE")
                .build();

        CreateMemberDto.SignUp memberDto2 = CreateMemberDto.SignUp.builder()
                .name("park")
                .email("sehun5515@naver.com")
                .password("1234")
                .phoneNumber("010-5213-8631")
                .gender("FEMALE")
                .build();

        // when
        memberService.createPartnerMember(memberDto);

        // then
        DuplicateException duplicateException = assertThrows(DuplicateException.class,
                () -> memberService.createPartnerMember(memberDto2));
        assertThat(duplicateException.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_EMAIL);
    }

}