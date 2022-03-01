const mongoose = require('mongoose');
const uniqueValidator = require('mongoose-unique-validator');

const historySchema = mongoose.Schema({
  creator: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
  listingId: { type: mongoose.Schema.Types.ObjectId, ref: 'Listing', required: true },
  status: { type: String, required: true, default: 'pending' }
},
{
  timestamps: true
});

historySchema.plugin(uniqueValidator);

module.exports = mongoose.model('History', historySchema);
