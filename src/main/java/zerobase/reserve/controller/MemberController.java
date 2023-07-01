package zerobase.reserve.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import zerobase.reserve.domain.Member;
import zerobase.reserve.domain.Reserve;
import zerobase.reserve.dto.CreateMemberDto;
import zerobase.reserve.dto.ReserveDto;
import zerobase.reserve.repository.ReserveRepository;
import zerobase.reserve.security.TokenProvider;
import zerobase.reserve.service.MemberService;
import zerobase.reserve.service.ReserveService;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;
    private final ReserveRepository reserveRepository;
    private final ReserveService reserveService;

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody CreateMemberDto.SignUp request){
        Member member = memberService.createMember(request);

        return ResponseEntity.ok(member);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody CreateMemberDto.SignIn request){
        Member member = memberService.authenticate(request);
        String token = tokenProvider.generateToken(member.getEmail(), member.getRoles());

        return ResponseEntity.ok(token);
    }

    @PreAuthorize("hasRole('PARTNER')")
    @GetMapping("/owner/reserves")
    public List<ReserveDto> getOwnerReserve(Principal principal){
        List<Reserve> ownerReserveList = reserveRepository.findByStoreOwnerEmail(principal.getName());

        return ownerReserveList.stream()
                .map(ReserveDto::fromEntity)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user/reserves")
    public List<ReserveDto> getUserReserve(Principal principal){
        List<Reserve> userReserveList = reserveRepository.findByMemberEmail(principal.getName());

        return userReserveList.stream()
                .map(ReserveDto::fromEntity)
                .collect(Collectors.toList());
    }
}