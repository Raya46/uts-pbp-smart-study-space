const express = require('express');
const router = express.Router();
const preferencesController = require('../controllers/preferencesController');
const authMiddleware = require('../middlewares/authMiddleware');

router.use(authMiddleware);

router.get('/', preferencesController.get);
router.put('/', preferencesController.update);
router.get('/:userId', preferencesController.get);
router.put('/:userId', preferencesController.update);

module.exports = router;
