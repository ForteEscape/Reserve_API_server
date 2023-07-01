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
}
