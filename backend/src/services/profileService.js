const userRepository = require('../repositories/userRepository');
const bookingRepository = require('../repositories/bookingRepository');
const favoriteRepository = require('../repositories/favoriteRepository');
const reviewRepository = require('../repositories/reviewRepository');
const AppError = require('../utils/AppError');

const profileService = {
  async get(userId) {
    const user = await userRepository.findById(userId);
    if (!user) {
      throw new AppError('User not found', 404);
    }
    return user;
  },

  async getStats(userId) {
    const user = await userRepository.findById(userId);
    if (!user) {
      throw new AppError('User not found', 404);
    }

    const allBookings = await bookingRepository.findByUserId(userId);
    const totalBookings = allBookings.length;
    const totalFavorites = await favoriteRepository.countByUserId(userId);
    const totalReviews = await reviewRepository.countByUserId(userId);

    return { totalBookings, totalFavorites, totalReviews };
  },

  async update(userId, data) {
    const user = await userRepository.findById(userId);
    if (!user) {
      throw new AppError('User not found', 404);
    }

    if (data.email) {
      const existing = await userRepository.findByEmail(data.email);
      if (existing && existing.id !== userId) {
        throw new AppError('Email already in use', 409);
      }
    }

    return userRepository.update(userId, data);
  },
};

module.exports = profileService;

