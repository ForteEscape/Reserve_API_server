package zerobase.reserve.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import zerobase.reserve.domain.Review;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ReviewRepository {

    private final EntityManager em;

    /**
     * 리뷰 엔티티 저장
     * @param review 저장할 리뷰 엔티티
     * @return 저장된 리뷰 엔티티
     */
    public Review save(Review review){
        em.persist(review);

        return review;
    }

    public Optional<Review> findById(Long reviewId){
        Review review = em.find(Review.class, reviewId);

        return Optional.ofNullable(review);
    }

    /**
     * 매장의 id를 통해 리뷰 조회
     * @param storeId 리뷰를 조회할 매장의 id
     * @return 해당 매장에 등록된 리뷰 리스트
     */
    public List<Review> findByStoreId(Long storeId){
        return em.createQuery("select r from Review r join Store s on s.id = :storeId", Review.class)
                .setParameter("storeId", storeId)
                .getResultList();
    }

    /**
     * 회원이 등록한 리뷰 조회
     * @param userEmail 확인할 회원의 이메일
     * @return 회원이 등록한 리뷰 리스트
     */
    public List<Review> findByUserEmail(String userEmail){
        return em.createQuery("select r from Review r join r.member m on m.email = :userEmail", Review.class)
                .setParameter("userEmail", userEmail)
                .getResultList();
    }

    /**
     * 자신의 매장에 들어온 모든 리뷰 조회
     * @param ownerEmail 확인할 파트너 회원의 이메일
     * @return 파트너 회원의 매장에 등록한 리뷰 리스트
     */
    public List<Review> findByOwnerEmail(String ownerEmail){
        return em.createQuery("select r from Review r join r.store s on s.owner.email = :ownerEmail", Review.class)
                .setParameter("ownerEmail", ownerEmail)
                .getResultList();
    }
}
