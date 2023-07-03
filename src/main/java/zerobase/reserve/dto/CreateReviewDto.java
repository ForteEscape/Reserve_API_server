package zerobase.reserve.dto;

import lombok.*;
import zerobase.reserve.domain.Review;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 후기 생성 DTO 모음
 * Request - 매장 후기 요청을 받기 위한 DTO
 * Response - 생성된 후기를 json 으로 반환할 DTO
 */
public class CreateReviewDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request{

        @NotNull
        @Min(0) @Max(5)
        private Integer rating;

        @NotEmpty
        private String reviewContent;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response{

        private Long id;
        private Integer rating;
        private String reviewContent;

        public static Response fromEntity(Review review){
            return Response.builder()
                    .id(review.getId())
                    .rating(review.getRating())
                    .reviewContent(review.getReviewContent())
                    .build();
        }
    }
}
