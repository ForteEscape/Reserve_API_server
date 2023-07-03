package zerobase.reserve.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import zerobase.reserve.domain.Member;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MemberRepository {

    private final EntityManager em;

    /**
     * 멤버 엔티티 저장
     * @param member 저장할 멤버 엔티티
     * @return 저장된 멤버 엔티티
     */
    public Member save(Member member){
        em.persist(member);

        return member;
    }

    /**
     * 회원 id로 회원 조회
     * @param memberId 조회할 회원의 id
     * @return 조회된 회원의 엔티티
     */
    public Optional<Member> findById(Long memberId){
        Member partnerMember = em.find(Member.class, memberId);

        return Optional.ofNullable(partnerMember);
    }

    /**
     * 회원 email 로 회원 조회
     * @param email 조회할 회원의 email
     * @return 조회된 회원의 엔티티
     */
    public Optional<Member> findByEmail(String email){
        List<Member> findMember = em.createQuery("select m from Member m where m.email = :email", Member.class)
                .setParameter("email", email)
                .getResultList();

        return findMember.stream().findAny();
    }

    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }


    /**
     * 중복 확인용 함수
     * @param email 확인할 이메일
     * @return 해당 이메일이 이미 존재하는 경우 True, 존재하지 않는 경우 False
     */
    public boolean existsByEmail(String email){
        return findByEmail(email).isPresent();
    }
}
