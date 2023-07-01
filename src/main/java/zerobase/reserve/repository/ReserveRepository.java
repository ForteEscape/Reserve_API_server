package zerobase.reserve.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import zerobase.reserve.domain.Reserve;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ReserveRepository {

    private final EntityManager em;

    public Reserve save(Reserve reserve){
        em.persist(reserve);

        return reserve;
    }

    public Optional<Reserve> findById(Long reserveId){
        return Optional.ofNullable(em.find(Reserve.class, reserveId));
    }

    public List<Reserve> findByStoreId(Long storeId){
        return em.createQuery("select r from Reserve r where r.store.id = :storeId", Reserve.class)
                .setParameter("storeId", storeId)
                .getResultList();
    }

    // 주인이 자신의 가계에 들어온 예약들을 모두 확인할 수 있어야 한다.
    // 해당 주인이 가지고 있는 가계가 가지고 있는 예약들을 확인해야 한다.
    public List<Reserve> findByStoreOwnerEmail(String ownerEmail){
        return em.createQuery("select r from Reserve r join r.store s join s.owner m on m.email = :ownerId",
                Reserve.class)
                .setParameter("ownerId", ownerEmail)
                .getResultList();
    }

    // 고객은 자신이 예약한 내역들을 모두 확인할 수 있어야 한다.
    public List<Reserve> findByMemberId(Long memberId){
        return em.createQuery("select r from Reserve r join r.member on r.member.id = :memberId", Reserve.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    public List<Reserve> findByMemberEmail(String memberEmail){
        return em.createQuery("select r from Reserve r join r.member on r.member.email = :memberEmail", Reserve.class)
                .setParameter("memberEmail", memberEmail)
                .getResultList();
    }

    // 회원이 매장에 도착하여 방문확인을 수행할 시 매장의 이름 및 자신의 이메일을 통해 예약을 조회할 수 있어야 한다.
    public Optional<Reserve> findByStoreNameAndMemberEmail(String storeName, String memberEmail){
        List<Reserve> resultList = em.createQuery("select r from Reserve r " +
                        "join r.store s " +
                        "join r.member m " +
                        "on s.storeName = :storeName and m.email = :memberEmail", Reserve.class
                )
                .setParameter("storeName", storeName)
                .setParameter("memberEmail", memberEmail)
                .getResultList();

        return resultList.stream().findAny();
    }
}
