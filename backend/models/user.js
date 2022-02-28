const mongoose = require('mongoose');
const uniqueValidator = require('mongoose-unique-validator');


const userSchema = mongoose.Schema({
  firstName: {type: String, required: true},
  lastName: {type: String, required: true},
  phone: {type: Number, required: true},
  email: {type: String, required: true, unique: true},
  password: {type: String, required: true},
  imageUrl: {type: String, required: true, unique: true},
  organisation: {type: Boolean, required: true},
  organisationName: {type: String, required: false},
  userType: {type: String, required: true},
});

userSchema.plugin(uniqueValidator);

module.exports = mongoose.model('User', userSchema);
