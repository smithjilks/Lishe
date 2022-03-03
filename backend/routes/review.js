const express = require('express');
const router = express.Router();
const reviewController = require('../controllers/review');
const checkAuth = require('../middleware/check-auth');

router.get('', reviewController.getReviews);

router.get('/:id', reviewController.getReview);

router.get('/user/:id', reviewController.getUserReviews);

router.post('', checkAuth, reviewController.createReview);

router.put('/:id', checkAuth, reviewController.updateReview);

router.delete('/:id', checkAuth, reviewController.deleteReview);

module.exports = router;
