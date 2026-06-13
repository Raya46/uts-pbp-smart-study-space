const reviewsService = require('../services/reviewsService');

const reviewsController = {
  async getBySpot(req, res) {
    try {
      const { spotId } = req.params;
      const reviews = await reviewsService.getBySpot(parseInt(spotId, 10));
      const mapped = reviews.map(r => ({
        id: r.id,
        user_id: r.user_id,
        user_name: r.user.name,
        user_avatar: r.user.avatar,
        rating: r.rating,
        comment: r.comment,
        created_at: r.created_at,
      }));
      res.json({ reviews: mapped });
    } catch (err) {
      console.error('Get reviews error:', err.message);
      res.status(500).json({ message: 'Server error' });
    }
  },

  async create(req, res) {
    try {
      const { spotId, rating, comment } = req.body;
      if (!spotId || !rating) {
        return res.status(400).json({ message: 'spotId and rating are required' });
      }
      const review = await reviewsService.create(
        req.user.id,
        parseInt(spotId, 10),
        parseInt(rating, 10),
        comment || ''
      );
      res.status(201).json({ review, message: 'Review created successfully' });
    } catch (err) {
      if (err.isOperational) {
        return res.status(err.statusCode).json({ message: err.message });
      }
      console.error('Create review error:', err.message);
      res.status(500).json({ message: 'Server error' });
    }
  },
};

module.exports = reviewsController;
