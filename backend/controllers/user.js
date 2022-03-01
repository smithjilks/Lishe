const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');

const User = require('../models/user');

exports.createUser = (req, res, next) => {
  const url = req.protocol + '://' + req.get('host');

  bcrypt
    .hash(req.body.password, 10)

    .then(hash => {
      const user = new User({
        firstName: req.body.firstName,
        lastName: req.body.lastName,
        phone: req.body.phone,
        email: req.body.email,
        password: hash,
        imageUrl: url + '/images/users/' + req.file.filename,
        organisation: req.body.organisation,
        organisationName: req.body.organisationName,
        userType: req.body.userType
      });

      user
        .save()

        .then(result => {
          res.status(201).json({
            message: 'user created',
            result: result
          });
        })

        .catch(error => {
          res.status(400).json({
            message: 'Invalid user details',
            error: error
          });
        });
    });
};

exports.loginUser = (req, res, next) => {
  let fetchedUser;

  User
    .findOne({
      email: req.body.email
    })

    .then(user => {
      if (!user) {
        return res.status(403).json({
          messsage: 'User does not exist'
        });
      }

      fetchedUser = user;

      bcrypt
        .compare(req.body.password, user.password)

        .then(result => {
          if (!result) {
            return res.status(401).json({
              messsage: 'Auth failed'
            });
          }

          const token = jwt.sign(
            {
              email: fetchedUser.email,
              userId: fetchedUser._id
            },
            process.env.JWT_KEY,
            {
              expiresIn: '1h'
            }
          );

          res.status(200).json({
            token: token,
            expiresIn: 3600,
            userId: fetchedUser._id
          });
        });
    })

    .catch(error => {
      return res.status(401).json({
        messsage: 'Auth failed',
        error: error
      });
    });
};

exports.getUsers = (req, res, next) => {
  const pageSize = +req.query.pagesize;
  const currentPage = +req.query.page;
  const userQuery = User.find();
  let fetchedUsers;

  // User.find(userQuery, fields, { skip: 10, limit: 5 }, function(err, results) { ... });

  if (pageSize && currentPage) {
    userQuery
      .sort({ date: -1 })
      .skip(pageSize * (currentPage - 1))
      .limit(pageSize);
  }

  userQuery
    .then(documents => {
      fetchedUsers = documents;
      return User.countDocuments();
    })

    .then(count => {
      res.status(200).json({
        message: 'Succesfully sent from api',
        body: fetchedUsers,
        maxUsers: count
      });
    })

    .catch(error => {
      res.status(500).json({
        message: 'Fetching users failed!',
        error: error
      });
    });
};

exports.getUser = (req, res, next) => {
  User
    .findById(req.params.id)

    .then(user => {
      if (user) {
        res.status(200).json(user);
      } else {
        res.status(404).json({
          message: 'User does not exist'
        });
      }
    })
    .catch(error => {
      res.status(500).json({
        message: 'Fetching user failed!',
        error: error
      });
    });
};

exports.updateUser = (req, res, next) => {
  const updateData = req.body;
  updateData._id = req.params.id;

  if (req.file) {
    const url = req.protocol + '://' + req.get('host');
    updateData.imageUrl = url + '/images/users/' + req.file.filename;
  }

  User
    .updateOne(
      {
        _id: req.userData.userId
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
    }).catch(error => {
      res.status(500).json({
        message: "Couldn't update post!",
        error: error
      });
    });
};

exports.deleteUser = (req, res, next) => {
  User
    .deleteOne({
      _id: req.userData.userId
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
        message: 'Deleting user failed!',
        error: error
      });
    });
};
