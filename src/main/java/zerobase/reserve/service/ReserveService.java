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
import zerobase.reserve.exception.InvalidReserveException;
import zerobase.reserve.exception.NotExistsException;
import zerobase.reserve.exception.NotMatchException;
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

    /**
     * 회원이 매장 상세 조회에 들어가지 않은 상태에서 직접 예약을 생성할 때 사용
     * 유저의 특정은 로그인한 사용자만 사용 가능하므로 principal.getName()을 통해 받아온다.
     * @param reserveInfo 생성할 예약의 정보 DTO
     * @param memberEmail 예약을 진행하는 회원의 Email
     * @return 예약 정보 DTO
     */
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

    /**
     * 매장 상세에서 예약을 진행하는 경우 호출
     * 매장 상세 URL 에서 사용되기 때문에 storeName 대신 storeId가 들어온다.
     * @param reserveInfo 생성할 예약의 정보 DTO
     * @param storeId 진행하는 예약의 대상인 매장 id
     * @param memberEmail 예약 진행중인 회원의 Email
     * @return 만들어진 예약 정보 DTO
     */
    @Transactional
    public CreateReserveDto.Response createReserveFromStore(
            CreateReserveDto.StoreRequest reserveInfo,
            Long storeId,
            String memberEmail
    ){
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new NotExistsException(ErrorCode.MEMBER_NOT_EXISTS));

        Store store = storeRepository.findById(storeId)
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

    /**
     * 점장에게 들어온 예약에 대한 단건 조회
     * 다른 점장에 대한 예약을 조회할 수 없도록 검증  - isOwnerEmailMatched 로 수행
     * @param reserveId 예약 id
     * @param partnerEmail 점장의 Email
     * @return 예약 DTO
     */
    public ReserveDto getOwnerReserveInfo(Long reserveId, String partnerEmail){
        Reserve reserve = reserveRepository.findById(reserveId)
                .orElseThrow(() -> new NotExistsException(ErrorCode.RESERVE_NOT_EXISTS));

        if (isOwnerEmailMatched(partnerEmail, reserve)){
            throw new NotMatchException(ErrorCode.ILLEGAL_ACCESS);
        }

        return ReserveDto.fromEntity(reserve);
    }

    /**
     * 점장에게 들어온 단건 예약에 대한 취소
     * 다른 점장의 예약을 취소할 수 없도록 예약이 걸린 매점의 점장에 대한 검증 - isOwnerEmailMatched 로 수행
     * @param reserveId 예약 id
     * @param partnerEmail 점장 Email
     * @return 취소된 예약의 DTO
     */
    @Transactional
    public ReserveDto cancelUserReserveFromOwner(Long reserveId, String partnerEmail){
        Reserve reserve = reserveRepository.findById(reserveId)
                .orElseThrow(() -> new NotExistsException(ErrorCode.RESERVE_NOT_EXISTS));

        if (isOwnerEmailMatched(partnerEmail, reserve)){
            throw new NotMatchException(ErrorCode.ILLEGAL_ACCESS);
        }

        reserve.changeReserveStatus(ReserveStatus.CANCEL);

        return ReserveDto.fromEntity(reserve);
    }

    /**
     * 회원의 예약 단건 조회
     * 다른 예약 조회 불가능하도록 검증 수행 - isUserEmailMatched 로 수행
     * @param reserveId 예약 id
     * @param userEmail 유저의 Email
     * @return 해당 예약의 DTO
     */
    public ReserveDto getReserveFromUser(Long reserveId, String userEmail){
        Reserve reserve = reserveRepository.findById(reserveId)
                .orElseThrow(() -> new NotExistsException(ErrorCode.RESERVE_NOT_EXISTS));

        if (isUserEmailMatched(userEmail, reserve)){
            throw new NotMatchException(ErrorCode.ILLEGAL_ACCESS);
        }

        return ReserveDto.fromEntity(reserve);
    }

    /**
     * 회원이 진행한 예약에 대한 취소
     * 다른 회원의 예약을 취소할 수 없도록 예약이 걸린 매점의 점장에 대한 검증 - isUserEmailMatched 로 수행
     * @param reserveId 예약 id
     * @param userEmail 점장 Email
     * @return 취소된 예약의 DTO
     */
    @Transactional
    public ReserveDto cancelUserReserveFromUser(Long reserveId, String userEmail) {
        Reserve reserve = reserveRepository.findById(reserveId)
                .orElseThrow(() -> new NotExistsException(ErrorCode.RESERVE_NOT_EXISTS));

        if (isUserEmailMatched(userEmail, reserve)) {
            throw new NotMatchException(ErrorCode.ILLEGAL_ACCESS);
        }

        reserve.changeReserveStatus(ReserveStatus.CANCEL);

        return ReserveDto.fromEntity(reserve);
    }

    private static boolean isUserEmailMatched(String userEmail, Reserve reserve) {
        return !reserve.getMember().getEmail().equals(userEmail);
    }

    private static boolean isOwnerEmailMatched(String email, Reserve reserve) {
        return !reserve.getStore().getOwner().getEmail().equals(email);
    }

    /**
     * 매점 방문 확인 기능
     * 예약을 조회한 후에 해당 예약을 회원이 진행한 것인지를 확인 후, 조건 확인
     * 이미 취소된 예약인지 확인 - ReserveStatus 확인
     * 예약 시간 10분 전에 도착했는지 확인 - isReserveValid 확인
     * @param reserveId 예약 id
     * @param userEmail 예약한 회원의 Email
     * @return 도착 완료된 예약의 정보 DTO
     */
    @Transactional(noRollbackFor = InvalidReserveException.class)
    public ReserveDto arriveCheck(Long reserveId, String userEmail){
        Reserve reserve = reserveRepository.findById(reserveId)
                .orElseThrow(() -> new NotExistsException(ErrorCode.RESERVE_NOT_EXISTS));

        if (isUserEmailMatched(userEmail, reserve)){
            throw new NotMatchException(ErrorCode.ILLEGAL_ACCESS);
        }

        // 이미 취소된 예약인지
        if (reserve.getReserveStatus() == ReserveStatus.CANCEL){
            throw new InvalidReserveException(ErrorCode.RESERVE_CANCELED);
        }

        // 예약시간 10분 이전에 도착했는지 확인
        if (isReserveValid(reserve)){
            reserve.changeReserveStatus(ReserveStatus.CANCEL);
            throw new InvalidReserveException(ErrorCode.RESERVE_NO_LONGER_AVAILABLE);
        }

        reserve.changeReserveStatus(ReserveStatus.COMPLETE);

        return ReserveDto.fromEntity(reserve);
    }

    private static boolean isReserveValid(Reserve reserve) {
        return !LocalDateTime.now().isBefore(reserve.getReserveTime().minusMinutes(10));
    }

    // String to LocalDateTime
    private LocalDateTime transformStringToLocalDate(String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return LocalDateTime.parse(date, formatter);
    }
}
