const Review = require('../models/review');
const User = require('../models/user');

const mongoose = require('mongoose');

exports.getReviews = (req, res, next) => {
  const pageSize = +req.query.pagesize;
  const currentPage = +req.query.page;

  const reviewQuery = Review
    .aggregate()

    .lookup({
      from: 'histories',
      localField: 'historyId',
      foreignField: '_id',
      as: 'historyDetails'
    })
    .lookup({
      from: 'listings',
      localField: 'historyDetails.listingId',
      foreignField: '_id',
      as: 'listingDetails'
    });

  let fetchedReview;

  if (pageSize && currentPage) {
    reviewQuery
      .sort({ createdAt: 1 })

      .skip(pageSize * (currentPage - 1))

      .limit(pageSize);
  }

  reviewQuery
    .then(documents => {
      fetchedReview = documents;
      return Review.countDocuments();
    })

    .then(count => {
      res.status(200).json({
        message: 'Succesfully sent from api',
        body: fetchedReview,
        maxReview: count
      });
    })

    .catch(error => {
      res.status(500).json({
        message: 'Fetching review failed!',
        error: error
      });
    });
};

exports.getReview = (req, res, next) => {
  Review
    .aggregate()

    .lookup({
      from: 'histories',
      localField: 'historyId',
      foreignField: '_id',
      as: 'historyDetails'
    })

    .lookup({
      from: 'listings',
      localField: 'historyDetails.listingId',
      foreignField: '_id',
      as: 'listingDetails'
    })

    .match({ _id: new mongoose.Types.ObjectId(req.params.id) })

    .then(review => {
      if (review[0]) {
        res.status(200).json(review[0]);
      } else {
        res.status(404).json({
          message: 'Review does not exist'
        });
      }
    })

    .catch(error => {
      res.status(500).json({
        message: 'Fetching review failed!',
        error: error
      });
    });
};

exports.getUserReviews = (req, res, next) => {
  const pageSize = +req.query.pagesize;
  const currentPage = +req.query.page;
  const reviewQuery = Review
    .aggregate()

    .lookup({
      from: 'histories',
      localField: 'historyId',
      foreignField: '_id',
      as: 'historyDetails'
    })

    .lookup({
      from: 'listings',
      localField: 'historyDetails.listingId',
      foreignField: '_id',
      as: 'listingDetails'
    })

    .match({ creator: new mongoose.Types.ObjectId(req.params.id) });

  let fetchedReview;

  if (pageSize && currentPage) {
    reviewQuery
      .sort({ createdAt: 1 })

      .skip(pageSize * (currentPage - 1))

      .limit(pageSize);
  }

  reviewQuery
    .then(documents => {
      fetchedReview = documents;
      return fetchedReview.length;
    })

    .then(count => {
      res.status(200).json({
        message: 'Succesfully sent from api',
        body: fetchedReview,
        maxReviews: count
      });
    })

    .catch(error => {
      res.status(500).json({
        message: 'Fetching review failed!',
        error: error
      });
    });
};

exports.createReview = (req, res) => {
  const review = new Review({
    creator: req.userData.userId,
    createdFor: req.body.createdFor,
    rating: req.body.rating,
    description: req.body.description,
    historyId: req.body.historyId
  });

  review
    .save()

    .then(createdReview => {
      User.find({ _id: req.body.createdFor })
        .then(result => {
          const rating = result[0].userRating + req.body.rating;
          console.log(rating);

          User
            .updateOne(
              { _id: req.body.createdFor },
              {
                $set: { userRating: rating }
              })

            .then(result => {
              if (result.modifiedCount > 0) {
                res.status(200).json({ message: 'Update successful' });
              } else {
                res.status(401).json({ message: 'Not Authorized' });
              }
            }).catch(error => {
              res.status(500).json({
                message: "Couldn't update rating!",
                error: error
              });
            });
        })

        .catch(error => {
          console.log(error);
          res.status(500).json({
            message: 'Creating a review failed!',
            error: error
          });
        });

      res.status(201).json({
        message: 'Review added successfully'
      });
    })

    .catch(error => {
      res.status(500).json({
        message: 'Creating a review failed!',
        error: error
      });
    });
};

exports.updateReview = (req, res, next) => {
  const updateData = req.body;
  updateData._id = req.params.id;

  Review
    .updateOne(
      {
        _id: req.params.id
      },
      {
        $set: updateData,
        creator: req.userData.userId
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
        message: "Couldn't update review!",
        error: error
      });
    });
};

exports.deleteReview = (req, res, next) => {
  Review

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
        message: 'Deleting review failed!',
        error: error
      });
    });
};
