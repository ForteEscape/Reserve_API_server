package zerobase.reserve.dto;

import lombok.*;
import zerobase.reserve.domain.Review;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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
