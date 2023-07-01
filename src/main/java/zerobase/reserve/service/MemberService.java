package zerobase.reserve.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zerobase.reserve.domain.Member;
import zerobase.reserve.dto.CreateMemberDto;
import zerobase.reserve.dto.MemberDto;
import zerobase.reserve.exception.DuplicateException;
import zerobase.reserve.exception.ErrorCode;
import zerobase.reserve.exception.LoginException;
import zerobase.reserve.exception.NotExistsException;
import zerobase.reserve.repository.MemberRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       return memberRepository.findByEmail(username)
                .orElseThrow(() -> new NotExistsException(ErrorCode.MEMBER_NOT_EXISTS));
    }

    /**
     * 일반 회원 가입 기능 함수
     * @param member 회원 가입할 회원의 정보 DTO
     * @return 회원가입 완료 후 회원의 정보 DTO
     */
    @Transactional
    public MemberDto createUserMember(CreateMemberDto.SignUp member){
        checkDuplicateEmail(member.getEmail());
        member.setPassword(passwordEncoder.encode(member.getPassword()));

        List<String> roles = new ArrayList<>();
        roles.add("ROLE_USER");

        return MemberDto.fromEntity(memberRepository.save(member.createMember(roles)));
    }

    /**
     * 파트너 회원 가입 기능 함수
     * @param member 파트너 회원 가입할 회원의 정보 DTO
     * @return 회원가입 완료 후 회원의 정보 DTO
     */
    @Transactional
    public MemberDto createPartnerMember(CreateMemberDto.SignUp member){
        checkDuplicateEmail(member.getEmail());
        member.setPassword(passwordEncoder.encode(member.getPassword()));

        List<String> roles = new ArrayList<>();
        roles.add("ROLE_PARTNER");
        roles.add("ROLE_USER");

        return MemberDto.fromEntity(memberRepository.save(member.createMember(roles)));
    }

    /**
     * 회원 가입에서 회원은 기본적으로 이메일에 의해 구분될 수 있다(PK는 인공 id로 설정)
     * 중복된 이메일로는 가입할 수 없도록 정책을 설정하였기 떄문에 입력받은 이메일에 대해 동일한 이메일이 존재하는지 확인
     * 존재하는 경우 DuplicateException 를 던진다.
     * @param email 확인할 이메일
     */
    private void checkDuplicateEmail(String email) {
        boolean exists = memberRepository.existsByEmail(email);

        if (exists){
            throw new DuplicateException(ErrorCode.DUPLICATE_EMAIL);
        }
    }

    /**
     * 로그인 기능 함수
     * @param signIn 로그인에 필요한 id 및 password 데이터 DTO
     *               로그인 실패 시 LoginException 반환
     * @return 로그인 성공 시 해당 멤버 엔티티 반환
     */
    public Member authenticate(CreateMemberDto.SignIn signIn){
        Member findMember = memberRepository.findByEmail(signIn.getEmail())
                .orElseThrow(() -> new NotExistsException(ErrorCode.MEMBER_NOT_EXISTS));

        if (isPasswordMatched(signIn.getPassword(), findMember.getPassword())){
            throw new LoginException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        return findMember;
    }

    private boolean isPasswordMatched(String inputPassword, String storedPassword) {
        return !passwordEncoder.matches(inputPassword, storedPassword);
    }
}
