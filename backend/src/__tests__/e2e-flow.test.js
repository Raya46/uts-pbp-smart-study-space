const request = require('supertest');
const app = require('../app');
const { prisma } = require('./setup');

let userId = null;
let spotId = null;
let bookingId = null;
let authToken = null;
const uniqueEmail = `e2e_${Date.now()}@example.com`;

afterAll(async () => {
  if (userId) {
    await prisma.studyPreference.deleteMany({ where: { user_id: userId } }).catch(() => {});
    await prisma.booking.deleteMany({ where: { user_id: userId } }).catch(() => {});
    await prisma.user.delete({ where: { id: userId } }).catch(() => {});
  }
  await prisma.$disconnect();
});

function auth() {
  return { Authorization: `Bearer ${authToken}` };
}

describe('Full E2E Flow — Register → Complete Profile → Login → Browse Spots → Book → View Bookings', () => {
  it('Step 1: Health check', async () => {
    const res = await request(app).get('/');
    expect(res.status).toBe(200);
    expect(res.body).toHaveProperty('message', 'Welcome to Smart Study Space API');
  });

  it('Step 2: Register step 1', async () => {
    const res = await request(app)
      .post('/api/auth/register-step1')
      .send({ name: 'E2E User', email: uniqueEmail, password: 'E2EPass123' });

    expect(res.status).toBe(201);
    expect(res.body).toHaveProperty('userId');
    userId = res.body.userId;
  });

  it('Step 3: Complete registration step 2', async () => {
    const res = await request(app)
      .put(`/api/auth/register-step2/${userId}`)
      .send({
        avatar: 'avatar_e2e',
        university: 'E2E University',
        major: 'Software Engineering',
        preferences: ['Quiet', 'WiFi'],
      });

    expect(res.status).toBe(200);
    expect(res.body).toHaveProperty('token');
    expect(res.body).toHaveProperty('user');
    expect(res.body).toHaveProperty('message', 'Registration complete');
    authToken = res.body.token;
  });

  it('Step 4: Login', async () => {
    const res = await request(app)
      .post('/api/auth/login')
      .send({ email: uniqueEmail, password: 'E2EPass123' });

    expect(res.status).toBe(200);
    expect(res.body).toHaveProperty('token');
    expect(res.body).toHaveProperty('user');
    expect(res.body.user.id).toBe(userId);
    expect(res.body.user.university).toBe('E2E University');
    expect(res.body.user.major).toBe('Software Engineering');
    authToken = res.body.token;
  });

  it('Step 5: Browse all spots', async () => {
    const res = await request(app).get('/api/spots');

    expect(res.status).toBe(200);
    expect(res.body.spots.length).toBeGreaterThanOrEqual(1);
    spotId = res.body.spots[0].id;
  });

  it('Step 6: Search spots by category', async () => {
    const res = await request(app).get(`/api/spots?category=Library`);

    expect(res.status).toBe(200);
    if (res.body.spots.length > 0) {
      expect(res.body.spots[0].category).toBe('Library');
    }
  });

  it('Step 7: View spot detail', async () => {
    const res = await request(app).get(`/api/spots/${spotId}`);

    expect(res.status).toBe(200);
    expect(res.body.spot.id).toBe(spotId);
    expect(res.body.spot).toHaveProperty('features');
    expect(Array.isArray(res.body.spot.features)).toBe(true);
  });

  it('Step 8: Create a booking (with auth)', async () => {
    const res = await request(app)
      .post('/api/bookings')
      .set(auth())
      .send({
        spotId,
        date: '2026-06-25',
        timeSlot: '09:00-11:00',
        seats: 2,
      });

    expect(res.status).toBe(201);
    expect(res.body.booking).toHaveProperty('id');
    bookingId = res.body.booking.id;
  });

  it('Step 9: View active bookings (with auth)', async () => {
    const res = await request(app)
      .get('/api/bookings?status=active')
      .set(auth());

    expect(res.status).toBe(200);
    expect(res.body.bookings.length).toBeGreaterThanOrEqual(1);
    const found = res.body.bookings.some(b => b.id === bookingId);
    expect(found).toBe(true);
  });

  it('Step 10: View profile (with auth)', async () => {
    const res = await request(app)
      .get(`/api/profile/${userId}`)
      .set(auth());

    expect(res.status).toBe(200);
    expect(res.body.user.name).toBe('E2E User');
    expect(res.body.user.major).toBe('Software Engineering');
  });

  it('Step 11: Update profile (with auth)', async () => {
    const res = await request(app)
      .put(`/api/profile/${userId}`)
      .set(auth())
      .send({ name: 'E2E Updated', major: 'Data Science' });

    expect(res.status).toBe(200);
    expect(res.body.user.name).toBe('E2E Updated');
    expect(res.body.user.major).toBe('Data Science');
  });

  it('Step 12: View preferences (with auth)', async () => {
    const res = await request(app)
      .get(`/api/preferences/${userId}`)
      .set(auth());

    expect(res.status).toBe(200);
    expect(res.body.preferences).toEqual(['Quiet', 'WiFi']);
  });

  it('Step 13: Update preferences (with auth)', async () => {
    const res = await request(app)
      .put(`/api/preferences/${userId}`)
      .set(auth())
      .send({ preferences: ['Library', 'AC', 'Power'] });

    expect(res.status).toBe(200);

    const getRes = await request(app)
      .get(`/api/preferences/${userId}`)
      .set(auth());
    expect(getRes.body.preferences).toEqual(['Library', 'AC', 'Power']);
  });

  it('Step 14: Verify updated profile persisted', async () => {
    const res = await request(app)
      .get(`/api/profile/${userId}`)
      .set(auth());

    expect(res.status).toBe(200);
    expect(res.body.user.name).toBe('E2E Updated');
    expect(res.body.user.major).toBe('Data Science');
  });

  it('Step 15: Login fails with wrong password', async () => {
    const res = await request(app)
      .post('/api/auth/login')
      .send({ email: uniqueEmail, password: 'WrongPassword' });

    expect(res.status).toBe(401);
  });
});
