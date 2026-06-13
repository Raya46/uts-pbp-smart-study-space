const authService = require('../services/authService');

const authController = {
  async registerStep1(req, res) {
    try {
      const { name, email, password } = req.body;
      if (!name || !email || !password) {
        return res.status(400).json({ message: 'All fields are required' });
      }
      const result = await authService.registerStep1({ name, email, password });
      res.status(201).json({ userId: result.userId, message: 'Step 1 complete' });
    } catch (err) {
      if (err.isOperational) {
        return res.status(err.statusCode).json({ message: err.message });
      }
      console.error('Register step1 error:', err.message);
      res.status(500).json({ message: 'Server error' });
    }
  },

  async registerStep2(req, res) {
    try {
      const { userId } = req.params;
      const { avatar, university, major, preferences } = req.body;
      const result = await authService.registerStep2(parseInt(userId, 10), { avatar, university, major, preferences });
      res.json({ token: result.token, user: result.user, message: 'Registration complete' });
    } catch (err) {
      if (err.isOperational) {
        return res.status(err.statusCode).json({ message: err.message });
      }
      console.error('Register step2 error:', err.message);
      res.status(500).json({ message: 'Server error' });
    }
  },

  async login(req, res) {
    try {
      const { email, password } = req.body;
      if (!email || !password) {
        return res.status(400).json({ message: 'Email and password are required' });
      }
      const result = await authService.login({ email, password });
      res.json(result);
    } catch (err) {
      if (err.isOperational) {
        return res.status(err.statusCode).json({ message: err.message });
      }
      console.error('Login error:', err.message);
      res.status(500).json({ message: 'Server error' });
    }
  },
};

module.exports = authController;
