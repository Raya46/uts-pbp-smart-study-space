const express = require('express');
const router = express.Router();
const favoritesController = require('../controllers/favoritesController');
const authMiddleware = require('../middlewares/authMiddleware');

router.use(authMiddleware);

router.get('/', favoritesController.getAll);
router.post('/toggle', favoritesController.toggle);
router.get('/check/:spotId', favoritesController.check);

module.exports = router;
