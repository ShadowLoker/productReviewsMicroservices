package cat.tecnocampus.review.application.services;

import cat.tecnocampus.review.application.ports.in.ReviewCRUD;
import cat.tecnocampus.review.application.ports.out.persistence.ReviewRepository;

import java.util.List;
import java.util.Optional;

public class ReviewService implements ReviewCRUD {
    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public List<Review> getReviewsFromProduct(long productId) {
        return reviewRepository.getReviewFromProduct(productId);
    }

    @Override
    public Optional<Review> getReview(long id) {
        return reviewRepository.getReview(id);
    }

    @Override
    public Review createReview(Review review) {
        return reviewRepository.save(review);
    }
}
