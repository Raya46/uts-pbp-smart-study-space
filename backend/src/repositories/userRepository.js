const prisma = require('../config/prisma');

const userRepository = {
  async findById(id) {
    return prisma.user.findUnique({
      where: { id },
      select: { id: true, name: true, email: true, avatar: true, university: true, major: true, created_at: true },
    });
  },

  async findByEmail(email) {
    return prisma.user.findUnique({ where: { email } });
  },

  async create(data) {
    return prisma.user.create({
      data,
      select: { id: true, name: true, email: true, avatar: true, university: true, major: true },
    });
  },

  async update(id, data) {
    return prisma.user.update({
      where: { id },
      data,
      select: { id: true, name: true, email: true, avatar: true, university: true, major: true },
    });
  },
};

module.exports = userRepository;
