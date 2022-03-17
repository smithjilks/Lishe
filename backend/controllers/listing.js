const Listing = require('../models/listing');

exports.getListings = (req, res, next) => {
  const pageSize = +req.query.pagesize;
  const currentPage = +req.query.page;
  const listingQuery = Listing.find();
  let fetchedListings;

  if (pageSize && currentPage) {
    listingQuery
      .sort({ createdAt: 1 })

      .skip(pageSize * (currentPage - 1))

      .limit(pageSize);
  }

  listingQuery
    .then(documents => {
      fetchedListings = documents;

      fetchedListings.map(fetchedListing => {
        const date = fetchedListing.expiration;
        const formatedDate = new Date(date).getDate() +
          '-' + (new Date(date).getMonth() + 1) +
          '-' + new Date(date).getFullYear();
        fetchedListing._doc.expiration = formatedDate;
        return fetchedListing;
      });
      return Listing.countDocuments();
    })

    .then(count => {
      res.status(200).json(fetchedListings);
    })

    .catch(error => {
      res.status(500).json({
        message: 'Fetching listings failed!',
        error: error
      });
    });
};

exports.getListing = (req, res, next) => {
  Listing
    .findById(req.params.id)

    .then(listing => {
      if (listing) {
        res.status(200).json(listing);
      } else {
        res.status(404).json({
          message: 'Listing does not exist'
        });
      }
    })

    .catch(error => {
      res.status(500).json({
        message: 'Fetching listing failed!',
        error: error
      });
    });
};

exports.getUserListings = (req, res, next) => {
  const pageSize = +req.query.pagesize;
  const currentPage = +req.query.page;
  const listingQuery = Listing.find({ creator: req.params.id });
  let fetchedListings;

  if (pageSize && currentPage) {
    listingQuery
      .sort({ createdAt: 1 })

      .skip(pageSize * (currentPage - 1))

      .limit(pageSize);
  }

  listingQuery
    .then(documents => {
      fetchedListings = documents;
      return fetchedListings.length;
    })

    .then(count => {
      res.status(200).json(fetchedListings);
    })

    .catch(error => {
      res.status(500).json({
        message: 'Fetching listings failed!',
        error: error
      });
    });
};

exports.createListing = (req, res) => {
  if (req.body === {}) {
    res.status(400).json({
      message: 'Bad request'
    });
  }
  const url = req.protocol + '://' + req.get('host');
  const listing = new Listing({
    title: req.body.title,
    description: req.body.description,
    imageUrl: url + '/images/listings/' + req.file.filename,
    creator: req.userData.userId,
    latitude: req.body.latitude,
    longitude: req.body.longitude,
    expiration: new Date(req.body.expiration), // YYYY-mm-dd
    status: req.body.status,
    individual: req.body.individual
  });

  console.log(req.body);

  listing
    .save()

    .then(createdListing => {
      res.status(201).json({
        message: 'listing added successfully',
        listing: {
          ...createdListing._doc,
          id: createdListing._id
        }
      });
    })

    .catch(error => {
      res.status(500).json({
        message: 'creating a listing failed!',
        error: error
      });
    });
};

exports.updateListing = (req, res, next) => {
  const updateData = req.body;
  updateData._id = req.params.id;

  if (req.file) {
    const url = req.protocol + '://' + req.get('host');
    updateData.imageUrl = url + '/images/listings/' + req.file.filename;
  }

  Listing
    .updateOne(
      {
        _id: req.params.id,
        creator: req.userData.userId
      },
      {
        $set: updateData
      })

    .then(result => {
      if (result.modifiedCount > 0) {
        res.status(200).json({ message: 'Update successful' });
      } else {
        res.status(401).json({ message: 'Not Authorized' });
      }
    })

    .catch(error => {
      res.status(500).json({
        message: "Couldn't update listing!",
        error: error
      });
    });
};

exports.deleteListing = (req, res, next) => {
  Listing

    .deleteOne({
      _id: req.params.id,
      creator: req.userData.userId
    })

    .then(result => {
      if (result.deletedCount >= 1) {
        res.status(200).json({ message: 'Deletion successful' });
      } else {
        res.status(401).json({ message: 'Not Authorized' });
      }
    })

    .catch(error => {
      res.status(500).json({
        message: 'Deleting listing failed!',
        error: error
      });
    });
};
