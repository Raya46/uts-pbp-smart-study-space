const favoriteRepository = require('../repositories/favoriteRepository');
const spotRepository = require('../repositories/spotRepository');
const AppError = require('../utils/AppError');

const favoritesService = {
  async getAll(userId) {
    return favoriteRepository.findByUserId(userId);
  },

  async toggle(userId, spotId) {
    const spot = await spotRepository.findById(spotId);
    if (!spot) {
      throw new AppError('Spot not found', 404);
    }

    const existing = await favoriteRepository.findByUserAndSpot(userId, spotId);
    if (existing) {
      await favoriteRepository.delete(userId, spotId);
      return { favorited: false, message: 'Removed from favorites' };
    } else {
      await favoriteRepository.create(userId, spotId);
      return { favorited: true, message: 'Added to favorites' };
    }
  },
};

module.exports = favoritesService;
