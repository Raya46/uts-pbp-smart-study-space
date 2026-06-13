const prisma = require('../config/prisma');

const favoriteRepository = {
  async findByUserId(userId) {
    return prisma.favorite.findMany({
      where: { user_id: userId },
      include: {
        spot: {
          select: { id: true, name: true, category: true, image_url: true, tag: true, rating: true },
        },
      },
      orderBy: { created_at: 'desc' },
    });
  },

  async countByUserId(userId) {
    return prisma.favorite.count({ where: { user_id: userId } });
  },

  async findByUserAndSpot(userId, spotId) {
    return prisma.favorite.findUnique({
      where: { user_id_spot_id: { user_id: userId, spot_id: spotId } },
    });
  },

  async create(userId, spotId) {
    return prisma.favorite.create({
      data: { user_id: userId, spot_id: spotId },
    });
  },

  async delete(userId, spotId) {
    return prisma.favorite.delete({
      where: { user_id_spot_id: { user_id: userId, spot_id: spotId } },
    });
  },
};

module.exports = favoriteRepository;
