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
import zerobase.reserve.exception.DuplicateException;
import zerobase.reserve.exception.ErrorCode;
import zerobase.reserve.exception.LoginException;
import zerobase.reserve.exception.NotExistsException;
import zerobase.reserve.repository.MemberRepository;

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

    @Transactional
    public Member createMember(CreateMemberDto.SignUp signUp){
        checkDuplicateEmail(signUp);
        signUp.setPassword(passwordEncoder.encode(signUp.getPassword()));

        return memberRepository.save(signUp.toEntity());
    }

    private void checkDuplicateEmail(CreateMemberDto.SignUp signUp) {
        boolean exists = memberRepository.existsByEmail(signUp.getEmail());

        if (exists){
            throw new DuplicateException(ErrorCode.DUPLICATE_EMAIL);
        }
    }

    public Member authenticate(CreateMemberDto.SignIn signIn){
        Member findMember = memberRepository.findByEmail(signIn.getEmail())
                .orElseThrow(() -> new NotExistsException(ErrorCode.MEMBER_NOT_EXISTS));

        if (!passwordEncoder.matches(signIn.getPassword(), findMember.getPassword())){
            throw new LoginException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        return findMember;
    }
}
