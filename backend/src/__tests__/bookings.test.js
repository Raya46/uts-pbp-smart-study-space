const request = require('supertest');
const app = require('../app');
const { prisma } = require('./setup');

let testUserId = null;
let testSpotId = null;
let authToken = null;

beforeAll(async () => {
  const bcrypt = require('bcryptjs');
  const jwt = require('jsonwebtoken');
  const hash = await bcrypt.hash('TestPass123', 10);
  const user = await prisma.user.create({
    data: { name: 'Booking Test', email: `booking_test_${Date.now()}@example.com`, password: hash },
  });
  testUserId = user.id;
  authToken = jwt.sign({ id: user.id, email: user.email }, process.env.JWT_SECRET, { expiresIn: '7d' });

  const spots = await prisma.studySpot.findMany({ take: 1 });
  testSpotId = spots[0].id;
});

afterAll(async () => {
  if (testUserId) {
    await prisma.booking.deleteMany({ where: { user_id: testUserId } }).catch(() => {});
    await prisma.user.delete({ where: { id: testUserId } }).catch(() => {});
  }
  await prisma.$disconnect();
});

function auth() {
  return { Authorization: `Bearer ${authToken}` };
}

describe('Bookings Endpoints — POST /api/bookings', () => {
  it('should create a new booking', async () => {
    const res = await request(app)
      .post('/api/bookings')
      .set(auth())
      .send({
        spotId: testSpotId,
        date: '2026-06-20',
        timeSlot: '10:00-12:00',
        seats: 2,
      });

    expect(res.status).toBe(201);
    expect(res.body).toHaveProperty('booking');
    expect(res.body.booking).toHaveProperty('id');
    expect(typeof res.body.booking.id).toBe('number');
    expect(res.body).toHaveProperty('message', 'Booking created successfully');
  });

  it('should fail if fields are missing', async () => {
    const res = await request(app)
      .post('/api/bookings')
      .set(auth())
      .send({ spotId: testSpotId });

    expect(res.status).toBe(400);
    expect(res.body).toHaveProperty('message', 'All fields are required');
  });

  it('should create a booking with single seat', async () => {
    const res = await request(app)
      .post('/api/bookings')
      .set(auth())
      .send({
        spotId: testSpotId,
        date: '2026-06-21',
        timeSlot: '14:00-16:00',
        seats: 1,
      });

    expect(res.status).toBe(201);
  });

  it('should fail without auth token', async () => {
    const res = await request(app)
      .post('/api/bookings')
      .send({
        spotId: testSpotId,
        date: '2026-06-20',
        timeSlot: '10:00-12:00',
        seats: 1,
      });

    expect(res.status).toBe(401);
    expect(res.body).toHaveProperty('message', 'No token provided');
  });
});

describe('Bookings Endpoints — GET /api/bookings', () => {
  it('should return bookings for authenticated user', async () => {
    const res = await request(app)
      .get('/api/bookings')
      .set(auth());

    expect(res.status).toBe(200);
    expect(res.body).toHaveProperty('bookings');
    expect(Array.isArray(res.body.bookings)).toBe(true);
    expect(res.body.bookings.length).toBeGreaterThanOrEqual(1);
  });

  it('should filter bookings by status=active', async () => {
    const res = await request(app)
      .get('/api/bookings?status=active')
      .set(auth());

    expect(res.status).toBe(200);
    expect(Array.isArray(res.body.bookings)).toBe(true);
    res.body.bookings.forEach(b => {
      expect(['Upcoming', 'Active']).toContain(b.status);
    });
  });

  it('should filter bookings by status=previous', async () => {
    const res = await request(app)
      .get('/api/bookings?status=previous')
      .set(auth());

    expect(res.status).toBe(200);
    expect(Array.isArray(res.body.bookings)).toBe(true);
  });

  it('should return 401 without auth token', async () => {
    const res = await request(app).get('/api/bookings');

    expect(res.status).toBe(401);
  });

  it('each booking should have required fields', async () => {
    const res = await request(app)
      .get('/api/bookings')
      .set(auth());

    expect(res.status).toBe(200);
    if (res.body.bookings.length > 0) {
      res.body.bookings.forEach(b => {
        expect(b).toHaveProperty('id');
        expect(b).toHaveProperty('spot_name');
        expect(b).toHaveProperty('category');
        expect(b).toHaveProperty('date');
        expect(b).toHaveProperty('time_slot');
        expect(b).toHaveProperty('seats');
        expect(b).toHaveProperty('status');
        expect(b).toHaveProperty('image_url');
        expect(b).toHaveProperty('tag');
      });
    }
  });
});
