const express = require('express');
const router = express.Router();
const profileController = require('../controllers/profileController');
const authMiddleware = require('../middlewares/authMiddleware');

router.use(authMiddleware);

router.get('/', profileController.get);
router.get('/stats', profileController.getStats);
router.put('/', profileController.update);
router.get('/:userId', profileController.get);
router.put('/:userId', profileController.update);

module.exports = router;
