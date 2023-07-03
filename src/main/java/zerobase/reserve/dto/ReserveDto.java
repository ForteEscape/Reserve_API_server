package zerobase.reserve.dto;

import lombok.*;
import zerobase.reserve.domain.Reserve;
import zerobase.reserve.domain.ReserveStatus;

import java.time.LocalDateTime;

/**
 * 예약 정보 DTO
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReserveDto {

    private Long id;
    private LocalDateTime reserveDateTime;
    private ReserveStatus reserveStatus;
    private String memberEmail;
    private String storeName;

    public static ReserveDto fromEntity(Reserve reserve){
        return ReserveDto.builder()
                .id(reserve.getId())
                .reserveDateTime(reserve.getReserveTime())
                .reserveStatus(reserve.getReserveStatus())
                .storeName(reserve.getStore().getStoreName())
                .memberEmail(reserve.getMember().getEmail())
                .build();
    }
}
