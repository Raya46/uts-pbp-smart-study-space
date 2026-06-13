const express = require('express');
const router = express.Router();
const spotsController = require('../controllers/spotsController');

router.get('/', spotsController.getAll);
router.get('/:id', spotsController.getById);

module.exports = router;
