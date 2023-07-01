package zerobase.reserve.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import zerobase.reserve.domain.Store;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class StoreRepository {

    private final EntityManager em;

    public Store save(Store store){
        em.persist(store);

        return store;
    }

    public Optional<Store> findById(Long storeId){
        Store store = em.find(Store.class, storeId);

        return Optional.ofNullable(store);
    }

    public List<Store> findAll(){
        return em.createQuery("select s from Store s", Store.class)
                .getResultList();
    }

    public Optional<Store> findByStoreName(String storeName){
        List<Store> findByStoreName = em.createQuery("select s from Store s where s.storeName = :storeName", Store.class)
                .setParameter("storeName", storeName)
                .getResultList();

        return findByStoreName.stream().findAny();
    }

    public List<Store> findByMemberId(Long memberId){
        return em.createQuery("select s from Store s join s.owner m on m.id = :memberId", Store.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }
}
