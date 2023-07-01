package zerobase.reserve.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import zerobase.reserve.domain.Store;
import zerobase.reserve.dto.CreateReserveDto;
import zerobase.reserve.dto.CreateStoreDto;
import zerobase.reserve.dto.StoreDto;
import zerobase.reserve.exception.ErrorCode;
import zerobase.reserve.exception.NotExistsException;
import zerobase.reserve.repository.StoreRepository;
import zerobase.reserve.service.ReserveService;
import zerobase.reserve.service.StoreService;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/stores")
public class StoreController {

    private final StoreService storeService;
    private final StoreRepository storeRepository;
    private final ReserveService reserveService;

    @GetMapping
    public List<StoreDto> getStoreList(){
        return storeRepository.findAll().stream()
                .map(StoreDto::fromEntity)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('PARTNER')")
    @PostMapping("/new")
    public CreateStoreDto.Response createStore(
            @RequestBody CreateStoreDto.Request request,
            Principal principal
    ){
        return storeService.createStore(request, principal.getName());
    }

    @GetMapping("/{storeId}")
    public StoreDto getStoreInfo(@PathVariable("storeId") Long storeId){
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NotExistsException(ErrorCode.STORE_NOT_EXISTS));

        return StoreDto.fromEntity(store);
    }

    // 회원은 해당 매점에 대해 예약 진행
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{storeId}/add-reserve")
    public CreateReserveDto.Response addReserve(
            @PathVariable("storeId") Long storeId,
            @RequestBody CreateReserveDto.StoreRequest reserveInfo,
            Principal principal
    ){
       return reserveService.createReserveFromStore(reserveInfo, storeId, principal.getName());
    }
}
