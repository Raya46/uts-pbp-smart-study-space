const prisma = require('../config/prisma');

const reviewRepository = {
  async findByUserId(userId) {
    return prisma.review.findMany({
      where: { user_id: userId },
      include: {
        spot: {
          select: { id: true, name: true, category: true },
        },
      },
      orderBy: { created_at: 'desc' },
    });
  },

  async countByUserId(userId) {
    return prisma.review.count({ where: { user_id: userId } });
  },

  async findBySpotId(spotId) {
    return prisma.review.findMany({
      where: { spot_id: spotId },
      include: {
        user: { select: { id: true, name: true, avatar: true } },
      },
      orderBy: { created_at: 'desc' },
    });
  },

  async findByUserAndSpot(userId, spotId) {
    return prisma.review.findUnique({
      where: { user_id_spot_id: { user_id: userId, spot_id: spotId } },
    });
  },

  async create(userId, spotId, rating, comment) {
    return prisma.review.create({
      data: { user_id: userId, spot_id: spotId, rating, comment },
    });
  },

  async countBySpotId(spotId) {
    return prisma.review.count({ where: { spot_id: spotId } });
  },

  async avgRatingBySpotId(spotId) {
    const result = await prisma.review.aggregate({
      where: { spot_id: spotId },
      _avg: { rating: true },
    });
    return result._avg.rating;
  },
};

module.exports = reviewRepository;
