package zerobase.reserve.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zerobase.reserve.domain.Member;
import zerobase.reserve.domain.Reserve;
import zerobase.reserve.domain.ReserveStatus;
import zerobase.reserve.domain.Store;
import zerobase.reserve.dto.CreateReserveDto;
import zerobase.reserve.dto.ReserveDto;
import zerobase.reserve.exception.ErrorCode;
import zerobase.reserve.exception.NotExistsException;
import zerobase.reserve.repository.MemberRepository;
import zerobase.reserve.repository.ReserveRepository;
import zerobase.reserve.repository.StoreRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReserveService {

    private final ReserveRepository reserveRepository;
    private final StoreRepository storeRepository;
    private final MemberRepository memberRepository;

    // 예약 생성
    // 로그인한 사용자만 사용 가능하므로 들어오면서 받는 email 을 통해 유저를 특정 가능
    @Transactional
    public CreateReserveDto.Response createReserve(CreateReserveDto.Request reserveInfo, String memberEmail){
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new NotExistsException(ErrorCode.MEMBER_NOT_EXISTS));

        Store store = storeRepository.findByStoreName(reserveInfo.getStoreName())
                .orElseThrow(() -> new NotExistsException(ErrorCode.STORE_NOT_EXISTS));

        LocalDateTime reserveTime = transformStringToLocalDate(reserveInfo.getReserveTime());

        return CreateReserveDto.Response.fromEntity(
                reserveRepository.save(
                        Reserve.builder()
                                .reserveTime(reserveTime)
                                .reserveStatus(ReserveStatus.VALID)
                                .member(member)
                                .store(store)
                                .build()
                )
        );
    }

    // 예약 확인
    // 예약을 먼저 조회 - 도착한 매장의 이름과 회원의 email 을 사용하여 예약을 찾아야 한다.
    // 도출되는 예약은 도착한 매장의 이름이 일치하는 동시에 해당 회원이 예약한 것이어야 한다.
    public ReserveDto arriveCheck(String storeName, String memberEmail){
        Reserve reserve = reserveRepository.findByStoreNameAndMemberEmail(storeName, memberEmail)
                .orElseThrow(() -> new NotExistsException(ErrorCode.RESERVE_NOT_EXISTS));

        return ReserveDto.fromEntity(reserve);
    }

    private LocalDateTime transformStringToLocalDate(String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return LocalDateTime.parse(date, formatter);
    }
}
