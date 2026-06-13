const request = require('supertest');
const app = require('../app');
const { prisma } = require('./setup');

const TEST_EMAIL = `auth_test_${Date.now()}@example.com`;
const TEST_PASSWORD = 'TestPass123';
const TEST_NAME = 'Auth Test User';
let userId = null;

afterAll(async () => {
  if (userId) {
    await prisma.studyPreference.deleteMany({ where: { user_id: userId } }).catch(() => {});
    await prisma.booking.deleteMany({ where: { user_id: userId } }).catch(() => {});
    await prisma.user.delete({ where: { id: userId } }).catch(() => {});
  }
  await prisma.$disconnect();
});

describe('Auth Endpoints — POST /api/auth/register-step1', () => {
  it('should register a new user and return userId', async () => {
    const res = await request(app)
      .post('/api/auth/register-step1')
      .send({ name: TEST_NAME, email: TEST_EMAIL, password: TEST_PASSWORD });

    expect(res.status).toBe(201);
    expect(res.body).toHaveProperty('userId');
    expect(res.body).toHaveProperty('message', 'Step 1 complete');
    expect(typeof res.body.userId).toBe('number');
    userId = res.body.userId;
  });

  it('should return 400 if fields are missing', async () => {
    const res = await request(app)
      .post('/api/auth/register-step1')
      .send({ name: 'No Email' });

    expect(res.status).toBe(400);
    expect(res.body).toHaveProperty('message', 'All fields are required');
  });

  it('should return 400 if email already registered', async () => {
    const res = await request(app)
      .post('/api/auth/register-step1')
      .send({ name: TEST_NAME, email: TEST_EMAIL, password: TEST_PASSWORD });

    expect(res.status).toBe(400);
    expect(res.body).toHaveProperty('message', 'Email already registered');
  });

  it('should return 400 if name is empty string', async () => {
    const res = await request(app)
      .post('/api/auth/register-step1')
      .send({ name: '', email: 'newtest@example.com', password: TEST_PASSWORD });
    expect(res.status).toBe(400);
  });

  it('should return 400 if password is empty string', async () => {
    const res = await request(app)
      .post('/api/auth/register-step1')
      .send({ name: 'Test', email: 'newtest@example.com', password: '' });
    expect(res.status).toBe(400);
  });
});

describe('Auth Endpoints — PUT /api/auth/register-step2/:userId', () => {
  it('should complete registration with profile and preferences', async () => {
    const res = await request(app)
      .put(`/api/auth/register-step2/${userId}`)
      .send({
        avatar: 'avatar_default',
        university: 'Test University',
        major: 'Computer Science',
        preferences: ['Quiet', 'WiFi', 'AC'],
      });

    expect(res.status).toBe(200);
    expect(res.body).toHaveProperty('token');
    expect(res.body).toHaveProperty('user');
    expect(res.body.user).toHaveProperty('id', userId);
    expect(res.body.user).toHaveProperty('name');
    expect(res.body.user).toHaveProperty('email');
    expect(res.body).toHaveProperty('message', 'Registration complete');
  });

  it('should succeed with minimal data', async () => {
    const res = await request(app)
      .put(`/api/auth/register-step2/${userId}`)
      .send({});

    expect(res.status).toBe(200);
    expect(res.body).toHaveProperty('token');
    expect(res.body).toHaveProperty('user');
  });

  it('should return 404 for non-existent userId', async () => {
    const res = await request(app)
      .put('/api/auth/register-step2/99999')
      .send({ avatar: 'test' });

    expect(res.status).toBe(404);
  });
});

describe('Auth Endpoints — POST /api/auth/login', () => {
  it('should login with correct credentials and return token + user', async () => {
    const res = await request(app)
      .post('/api/auth/login')
      .send({ email: TEST_EMAIL, password: TEST_PASSWORD });

    expect(res.status).toBe(200);
    expect(res.body).toHaveProperty('token');
    expect(res.body).toHaveProperty('user');
    expect(res.body.user).toHaveProperty('id', userId);
    expect(res.body.user).toHaveProperty('name', TEST_NAME);
    expect(res.body.user).toHaveProperty('email', TEST_EMAIL);
    expect(res.body.user).toHaveProperty('university', 'Test University');
    expect(res.body.user).toHaveProperty('major', 'Computer Science');
  });

  it('should fail with wrong password', async () => {
    const res = await request(app)
      .post('/api/auth/login')
      .send({ email: TEST_EMAIL, password: 'WrongPassword' });

    expect(res.status).toBe(401);
    expect(res.body).toHaveProperty('message', 'Invalid email or password');
  });

  it('should fail with non-existent email', async () => {
    const res = await request(app)
      .post('/api/auth/login')
      .send({ email: 'nonexistent@example.com', password: TEST_PASSWORD });

    expect(res.status).toBe(401);
    expect(res.body).toHaveProperty('message', 'Invalid email or password');
  });

  it('should return 400 if email is missing', async () => {
    const res = await request(app)
      .post('/api/auth/login')
      .send({ password: TEST_PASSWORD });

    expect(res.status).toBe(400);
    expect(res.body).toHaveProperty('message');
  });

  it('should return 400 if password is missing', async () => {
    const res = await request(app)
      .post('/api/auth/login')
      .send({ email: TEST_EMAIL });

    expect(res.status).toBe(400);
    expect(res.body).toHaveProperty('message');
  });
});
