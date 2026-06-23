const prisma = require('../config/prisma');

const bookingRepository = {

  async findByUserId(userId, statusFilter) {
    const where = {
      user_id: userId,
    };

    if (statusFilter === 'active') {
      where.status = {
        in: ['Upcoming', 'Active'],
      };
    } else if (statusFilter === 'previous') {
      where.status = {
        in: ['Completed', 'Cancelled'],
      };
    }

    return prisma.booking.findMany({
      where,
      include: {
        spot: {
          select: {
            name: true,
            category: true,
            image_url: true,
            tag: true,
          },
        },
      },
      orderBy: {
        created_at: 'desc',
      },
    });
  },

  async findById(id, userId) {
    return prisma.booking.findFirst({
      where: {
        id,
        user_id: userId,
      },
      include: {
        spot: {
          select: {
            name: true,
            category: true,
            image_url: true,
            tag: true,
          },
        },
      },
    });
  },

  async create(data) {
    return prisma.booking.create({
      data,
      select: {
        id: true,
      },
    });
  },

  async updateStatus(id, userId, status) {
    return prisma.booking.updateMany({
      where: {
        id,
        user_id: userId,
      },
      data: {
        status,
      },
    });
  },

  async cancelBooking(id, userId) {
    return prisma.booking.updateMany({
      where: {
        id,
        user_id: userId,
      },
      data: {
        status: 'Cancelled',
      },
    });
  },
};

module.exports = bookingRepository;
