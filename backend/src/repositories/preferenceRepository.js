const prisma = require('../config/prisma');

const preferenceRepository = {
  async findByUserId(userId) {
    return prisma.studyPreference.findUnique({ where: { user_id: userId } });
  },

  async upsert(userId, preferences) {
    return prisma.studyPreference.upsert({
      where: { user_id: userId },
      update: { preferences },
      create: { user_id: userId, preferences },
    });
  },
};

module.exports = preferenceRepository;
