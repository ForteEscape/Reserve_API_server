package zerobase.reserve.dto;

import lombok.*;
import zerobase.reserve.domain.Store;


public class CreateStoreDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request{
        private String storeName;
        private String legion;
        private String city;
        private String street;
        private String zipcode;
        private String description;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response{
        private Long storeId;
        private String storeName;
        private String owner;
        private String legion;
        private String city;
        private String street;
        private String zipcode;
        private String description;

        public static Response fromEntity(Store store){
            return Response.builder()
                    .storeId(store.getId())
                    .storeName(store.getStoreName())
                    .owner(store.getOwner().getName())
                    .city(store.getAddress().getCity())
                    .legion(store.getAddress().getLegion())
                    .street(store.getAddress().getStreet())
                    .zipcode(store.getAddress().getZipcode())
                    .description(store.getDescription())
                    .build();
        }
    }
}
