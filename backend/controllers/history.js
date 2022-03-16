const History = require('../models/history');
const mongoose = require('mongoose');

exports.getAllHistory = (req, res, next) => {
  const pageSize = +req.query.pagesize;
  const currentPage = +req.query.page;

  const historyQuery = History
    .aggregate()

    .lookup({
      from: 'listings',
      localField: 'listingId',
      foreignField: '_id',
      as: 'listingDetails'
    });

  let fetchedHistory;

  if (pageSize && currentPage) {
    historyQuery
      .sort({ createdAt: 1 })

      .skip(pageSize * (currentPage - 1))

      .limit(pageSize);
  }

  historyQuery
    .then(documents => {
      fetchedHistory = documents;
      return History.countDocuments();
    })

    .then(count => {
      res.status(200).json(
        fetchedHistory
      );
    })

    .catch(error => {
      res.status(500).json({
        message: 'Fetching history failed!',
        error: error
      });
    });
};

exports.getHistory = (req, res, next) => {
  History
    .aggregate()

    .lookup({
      from: 'listings',
      localField: 'listingId',
      foreignField: '_id',
      as: 'listingDetails'
    })
    .match({ _id: new mongoose.Types.ObjectId(req.params.id) })

    .then(history => {
      if (history[0]) {
        res.status(200).json(history[0]);
      } else {
        res.status(404).json({
          message: 'History does not exist'
        });
      }
    })

    .catch(error => {
      res.status(500).json({
        message: 'Fetching history failed!',
        error: error
      });
    });
};

exports.getUserHistory = (req, res, next) => {
  const pageSize = +req.query.pagesize;
  const currentPage = +req.query.page;
  const historyQuery = History
    .aggregate()

    .lookup({
      from: 'listings',
      localField: 'listingId',
      foreignField: '_id',
      as: 'listingDetails'
    })
    .match({ creator: new mongoose.Types.ObjectId(req.params.id) });

  let fetchedHistory;

  if (pageSize && currentPage) {
    historyQuery
      .sort({ createdAt: 1 })

      .skip(pageSize * (currentPage - 1))

      .limit(pageSize);
  }

  historyQuery
    .then(documents => {
      fetchedHistory = documents;
      return fetchedHistory.length;
    })

    .then(count => {
      res.status(200).json(
        fetchedHistory
      );
    })

    .catch(error => {
      res.status(500).json({
        message: 'Fetching history failed!',
        error: error
      });
    });
};

exports.createHistory = (req, res) => {
  const history = new History({
    creator: req.userData.userId,
    listingId: req.body.listingId
  });

  history
    .save()

    .then(createdHistory => {
      res.status(201).json({
        message: 'history added successfully'
      });
    })

    .catch(error => {
      res.status(500).json({
        message: 'creating a history failed!',
        error: error
      });
    });
};

exports.updateHistory = (req, res, next) => {
  const updateData = req.body;
  updateData._id = req.params.id;

  History
    .updateOne(
      {
        _id: req.params.id
      },
      {
        $set: updateData
      })

    .then(result => {
      console.log(result)
      if (result.modifiedCount > 0) {
        res.status(200).json({ message: 'Update successful' });
      } else {
        res.status(401).json({ message: 'Not Authorized' });
      }
    })

    .catch(error => {
      res.status(500).json({
        message: "Couldn't update history!",
        error: error
      });
    });
};

exports.deleteHistory = (req, res, next) => {
  History

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
        message: 'Deleting history failed!',
        error: error
      });
    });
};
