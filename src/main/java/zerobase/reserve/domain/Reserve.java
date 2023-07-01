package zerobase.reserve.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Reserve{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reserve_id")
    private Long id;

    private LocalDateTime reserveTime;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private ReserveStatus reserveStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    @Builder
    public Reserve(Long id, LocalDateTime reserveTime, ReserveStatus reserveStatus, Member member, Store store) {
        this.id = id;
        this.reserveTime = reserveTime;
        this.reserveStatus = reserveStatus;
        this.member = member;
        this.store = store;
    }

    public void changeReserveStatus(ReserveStatus reserveStatus){
        this.reserveStatus = reserveStatus;
    }
}
