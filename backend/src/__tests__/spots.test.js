const request = require('supertest');
const app = require('../app');
const { prisma } = require('./setup');

afterAll(async () => {
  await prisma.$disconnect();
});

describe('Spots Endpoints — GET /api/spots', () => {
  it('should return all spots as an array', async () => {
    const res = await request(app).get('/api/spots');

    expect(res.status).toBe(200);
    expect(res.body).toHaveProperty('spots');
    expect(Array.isArray(res.body.spots)).toBe(true);
    expect(res.body.spots.length).toBeGreaterThanOrEqual(3);
  });

  it('should filter spots by category', async () => {
    const res = await request(app).get('/api/spots?category=Library');

    expect(res.status).toBe(200);
    expect(res.body.spots.length).toBeGreaterThanOrEqual(1);
    res.body.spots.forEach(spot => {
      expect(spot.category).toBe('Library');
    });
  });

  it('should search spots by name', async () => {
    const res = await request(app).get('/api/spots?search=Library');

    expect(res.status).toBe(200);
    expect(res.body.spots.length).toBeGreaterThanOrEqual(1);
    res.body.spots.forEach(spot => {
      expect(spot.name.toLowerCase()).toContain('library');
    });
  });

  it('should return empty array for non-matching category', async () => {
    const res = await request(app).get('/api/spots?category=NonExistent');

    expect(res.status).toBe(200);
    expect(res.body.spots).toEqual([]);
  });

  it('should return spots sorted by rating descending', async () => {
    const res = await request(app).get('/api/spots');

    expect(res.status).toBe(200);
    const ratings = res.body.spots.map(s => s.rating);
    for (let i = 1; i < ratings.length; i++) {
      expect(ratings[i]).toBeLessThanOrEqual(ratings[i - 1]);
    }
    ratings.forEach(r => {
      expect(typeof r).toBe('number');
    });
  });

  it('each spot should have required fields (snake_case)', async () => {
    const res = await request(app).get('/api/spots');

    expect(res.status).toBe(200);
    res.body.spots.forEach(spot => {
      expect(spot).toHaveProperty('id');
      expect(spot).toHaveProperty('name');
      expect(spot).toHaveProperty('category');
      expect(spot).toHaveProperty('distance');
      expect(spot).toHaveProperty('rating');
      expect(spot).toHaveProperty('image_url');
      expect(spot).toHaveProperty('tag');
      expect(spot).toHaveProperty('features');
      expect(spot).toHaveProperty('availability');
      expect(spot).toHaveProperty('reviews_count');
    });
  });

  it('rating should be a number, not a string', async () => {
    const res = await request(app).get('/api/spots');

    expect(res.status).toBe(200);
    res.body.spots.forEach(spot => {
      expect(typeof spot.rating).toBe('number');
    });
  });

  it('features should be an array', async () => {
    const res = await request(app).get('/api/spots');

    expect(res.status).toBe(200);
    res.body.spots.forEach(spot => {
      expect(Array.isArray(spot.features)).toBe(true);
    });
  });
});

describe('Spots Endpoints — GET /api/spots/:id', () => {
  it('should return spot detail by id', async () => {
    const listRes = await request(app).get('/api/spots');
    const firstSpot = listRes.body.spots[0];

    const res = await request(app).get(`/api/spots/${firstSpot.id}`);

    expect(res.status).toBe(200);
    expect(res.body).toHaveProperty('spot');
    expect(res.body.spot.id).toBe(firstSpot.id);
    expect(res.body.spot.name).toBe(firstSpot.name);
    expect(res.body.spot).toHaveProperty('features');
    expect(res.body.spot).toHaveProperty('reviews_count');
    expect(typeof res.body.spot.rating).toBe('number');
  });

  it('should return 404 for non-existent spot', async () => {
    const res = await request(app).get('/api/spots/99999');

    expect(res.status).toBe(404);
    expect(res.body).toHaveProperty('message', 'Spot not found');
  });

  it('should return 404 for invalid id (string)', async () => {
    const res = await request(app).get('/api/spots/invalid');

    expect(res.status).toBe(404);
    expect(res.body).toHaveProperty('message', 'Spot not found');
  });
});
