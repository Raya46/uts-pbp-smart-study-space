const bookingsService = require('../services/bookingsService');

const bookingsController = {

  async getByUser(req, res) {
    try {
      const userId = req.user.id;
      const { status } = req.query;

      const bookings = await bookingsService.getByUser(userId, status);

      const mapped = bookings.map(b => ({
        id: b.id,
        spot_id: b.spot_id,
        spot_name: b.spot.name,
        category: b.spot.category,
        date: b.date,
        time_slot: b.time_slot,
        seats: b.seats,
        status: b.status,
        image_url: b.spot.image_url,
        tag: b.spot.tag,
      }));

      res.json({
        bookings: mapped,
      });

    } catch (err) {
      console.error('Get bookings error:', err.message);
      res.status(500).json({
        message: 'Server error',
      });
    }
  },

  async getDetail(req, res) {
    try {

      const bookingId = parseInt(req.params.id);

      const booking = await bookingsService.getDetail(
        req.user.id,
        bookingId
      );

      res.json({
          booking: {
              id: booking.id,
              spot_id: booking.spot_id,
              spot_name: booking.spot.name,
              category: booking.spot.category,
              date: booking.date,
              time_slot: booking.time_slot,
              seats: booking.seats,
              status: booking.status,
              image_url: booking.spot.image_url,
              tag: booking.spot.tag
          }
      });

    } catch (err) {

      if (err.isOperational) {
        return res.status(err.statusCode).json({
          message: err.message,
        });
      }

      console.error('Get booking detail error:', err.message);

      res.status(500).json({
        message: 'Server error',
      });

    }
  },

  async create(req, res) {

    try {

      const {
        spotId,
        date,
        timeSlot,
        seats,
      } = req.body;

      if (!spotId || !date || !timeSlot || !seats) {
        return res.status(400).json({
          message: 'All fields are required',
        });
      }

      const booking = await bookingsService.create({
        userId: req.user.id,
        spotId: parseInt(spotId),
        date,
        timeSlot,
        seats,
      });

      res.status(201).json({
        booking,
        message: 'Booking created successfully',
      });

    } catch (err) {

      if (err.isOperational) {
        return res.status(err.statusCode).json({
          message: err.message,
        });
      }

      console.error('Create booking error:', err.message);

      res.status(500).json({
        message: 'Server error',
      });

    }

  },

  async checkIn(req, res) {

    try {

      const bookingId = parseInt(req.params.id);

      await bookingsService.checkIn(
        req.user.id,
        bookingId
      );

      res.json({
        message: 'Check in successful',
      });

    } catch (err) {

      if (err.isOperational) {
        return res.status(err.statusCode).json({
          message: err.message,
        });
      }

      console.error('Check in error:', err.message);

      res.status(500).json({
        message: 'Server error',
      });

    }

  },

  async endBooking(req, res) {

    try {

      const bookingId = parseInt(req.params.id);

      await bookingsService.endBooking(
        req.user.id,
        bookingId
      );

      res.json({
        message: 'Booking completed',
      });

    } catch (err) {

      if (err.isOperational) {
        return res.status(err.statusCode).json({
          message: err.message,
        });
      }

      console.error('End booking error:', err.message);

      res.status(500).json({
        message: 'Server error',
      });

    }

  },

  async cancelBooking(req, res) {

    try {

      const bookingId = parseInt(req.params.id);

      await bookingsService.cancelBooking(
        req.user.id,
        bookingId
      );

      res.json({
        message: 'Booking cancelled',
      });

    } catch (err) {

      if (err.isOperational) {
        return res.status(err.statusCode).json({
          message: err.message,
        });
      }

      console.error('Cancel booking error:', err.message);

      res.status(500).json({
        message: 'Server error',
      });

    }

  },

};

module.exports = bookingsController;