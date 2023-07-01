package zerobase.reserve.dto;

import lombok.*;
import zerobase.reserve.domain.Reserve;
import zerobase.reserve.domain.ReserveStatus;

import java.time.LocalDateTime;

public class CreateReserveDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request{

        private String reserveTime;
        private String storeName;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class StoreRequest{

        private String reserveTime;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response{

        private Long id;
        private LocalDateTime reserveDateTime;
        private ReserveStatus reserveStatus;
        private String memberEmail;
        private String storeName;

        public static Response fromEntity(Reserve reserve){
            return Response.builder()
                    .id(reserve.getId())
                    .reserveDateTime(reserve.getReserveTime())
                    .reserveStatus(reserve.getReserveStatus())
                    .memberEmail(reserve.getMember().getName())
                    .storeName(reserve.getStore().getStoreName())
                    .build();
        }
    }
}
