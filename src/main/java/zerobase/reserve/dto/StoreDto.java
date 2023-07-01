package zerobase.reserve.dto;

import lombok.*;
import zerobase.reserve.domain.Store;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreDto {

    private Long storeId;
    private String ownerName;
    private String storeName;
    private String legion;
    private String city;
    private String street;
    private String description;

    public static StoreDto fromEntity(Store store){
        return StoreDto.builder()
                .storeId(store.getId())
                .ownerName(store.getOwner().getName())
                .storeName(store.getStoreName())
                .city(store.getAddress().getCity())
                .legion(store.getAddress().getLegion())
                .street(store.getAddress().getStreet())
                .description(store.getDescription())
                .build();
    }
}
