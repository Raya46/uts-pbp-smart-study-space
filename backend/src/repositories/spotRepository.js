const prisma = require('../config/prisma');

const spotRepository = {
  async findAll(filters = {}) {
    const where = {};
    if (filters.search) {
      where.name = { contains: filters.search, mode: 'insensitive' };
    }
    if (filters.category) {
      where.category = filters.category;
    }
    return prisma.studySpot.findMany({
      where,
      orderBy: { rating: 'desc' },
    });
  },

  async findById(id) {
    if (isNaN(id)) return null;
    return prisma.studySpot.findUnique({ where: { id } });
  },

  async updateSpotRating(id, avgRating, reviewCount) {
    return prisma.studySpot.update({
      where: { id },
      data: { rating: avgRating, reviews_count: reviewCount },
    });
  },
};

module.exports = spotRepository;
