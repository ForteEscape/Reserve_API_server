package zerobase.reserve.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zerobase.reserve.domain.Member;
import zerobase.reserve.dto.CreateMemberDto;
import zerobase.reserve.dto.MemberDto;
import zerobase.reserve.security.TokenProvider;
import zerobase.reserve.service.MemberService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    @PostMapping("/owner/signup")
    public MemberDto signUpOwner(@Valid @RequestBody CreateMemberDto.SignUp request){
        return memberService.createPartnerMember(request);
    }

    @PostMapping("/user/signup")
    public MemberDto signUpUser(@Valid @RequestBody CreateMemberDto.SignUp request){
        return memberService.createUserMember(request);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@Valid @RequestBody CreateMemberDto.SignIn request){
        Member member = memberService.authenticate(request);
        String token = tokenProvider.generateToken(member.getEmail(), member.getRoles());

        return ResponseEntity.ok(token);
    }
}
