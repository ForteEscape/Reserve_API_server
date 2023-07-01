package zerobase.reserve.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import zerobase.reserve.domain.Reserve;
import zerobase.reserve.dto.CreateReserveDto;
import zerobase.reserve.dto.ReserveDto;
import zerobase.reserve.exception.ErrorCode;
import zerobase.reserve.exception.NotExistsException;
import zerobase.reserve.repository.ReserveRepository;
import zerobase.reserve.service.ReserveService;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/reserves")
public class ReserveController {

    private final ReserveRepository reserveRepository;
    private final ReserveService reserveService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/new")
    public CreateReserveDto.Response addReserve(
            @RequestBody CreateReserveDto.Request request,
            Principal principal
    ){
        return reserveService.createReserve(request, principal.getName());
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{reserveId}")
    public ReserveDto getReserve(@PathVariable("reserveId") Long reserveId){
        Reserve reserve = reserveRepository.findById(reserveId)
                .orElseThrow(() -> new NotExistsException(ErrorCode.MEMBER_NOT_EXISTS));

        return ReserveDto.fromEntity(reserve);
    }

    // 매점 점장이 자신 소유의 모든 매점에 들어온 예약을 확인할 수 있다.
    @PreAuthorize("hasRole('PARTNER')")
    @GetMapping("/owner")
    public List<ReserveDto> getOwnerReserveList(Principal principal){
        List<Reserve> ownerReserveList = reserveRepository.findByStoreOwnerEmail(principal.getName());

        return ownerReserveList.stream()
                .map(ReserveDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 매점 점장에게 들어온 예약 내역 단건 조회
    @PreAuthorize("hasRole('PARTNER')")
    @GetMapping("/owner/{reserveId}")
    public ReserveDto getOwnerReserve(@PathVariable("reserveId") Long reserveId, Principal principal){
        return reserveService.getOwnerReserveInfo(reserveId, principal.getName());
    }

    // 매점 점장에게 들어온 예약 취소
    @PreAuthorize("hasRole('PARTNER')")
    @PostMapping("/owner/{reserveId}/cancel")
    public ReserveDto cancelReserveFromOwner(@PathVariable("reserveId") Long reserveId, Principal principal){
        return reserveService.cancelUserReserveFromOwner(reserveId, principal.getName());
    }

    // 회원은 자신이 예약한 내역들을 모두 확인할 수 있다.
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public List<ReserveDto> getUserReserveList(Principal principal){
        List<Reserve> userReserveList = reserveRepository.findByMemberEmail(principal.getName());

        return userReserveList.stream()
                .map(ReserveDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 회원 자신의 예약 단건 조회
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user/{reserveId}")
    public ReserveDto getUserReserve(@PathVariable("reserveId") Long reserveId, Principal principal){
        return reserveService.getReserveFromUser(reserveId, principal.getName());
    }

    // 회원 자신이 예약을 취소
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/user/{reserveId}/cancel")
    public ReserveDto cancelReserveFromUser(@PathVariable("reserveId") Long reserveId, Principal principal){
        return reserveService.cancelUserReserveFromUser(reserveId, principal.getName());
    }

    // 매점 방문 확인
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/user/{reserveId}/checkin")
    public ReserveDto checkinReserve(@PathVariable("reserveId") Long reserveId, Principal principal){
        return reserveService.arriveCheck(reserveId, principal.getName());
    }
}
