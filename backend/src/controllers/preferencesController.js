const preferencesService = require('../services/preferencesService');

const preferencesController = {
  async get(req, res) {
    try {
      const preferences = await preferencesService.get(req.user.id);
      res.json({ preferences });
    } catch (err) {
      if (err.isOperational) {
        return res.status(err.statusCode).json({ message: err.message });
      }
      console.error('Get preferences error:', err.message);
      res.status(500).json({ message: 'Server error' });
    }
  },

  async update(req, res) {
    try {
      const { preferences } = req.body;
      await preferencesService.update(req.user.id, preferences);
      res.json({ message: 'Preferences saved successfully' });
    } catch (err) {
      if (err.isOperational) {
        return res.status(err.statusCode).json({ message: err.message });
      }
      console.error('Update preferences error:', err.message);
      res.status(500).json({ message: 'Server error' });
    }
  },
};

module.exports = preferencesController;
