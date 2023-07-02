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

    public Review save(Review review){
        em.persist(review);

        return review;
    }

    public Optional<Review> findById(Long reviewId){
        Review review = em.find(Review.class, reviewId);

        return Optional.ofNullable(review);
    }

    // 가계에 대한 리뷰 확인
    public List<Review> findByStoreName(String storeName){
        return em.createQuery("select r from Review r join Store s on s.storeName = :storeName", Review.class)
                .setParameter("storeName", storeName)
                .getResultList();
    }

    // 회원이 등록한 리뷰 확인
    public List<Review> findByUserEmail(String userEmail){
        return em.createQuery("select r from Review r join Member m on m.email = :userEmail", Review.class)
                .setParameter("userEmail", userEmail)
                .getResultList();
    }
}
