const prisma = require('../config/prisma');

const bookingRepository = {
  async findByUserId(userId, statusFilter) {
    const where = { user_id: userId };

    if (statusFilter === 'active') {
      where.status = { in: ['Upcoming', 'Active'] };
    } else if (statusFilter === 'previous') {
      where.status = { in: ['Completed', 'Cancelled'] };
    }

    return prisma.booking.findMany({
      where,
      include: {
        spot: {
          select: { name: true, category: true, image_url: true, tag: true },
        },
      },
      orderBy: { created_at: 'desc' },
    });
  },

  async create(data) {
    return prisma.booking.create({
      data,
      select: { id: true },
    });
  },
};

module.exports = bookingRepository;
