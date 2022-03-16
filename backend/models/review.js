const mongoose = require('mongoose');
const uniqueValidator = require('mongoose-unique-validator');

const reviewSchema = mongoose.Schema({
  creator: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
  createdFor: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
  historyId: { type: mongoose.Schema.Types.ObjectId, ref: 'History', required: true, unique: true },
  rating: { type: Number, required: true },
  description: { type: String, required: false }

},
{
  timestamps: true
});

reviewSchema.plugin(uniqueValidator);

module.exports = mongoose.model('Review', reviewSchema);
