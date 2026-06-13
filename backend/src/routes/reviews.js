const express = require('express');
const router = express.Router();
const reviewsController = require('../controllers/reviewsController');
const authMiddleware = require('../middlewares/authMiddleware');

router.get('/spot/:spotId', reviewsController.getBySpot);

router.post('/', authMiddleware, reviewsController.create);

module.exports = router;
