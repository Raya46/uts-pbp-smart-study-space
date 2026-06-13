const { PrismaClient } = require('@prisma/client');
const prisma = new PrismaClient();

const TEST_EMAIL = `test_${Date.now()}@example.com`;
const TEST_PASSWORD = 'TestPass123';
const TEST_NAME = 'Test User';

let testUserId = null;
let authToken = null;

async function createTestUser() {
  const bcrypt = require('bcryptjs');
  const hashedPassword = await bcrypt.hash(TEST_PASSWORD, 10);
  const user = await prisma.user.create({
    data: { name: TEST_NAME, email: TEST_EMAIL, password: hashedPassword },
  });
  testUserId = user.id;
  return user;
}

async function cleanupTestUser() {
  if (testUserId) {
    await prisma.studyPreference.deleteMany({ where: { userId: testUserId } });
    await prisma.booking.deleteMany({ where: { userId: testUserId } });
    await prisma.user.delete({ where: { id: testUserId } }).catch(() => {});
    testUserId = null;
  }
}

function getTestUserId() {
  return testUserId;
}

function setAuthToken(token) {
  authToken = token;
}

function getAuthToken() {
  return authToken;
}

module.exports = {
  prisma,
  TEST_EMAIL,
  TEST_PASSWORD,
  TEST_NAME,
  createTestUser,
  cleanupTestUser,
  getTestUserId,
  setAuthToken,
  getAuthToken,
};
