const express = require('express');
const cors = require('cors');
require('dotenv').config();

const app = express();

// Middleware
app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Basic Route
app.get('/', (req, res) => {
    res.json({ message: 'Welcome to Smart Study Space API' });
});

// Routes (to be added later)
// const spotRoutes = require('./routes/spotRoutes');
// app.use('/api/spots', spotRoutes);

module.exports = app;
