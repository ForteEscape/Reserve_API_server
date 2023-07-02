package zerobase.reserve.dto;

import lombok.*;
import zerobase.reserve.domain.Reserve;
import zerobase.reserve.domain.ReserveStatus;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

public class CreateReserveDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request{

        @NotEmpty
        private String reserveTime;
        @NotEmpty
        private String storeName;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class StoreRequest{

        @NotEmpty
        private String reserveTime;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
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
                    .memberEmail(reserve.getMember().getEmail())
                    .storeName(reserve.getStore().getStoreName())
                    .build();
        }
    }
}
