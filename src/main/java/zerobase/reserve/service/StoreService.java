package zerobase.reserve.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zerobase.reserve.domain.Address;
import zerobase.reserve.domain.Member;
import zerobase.reserve.domain.Store;
import zerobase.reserve.dto.CreateStoreDto;
import zerobase.reserve.exception.DuplicateException;
import zerobase.reserve.exception.NotExistsException;
import zerobase.reserve.repository.MemberRepository;
import zerobase.reserve.repository.StoreRepository;

import java.util.Optional;

import static zerobase.reserve.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

    private final StoreRepository storeRepository;
    private final MemberRepository memberRepository;

    // 입력받은 매점 정보를 DB에 저장

    /**
     * 입력으로 들어온 매장 정보를 DB에 저장
     * @param storeInfo 저장할 매장 정보 DTO
     * @param ownerEmail 해당 매장의 주인 - 로그인을 통해 나온 JWT 로 얻을 수 있다.
     * @return 저장된 매장 정보 DTO
     */
    @Transactional
    public CreateStoreDto.Response createStore(CreateStoreDto.Request storeInfo, String ownerEmail){
        validateStoreName(storeInfo);
        Member owner = getOwnerFromEmail(ownerEmail);

        return CreateStoreDto.Response.fromEntity(
                storeRepository.save(Store.builder()
                        .storeName(storeInfo.getStoreName())
                        .owner(owner)
                        .address(new Address(storeInfo.getLegion(), storeInfo.getCity(), storeInfo.getStreet(), storeInfo.getZipcode()))
                        .description(storeInfo.getDescription())
                        .build()
                )
        );
    }


    /**
     * 파트너 이메일을 통해 파트너 엔티티 가져오기
     * @param ownerEmail 찾을 파트너 이메일
     * @return 파트너 엔티티
     */
    private Member getOwnerFromEmail(String ownerEmail) {
        Optional<Member> owner = memberRepository.findByEmail(ownerEmail);

        if (owner.isEmpty()){
            throw new NotExistsException(MEMBER_NOT_EXISTS);
        }

        return owner.get();
    }

    // 해당 매점 이름이 이미 존재하는지 확인
    private void validateStoreName(CreateStoreDto.Request storeInfo) {
        Optional<Store> storeData = storeRepository.findByStoreName(storeInfo.getStoreName());

        if (storeData.isPresent()){
            throw new DuplicateException(DUPLICATE_STORE_NAME);
        }
    }
}
