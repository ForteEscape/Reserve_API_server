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

    public Member save(Member member){
        em.persist(member);

        return member;
    }

    public Optional<Member> findById(Long memberId){
        Member partnerMember = em.find(Member.class, memberId);

        return Optional.ofNullable(partnerMember);
    }

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

    public boolean existsByEmail(String email){
        return findByEmail(email).isPresent();
    }
}
