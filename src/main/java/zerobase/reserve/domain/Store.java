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
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "store")
public class Store{

    @Id @GeneratedValue
    @Column(name = "store_id")
    private Long id;

    @Column(length = 30)
    private String storeName;

    @Embedded
    @Column(length = 50)
    private Address address;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member owner;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    @Builder
    public Store(Long id, String storeName, Address address, String description, Member owner) {
        this.id = id;
        this.storeName = storeName;
        this.address = address;
        this.description = description;
        this.owner = owner;
    }
}
