const express = require('express');
const router = express.Router();
const historyController = require('../controllers/history');
const checkAuth = require('../middleware/check-auth');

router.get('', historyController.getAllHistory);

router.get('/:id', historyController.getHistory);

router.get('/user/:id', historyController.getUserHistory);

router.post('', checkAuth, historyController.createHistory);

router.put('/:id', checkAuth, historyController.updateHistory);

router.delete('/:id', checkAuth, historyController.deleteHistory);

module.exports = router;
