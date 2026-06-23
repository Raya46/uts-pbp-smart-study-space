const bookingRepository = require('../repositories/bookingRepository');
const userRepository = require('../repositories/userRepository');
const spotRepository = require('../repositories/spotRepository');
const AppError = require('../utils/AppError');

const bookingsService = {

  async getByUser(userId, status) {
    return bookingRepository.findByUserId(userId, status);
  },

  async getDetail(userId, id) {

    const booking = await bookingRepository.findById(id, userId);

    if (!booking) {
      throw new AppError('Booking not found', 404);
    }

    return booking;
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

    return {
      id: result.id,
    };
  },

  async checkIn(userId, id) {

    const booking = await bookingRepository.findById(id, userId);

    if (!booking) {
      throw new AppError('Booking not found', 404);
    }

    if (booking.status !== 'Upcoming') {
      throw new AppError('Booking cannot be checked in', 400);
    }

    return bookingRepository.updateStatus(id, userId, 'Active');
  },

  async endBooking(userId, id) {

    const booking = await bookingRepository.findById(id, userId);

    if (!booking) {
      throw new AppError('Booking not found', 404);
    }

    if (booking.status !== 'Active') {
      throw new AppError('Booking is not active', 400);
    }

    return bookingRepository.updateStatus(id, userId, 'Completed');
  },

  async cancelBooking(userId, id) {

    const booking = await bookingRepository.findById(id, userId);

    if (!booking) {
      throw new AppError('Booking not found', 404);
    }

    if (booking.status !== 'Upcoming') {
      throw new AppError('Only upcoming bookings can be cancelled', 400);
    }

    return bookingRepository.cancelBooking(id, userId);
  },

};

module.exports = bookingsService;