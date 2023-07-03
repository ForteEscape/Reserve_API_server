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

    /**
     * 예약 엔티티 저장
     * @param reserve 저장할 예약 엔티티
     * @return 저장된 예약 엔티티
     */
    public Reserve save(Reserve reserve){
        em.persist(reserve);

        return reserve;
    }

    public Optional<Reserve> findById(Long reserveId){
        return Optional.ofNullable(em.find(Reserve.class, reserveId));
    }

    /**
     * 주인이 가지고 있는 모든 가계들에 대한 예약을 가져오는 메서드
     * @param ownerEmail 파트너 회원의 이메일
     * @return 입력으로 받은 파트너 회원이 가진 모든 가계들에 들어온 모든 예약 데이터 리스트
     */
    public List<Reserve> findByStoreOwnerEmail(String ownerEmail){
        return em.createQuery("select r from Reserve r join r.store s join s.owner m on m.email = :ownerId",
                Reserve.class)
                .setParameter("ownerId", ownerEmail)
                .getResultList();
    }

    // 고객은 자신이 예약한 내역들을 모두 확인할 수 있어야 한다.

    /**
     * 회원 자신이 예약한 내역들을 모두 확인 가능
     * @param memberEmail 회원 이메일
     * @return 회원이 수행한 예약 리스트들
     */
    public List<Reserve> findByMemberEmail(String memberEmail){
        return em.createQuery("select r from Reserve r join r.member on r.member.email = :memberEmail", Reserve.class)
                .setParameter("memberEmail", memberEmail)
                .getResultList();
    }
}
