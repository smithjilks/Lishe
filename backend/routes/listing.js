const express = require('express');
const router = express.Router();
const listingController = require('../controllers/listing');
const extractFile = require('../middleware/file')('listings');
const checkAuth = require('../middleware/check-auth');

router.get('', listingController.getListings);

router.get('/:id', listingController.getListing);

router.get('/user/:id', listingController.getUserListings);

router.post('', checkAuth, extractFile, listingController.createListing);

router.put('/:id', checkAuth, extractFile, listingController.updateListing);

router.delete('/:id', checkAuth, listingController.deleteListing);

module.exports = router;
