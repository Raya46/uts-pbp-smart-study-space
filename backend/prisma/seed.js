const { PrismaClient } = require('@prisma/client');

const prisma = new PrismaClient();

async function main() {
  const existingSpots = await prisma.studySpot.count();
  if (existingSpots > 0) {
    console.log('Seed data already exists, skipping...');
    return;
  }

  await prisma.studySpot.createMany({
    data: [
      {
        name: 'Central Library',
        category: 'Library',
        distance: '0.5 km',
        rating: 4.8,
        reviews_count: 124,
        availability: '15/50 seats available',
        image_url: 'bg_library',
        tag: 'Quiet',
        features: ['WiFi', 'AC', 'Power'],
        latitude: -6.2564,
        longitude: 106.7986,
      },
      {
        name: 'Kampus Café',
        category: 'Cafe',
        distance: '0.8 km',
        rating: 4.8,
        reviews_count: 124,
        availability: 'Limited seats available',
        image_url: 'img_1',
        tag: 'Moderate',
        features: ['Coffee', 'WiFi', 'AC'],
        latitude: -6.2550,
        longitude: 106.8000,
      },
      {
        name: 'Working Hub',
        category: 'Working Space',
        distance: '1.2 km',
        rating: 4.5,
        reviews_count: 89,
        availability: '20/30 seats available',
        image_url: 'img_2',
        tag: 'Quiet',
        features: ['WiFi', 'Power', 'Printer'],
        latitude: -6.2600,
        longitude: 106.7950,
      },
    ],
  });

  console.log('Seed data inserted successfully');
}

main()
  .catch((e) => {
    console.error('Seed error:', e);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
  });
