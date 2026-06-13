const spotsService = require('../services/spotsService');

const spotsController = {
  async getAll(req, res) {
    try {
      const { search, category } = req.query;
      const spots = await spotsService.getAll({ search, category });
      res.json({ spots });
    } catch (err) {
      console.error('Get spots error:', err.message);
      res.status(500).json({ message: 'Server error' });
    }
  },

  async getById(req, res) {
    try {
      const { id } = req.params;
      const spot = await spotsService.getById(parseInt(id, 10));
      res.json({ spot });
    } catch (err) {
      if (err.isOperational) {
        return res.status(err.statusCode).json({ message: err.message });
      }
      console.error('Get spot detail error:', err.message);
      res.status(500).json({ message: 'Server error' });
    }
  },
};

module.exports = spotsController;
