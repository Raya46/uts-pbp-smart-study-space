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
    data: {
      name: 'Profile Test',
      email: `profile_test_${Date.now()}@example.com`,
      password: hash,
      avatar: 'avatar_default',
      university: 'Test University',
      major: 'Test Major',
    },
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

describe('Profile Endpoints — GET /api/profile/:userId', () => {
  it('should return user profile', async () => {
    const res = await request(app)
      .get(`/api/profile/${testUserId}`)
      .set(auth());

    expect(res.status).toBe(200);
    expect(res.body).toHaveProperty('user');
    expect(res.body.user.id).toBe(testUserId);
    expect(res.body.user.name).toBe('Profile Test');
    expect(res.body.user.email).toContain('profile_test_');
    expect(res.body.user).toHaveProperty('avatar');
    expect(res.body.user).toHaveProperty('university');
    expect(res.body.user).toHaveProperty('major');
  });

  it('should return 401 without auth token', async () => {
    const res = await request(app).get(`/api/profile/${testUserId}`);

    expect(res.status).toBe(401);
  });

  it('should not expose password field', async () => {
    const res = await request(app)
      .get(`/api/profile/${testUserId}`)
      .set(auth());

    expect(res.status).toBe(200);
    expect(res.body.user).not.toHaveProperty('password');
  });
});

describe('Profile Endpoints — PUT /api/profile/:userId', () => {
  it('should update user profile partially', async () => {
    const res = await request(app)
      .put(`/api/profile/${testUserId}`)
      .set(auth())
      .send({ name: 'Updated Name', university: 'Updated University' });

    expect(res.status).toBe(200);
    expect(res.body).toHaveProperty('message', 'Profile updated successfully');
    expect(res.body.user.name).toBe('Updated Name');
    expect(res.body.user.university).toBe('Updated University');
    expect(res.body.user.major).toBe('Test Major');
  });

  it('should update all profile fields', async () => {
    const res = await request(app)
      .put(`/api/profile/${testUserId}`)
      .set(auth())
      .send({
        name: 'Full Update',
        email: `full_update_${Date.now()}@example.com`,
        avatar: 'avatar_new',
        university: 'New University',
        major: 'New Major',
      });

    expect(res.status).toBe(200);
    expect(res.body.user.name).toBe('Full Update');
    expect(res.body.user.avatar).toBe('avatar_new');
  });

  it('should return 401 without auth token', async () => {
    const res = await request(app)
      .put(`/api/profile/${testUserId}`)
      .send({ name: 'Ghost' });

    expect(res.status).toBe(401);
  });

  it('should not expose password after update', async () => {
    const res = await request(app)
      .put(`/api/profile/${testUserId}`)
      .set(auth())
      .send({ name: 'No Password' });

    expect(res.status).toBe(200);
    expect(res.body.user).not.toHaveProperty('password');
  });

  it('should reject duplicate email', async () => {
    const bcrypt = require('bcryptjs');
    const hash = await bcrypt.hash('TestPass123', 10);
    const otherUser = await prisma.user.create({
      data: { name: 'Other', email: `other_${Date.now()}@example.com`, password: hash },
    });

    const res = await request(app)
      .put(`/api/profile/${testUserId}`)
      .set(auth())
      .send({ email: otherUser.email });

    expect(res.status).toBe(409);

    await prisma.user.delete({ where: { id: otherUser.id } }).catch(() => {});
  });
});
