package zerobase.reserve.dto;

import lombok.*;
import zerobase.reserve.domain.Reserve;
import zerobase.reserve.domain.ReserveStatus;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

/**
 * 예약 생성 DTO 모음
 * Request - 예약 생성 요청을 받기 위한 DTO
 * Response - 생성된 예약을 json 으로 반환할 DTO
 * StoreRequest - 매장 상세 정보에서 예약 수행 시 요청을 받는 DTO
 */
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
