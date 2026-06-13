const express = require('express');
const router = express.Router();
const bookingsController = require('../controllers/bookingsController');
const authMiddleware = require('../middlewares/authMiddleware');

router.use(authMiddleware);

router.get('/', bookingsController.getByUser);
router.post('/', bookingsController.create);

module.exports = router;
