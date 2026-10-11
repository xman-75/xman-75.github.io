const mongoose = require('mongoose');
// Define the trip schema
const tripSchema = new mongoose.Schema({
    code: { type: String, required: true, unique: true, trim: true, maxlength: 20 },
    name: { type: String, required: true, index: true, trim: true, maxlength: 100 },
    length: { type: String, required: true, trim: true, maxlength: 30 },
    start: { type: Date, required: true },
    resort: { type: String, required: true, trim: true, maxlength: 100 },
    perPerson: { type: Number, required: true, min: 0 },
    image: { type: String, required: true, trim: true, maxlength: 200 },
    description: { type: String, required: true, trim: true, maxlength: 2000 }
});
const Trip = mongoose.model('trips', tripSchema);
module.exports = Trip;