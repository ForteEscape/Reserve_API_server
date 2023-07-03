package zerobase.reserve.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Address {

    @Column(length = 30)
    private String legion;

    @Column(length = 30)
    private String city;

    @Column(length = 30)
    private String street;

    @Column(length = 30)
    private String zipcode;

    @Builder
    public Address(String legion, String city, String street, String zipcode) {
        this.legion = legion;
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
