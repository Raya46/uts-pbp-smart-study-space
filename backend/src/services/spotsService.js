const spotRepository = require('../repositories/spotRepository');
const AppError = require('../utils/AppError');

const spotsService = {
  async getAll(filters) {
    return spotRepository.findAll(filters);
  },

  async getById(id) {
    const spot = await spotRepository.findById(id);
    if (!spot) {
      throw new AppError('Spot not found', 404);
    }
    return spot;
  },
};

module.exports = spotsService;
