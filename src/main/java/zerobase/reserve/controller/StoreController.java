package zerobase.reserve.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import zerobase.reserve.domain.Store;
import zerobase.reserve.dto.CreateStoreDto;
import zerobase.reserve.dto.StoreDto;
import zerobase.reserve.repository.StoreRepository;
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
}
