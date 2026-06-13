const preferenceRepository = require('../repositories/preferenceRepository');
const AppError = require('../utils/AppError');

const preferencesService = {
  async get(userId) {
    const pref = await preferenceRepository.findByUserId(userId);
    return pref ? pref.preferences : [];
  },

  async update(userId, preferences) {
    await preferenceRepository.upsert(userId, preferences);
  },
};

module.exports = preferencesService;
