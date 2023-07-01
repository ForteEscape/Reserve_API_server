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

    //spring security 를 사용하여 로그인한 사용자 정보에서 id를 토대로 사용자 정보 가져오기
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
