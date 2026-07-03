const express = require('express');
const router = express.Router();

const bookingsController = require('../controllers/bookingsController');
const authMiddleware = require('../middlewares/authMiddleware');

router.use(authMiddleware);
router.get('/', bookingsController.getByUser);
router.get('/:id', bookingsController.getDetail);
router.post('/', bookingsController.create);
router.patch('/:id/check-in', bookingsController.checkIn);
router.patch('/:id/end', bookingsController.endBooking);
router.patch('/:id/cancel', bookingsController.cancelBooking);

module.exports = router;