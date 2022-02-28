const express = require('express');
const router = express.Router();
const userController = require ('../controllers/user');
const extractFile = require('../middleware/file')('users');



router.get('/', userController.getUsers);

router.get('/:id', userController.getUser);

router.put('/:id', extractFile, userController.updateUser);

router.delete('/:id', userController.deleteUser);

router.post('/login', userController.loginUser);

router.post('/signup', extractFile, userController.createUser);

module.exports = router;