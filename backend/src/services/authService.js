const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const userRepository = require('../repositories/userRepository');
const preferenceRepository = require('../repositories/preferenceRepository');
const AppError = require('../utils/AppError');

function generateToken(user) {
  return jwt.sign(
    { id: user.id, email: user.email },
    process.env.JWT_SECRET,
    { expiresIn: '7d' }
  );
}

const authService = {
  async registerStep1({ name, email, password }) {
    const existing = await userRepository.findByEmail(email);
    if (existing) {
      throw new AppError('Email already registered', 400);
    }

    const hashedPassword = await bcrypt.hash(password, 10);
    const user = await userRepository.create({
      name,
      email,
      password: hashedPassword,
    });

    return { userId: user.id };
  },

  async registerStep2(userId, { avatar, university, major, preferences }) {
    const user = await userRepository.findById(userId);
    if (!user) {
      throw new AppError('User not found', 404);
    }

    const updateData = {};
    if (avatar !== undefined) updateData.avatar = avatar;
    if (university !== undefined) updateData.university = university;
    if (major !== undefined) updateData.major = major;

    if (Object.keys(updateData).length > 0) {
      await userRepository.update(userId, updateData);
    }

    if (preferences && preferences.length > 0) {
      await preferenceRepository.upsert(userId, preferences);
    }

    const updatedUser = await userRepository.findById(userId);
    const token = generateToken(updatedUser);

    return {
      token,
      user: {
        id: updatedUser.id,
        name: updatedUser.name,
        email: updatedUser.email,
        avatar: updatedUser.avatar,
        university: updatedUser.university,
        major: updatedUser.major,
      },
    };
  },

  async login({ email, password }) {
    const user = await userRepository.findByEmail(email);
    if (!user) {
      throw new AppError('Invalid email or password', 401);
    }

    const validPassword = await bcrypt.compare(password, user.password);
    if (!validPassword) {
      throw new AppError('Invalid email or password', 401);
    }

    const token = generateToken(user);

    return {
      token,
      user: {
        id: user.id,
        name: user.name,
        email: user.email,
        avatar: user.avatar,
        university: user.university,
        major: user.major,
      },
    };
  },
};

module.exports = authService;
