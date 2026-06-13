CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    avatar VARCHAR(255) DEFAULT '',
    university VARCHAR(255) DEFAULT '',
    major VARCHAR(255) DEFAULT '',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS study_spots (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(100) NOT NULL,
    distance VARCHAR(50) DEFAULT '',
    rating DECIMAL(2,1) DEFAULT 0.0,
    reviews_count INTEGER DEFAULT 0,
    availability VARCHAR(100) DEFAULT '',
    image_url VARCHAR(255) DEFAULT '',
    tag VARCHAR(50) DEFAULT '',
    features TEXT[] DEFAULT '{}'
);

CREATE TABLE IF NOT EXISTS bookings (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    spot_id INTEGER REFERENCES study_spots(id) ON DELETE CASCADE,
    date VARCHAR(50) NOT NULL,
    time_slot VARCHAR(50) NOT NULL,
    seats INTEGER DEFAULT 1,
    status VARCHAR(50) DEFAULT 'Upcoming',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS study_preferences (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE UNIQUE,
    preferences TEXT[] DEFAULT '{}'
);

-- Seed data for study spots
INSERT INTO study_spots (name, category, distance, rating, reviews_count, availability, image_url, tag, features) VALUES
('Central Library', 'Library', '0.5 km', 4.8, 124, '15/50 seats available', 'bg_library', 'Quiet', ARRAY['WiFi', 'AC', 'Power']),
('Kampus Café', 'Cafe', '0.8 km', 4.8, 124, 'Limited seats available', 'img_1', 'Moderate', ARRAY['Coffee', 'WiFi', 'AC']),
('Working Hub', 'Working Space', '1.2 km', 4.5, 89, '20/30 seats available', 'img_2', 'Quiet', ARRAY['WiFi', 'Power', 'Printer'])
ON CONFLICT DO NOTHING;
