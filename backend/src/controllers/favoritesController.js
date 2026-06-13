const favoritesService = require('../services/favoritesService');

const favoritesController = {
  async getAll(req, res) {
    try {
      const favorites = await favoritesService.getAll(req.user.id);
      const mapped = favorites.map(f => ({
        id: f.id,
        spot_id: f.spot_id,
        spot_name: f.spot.name,
        category: f.spot.category,
        image_url: f.spot.image_url,
        tag: f.spot.tag,
        rating: f.spot.rating,
        created_at: f.created_at,
      }));
      res.json({ favorites: mapped });
    } catch (err) {
      console.error('Get favorites error:', err.message);
      res.status(500).json({ message: 'Server error' });
    }
  },

  async toggle(req, res) {
    try {
      const { spotId } = req.body;
      if (!spotId) {
        return res.status(400).json({ message: 'spotId is required' });
      }
      const result = await favoritesService.toggle(req.user.id, parseInt(spotId, 10));
      res.json(result);
    } catch (err) {
      if (err.isOperational) {
        return res.status(err.statusCode).json({ message: err.message });
      }
      console.error('Toggle favorite error:', err.message);
      res.status(500).json({ message: 'Server error' });
    }
  },

  async check(req, res) {
    try {
      const { spotId } = req.params;
      const favoriteRepository = require('../repositories/favoriteRepository');
      const fav = await favoriteRepository.findByUserAndSpot(req.user.id, parseInt(spotId, 10));
      res.json({ favorited: !!fav });
    } catch (err) {
      console.error('Check favorite error:', err.message);
      res.status(500).json({ message: 'Server error' });
    }
  },
};

module.exports = favoritesController;
