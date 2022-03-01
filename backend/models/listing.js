const mongoose = require('mongoose');
const uniqueValidator = require('mongoose-unique-validator');

const listingSchema = mongoose.Schema({
  title: { type: String, required: true },
  description: { type: String, required: true },
  creator: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
  latitude: { type: Number, required: true },
  longitude: { type: Number, required: true },
  expiration: { type: Date, required: true },
  status: { type: String, required: true, default: 'available' },
  individual: { type: Boolean, required: true },
  imageUrl: { type: String, required: true }
},
{
  timestamps: true
});

listingSchema.plugin(uniqueValidator);

module.exports = mongoose.model('Listing', listingSchema);
