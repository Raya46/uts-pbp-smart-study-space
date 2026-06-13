const request = require('supertest');
const app = require('../app');
const { prisma } = require('./setup');

let testUserId = null;
let authToken = null;

beforeAll(async () => {
  const bcrypt = require('bcryptjs');
  const jwt = require('jsonwebtoken');
  const hash = await bcrypt.hash('TestPass123', 10);
  const user = await prisma.user.create({
    data: { name: 'Pref Test', email: `pref_test_${Date.now()}@example.com`, password: hash },
  });
  testUserId = user.id;
  authToken = jwt.sign({ id: user.id, email: user.email }, process.env.JWT_SECRET, { expiresIn: '7d' });
});

afterAll(async () => {
  if (testUserId) {
    await prisma.studyPreference.deleteMany({ where: { user_id: testUserId } }).catch(() => {});
    await prisma.booking.deleteMany({ where: { user_id: testUserId } }).catch(() => {});
    await prisma.user.delete({ where: { id: testUserId } }).catch(() => {});
  }
  await prisma.$disconnect();
});

function auth() {
  return { Authorization: `Bearer ${authToken}` };
}

describe('Preferences Endpoints — GET /api/preferences/:userId', () => {
  it('should return empty array for new user', async () => {
    const res = await request(app)
      .get(`/api/preferences/${testUserId}`)
      .set(auth());

    expect(res.status).toBe(200);
    expect(res.body).toHaveProperty('preferences');
    expect(Array.isArray(res.body.preferences)).toBe(true);
    expect(res.body.preferences).toEqual([]);
  });

  it('should return 401 without auth token', async () => {
    const res = await request(app).get(`/api/preferences/${testUserId}`);

    expect(res.status).toBe(401);
  });
});

describe('Preferences Endpoints — PUT /api/preferences/:userId', () => {
  it('should save preferences', async () => {
    const res = await request(app)
      .put(`/api/preferences/${testUserId}`)
      .set(auth())
      .send({ preferences: ['Quiet', 'WiFi', 'AC'] });

    expect(res.status).toBe(200);
    expect(res.body).toHaveProperty('message', 'Preferences saved successfully');
  });
});

describe('Preferences Endpoints — GET after PUT', () => {
  it('should return saved preferences', async () => {
    const res = await request(app)
      .get(`/api/preferences/${testUserId}`)
      .set(auth());

    expect(res.status).toBe(200);
    expect(res.body.preferences).toEqual(['Quiet', 'WiFi', 'AC']);
  });

  it('should update existing preferences', async () => {
    const res1 = await request(app)
      .put(`/api/preferences/${testUserId}`)
      .set(auth())
      .send({ preferences: ['Library', 'Power', 'Coffee'] });

    expect(res1.status).toBe(200);

    const res2 = await request(app)
      .get(`/api/preferences/${testUserId}`)
      .set(auth());
    expect(res2.body.preferences).toEqual(['Library', 'Power', 'Coffee']);
  });

  it('should save empty preferences', async () => {
    const res = await request(app)
      .put(`/api/preferences/${testUserId}`)
      .set(auth())
      .send({ preferences: [] });

    expect(res.status).toBe(200);

    const getRes = await request(app)
      .get(`/api/preferences/${testUserId}`)
      .set(auth());
    expect(getRes.body.preferences).toEqual([]);
  });

  it('should return 401 without auth token', async () => {
    const res = await request(app)
      .put(`/api/preferences/${testUserId}`)
      .send({ preferences: ['Test'] });

    expect(res.status).toBe(401);
  });
});
