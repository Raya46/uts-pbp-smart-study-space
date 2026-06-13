const bookingRepository = require('../repositories/bookingRepository');
const userRepository = require('../repositories/userRepository');
const spotRepository = require('../repositories/spotRepository');
const AppError = require('../utils/AppError');

const bookingsService = {
  async getByUser(userId, status) {
    return bookingRepository.findByUserId(userId, status);
  },

  async create({ userId, spotId, date, timeSlot, seats }) {
    const user = await userRepository.findById(userId);
    if (!user) {
      throw new AppError('User not found', 404);
    }

    const spot = await spotRepository.findById(spotId);
    if (!spot) {
      throw new AppError('Spot not found', 404);
    }

    const result = await bookingRepository.create({
      user_id: userId,
      spot_id: spotId,
      date,
      time_slot: timeSlot,
      seats,
      status: 'Upcoming',
    });
    return { id: result.id };
  },
};

module.exports = bookingsService;

