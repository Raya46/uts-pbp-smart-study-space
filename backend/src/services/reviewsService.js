const reviewRepository = require('../repositories/reviewRepository');
const spotRepository = require('../repositories/spotRepository');
const AppError = require('../utils/AppError');

const reviewsService = {
  async getBySpot(spotId) {
    return reviewRepository.findBySpotId(spotId);
  },

  async create(userId, spotId, rating, comment) {
    const spot = await spotRepository.findById(spotId);
    if (!spot) {
      throw new AppError('Spot not found', 404);
    }

    const existing = await reviewRepository.findByUserAndSpot(userId, spotId);
    if (existing) {
      throw new AppError('You have already reviewed this spot', 409);
    }

    if (rating < 1 || rating > 5) {
      throw new AppError('Rating must be between 1 and 5', 400);
    }

    const review = await reviewRepository.create(userId, spotId, rating, comment);

    const avgRating = await reviewRepository.avgRatingBySpotId(spotId);
    const reviewCount = await reviewRepository.countBySpotId(spotId);

    await spotRepository.updateSpotRating(spotId, avgRating || 0, reviewCount);

    return review;
  },
};

module.exports = reviewsService;
