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

    /**
     * 매장 엔티티 저장
     * @param store 저장할 매장 엔티티
     * @return 저장된 매장 엔티티
     */
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

    /**
     * 매장 이름으로 매장 엔티티 조회
     * @param storeName 조회할 매장의 이름
     * @return 해당 매장의 엔티티
     */
    public Optional<Store> findByStoreName(String storeName){
        List<Store> findByStoreName = em.createQuery("select s from Store s where s.storeName = :storeName", Store.class)
                .setParameter("storeName", storeName)
                .getResultList();

        return findByStoreName.stream().findAny();
    }
}
