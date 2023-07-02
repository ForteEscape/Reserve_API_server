package zerobase.reserve.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zerobase.reserve.dto.CreateReviewDto;
import zerobase.reserve.repository.ReviewRepository;
import zerobase.reserve.service.ReviewService;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewRepository reviewRepository;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public List<CreateReviewDto.Response> getReviewFromUser(Principal principal){
        return reviewRepository.findByUserEmail(principal.getName()).stream()
                .map(CreateReviewDto.Response::fromEntity)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('PARTNER')")
    @GetMapping("/owner")
    public List<CreateReviewDto.Response> getReviewFromUserOwnerSide(Principal principal){
        return reviewRepository.findByOwnerEmail(principal.getName()).stream()
                .map(CreateReviewDto.Response::fromEntity)
                .collect(Collectors.toList());
    }
}
