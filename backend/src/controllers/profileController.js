const profileService = require('../services/profileService');

const profileController = {
  async get(req, res) {
    try {
      const user = await profileService.get(req.user.id);
      res.json({ user });
    } catch (err) {
      if (err.isOperational) {
        return res.status(err.statusCode).json({ message: err.message });
      }
      console.error('Get profile error:', err.message);
      res.status(500).json({ message: 'Server error' });
    }
  },

  async getStats(req, res) {
    try {
      const stats = await profileService.getStats(req.user.id);
      res.json(stats);
    } catch (err) {
      if (err.isOperational) {
        return res.status(err.statusCode).json({ message: err.message });
      }
      console.error('Get profile stats error:', err.message);
      res.status(500).json({ message: 'Server error' });
    }
  },

  async update(req, res) {
    try {
      const { name, email, avatar, university, major } = req.body;
      const user = await profileService.update(req.user.id, { name, email, avatar, university, major });
      res.json({ user, message: 'Profile updated successfully' });
    } catch (err) {
      if (err.isOperational) {
        return res.status(err.statusCode).json({ message: err.message });
      }
      console.error('Update profile error:', err.message);
      res.status(500).json({ message: 'Server error' });
    }
  },
};

module.exports = profileController;
