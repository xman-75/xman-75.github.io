const mongoose = require('mongoose');
const Trip = require('../models/travlr'); // Register model
const Model = mongoose.model('trips');

// Pagination limits
const DEFAULT_PAGE_SIZE = 10;
const MAX_PAGE_SIZE = 50;

// Only these fields are ever read from a request body
const TRIP_FIELDS = [
    'code', 'name', 'length', 'start',
    'resort', 'perPerson', 'image', 'description'
];

// Builds a clean object from untrusted request data. Strings are trimmed
// and stripped of angle brackets, numbers pass through, and everything else
// (objects, arrays, such as {"$gt": ""}) is dropped so it cannot reach the
// query or the document. Dropped required fields then fail schema validation.
const sanitizeTripInput = (body) => {
    const clean = {};
    const source = body || {};
    for (const field of TRIP_FIELDS) {
        const value = source[field];
        if (typeof value === 'string') {
            clean[field] = value.replace(/[<>]/g, '').trim();
        } else if (typeof value === 'number') {
            clean[field] = value;
        }
    }
    return clean;
};

// GET: /trips - lists trips one page at a time
// Query params: page (default 1), limit (default 10, max 50)
// Total record count is returned in the X-Total-Count header
// Regardless of outcome, response must include HTML status code
// and JSON message to the requesting client
const tripsList = async (req, res, next) => {
    try {
        const page = Math.max(parseInt(req.query.page, 10) || 1, 1);
        const limit = Math.min(
            Math.max(parseInt(req.query.limit, 10) || DEFAULT_PAGE_SIZE, 1),
            MAX_PAGE_SIZE
        );

        const [q, total] = await Promise.all([
            Model
                .find({})
                .sort({ code: 1 }) // Stable order so pages do not overlap
                .skip((page - 1) * limit)
                .limit(limit)
                .exec(),
            Model.countDocuments({}).exec()
        ]);

        res.set('X-Total-Count', total);
        return res
            .status(200)
            .json(q); // Return resulting trip list
    } catch (err) {
        return next(err);
    }
};

// GET: /trips/:tripCode - lists a single trip
// Regardless of outcome, response must include HTML status code
// and JSON message to the requesting client
const tripsFindByCode = async (req, res, next) => {
    try {
        const q = await Model
            .find({ 'code': String(req.params.tripCode) }) // Return single record
            .exec();

        if (!q || q.length === 0) { // Database returned no data
            return res
                .status(404)
                .json({ message: `No trip found with code: ${req.params.tripCode}` });
        } else { // Return resulting trip list
            return res
                .status(200)
                .json(q);
        }
    } catch (err) {
        return next(err);
    }
};

// POST: /trips - Adds a new Trip
// Regardless of outcome, response must include HTML status code
// and JSON message to the requesting client
const tripsAddTrip = async (req, res, next) => {
    try {
        const newTrip = new Trip(sanitizeTripInput(req.body));
        const q = await newTrip.save();

        return res
            .status(201)
            .json(q); // Return new trip
    } catch (err) {
        return next(err);
    }
};

// PUT: /trips/:tripCode - Updates an existing Trip
const tripsUpdateTrip = async (req, res, next) => {
    try {
        const q = await Trip.findOneAndUpdate(
            { 'code': String(req.params.tripCode) },
            sanitizeTripInput(req.body),
            {
                new: true,          // Return the updated document
                runValidators: true // Apply schema rules to updates too
            }
        ).exec();

        if (!q) {
            return res
                .status(404)
                .json({ message: "Trip not found with code: " + req.params.tripCode });
        } else {
            return res
                .status(200)
                .json(q);
        }
    } catch (err) {
        return next(err);
    }
};

// DELETE: /trips/:tripCode - Removes an existing Trip
const tripsDeleteTrip = async (req, res, next) => {
    try {
        const q = await Trip
            .findOneAndDelete({ 'code': String(req.params.tripCode) })
            .exec();

        if (!q) { // Database returned no data
            return res
                .status(404)
                .json({ message: `No trip found with code: ${req.params.tripCode}` });
        } else { // Confirm deletion
            return res
                .status(200)
                .json({ message: `Trip ${req.params.tripCode} deleted.` });
        }
    } catch (err) {
        return next(err);
    }
};

module.exports = {
    tripsList,
    tripsFindByCode,
    tripsAddTrip,
    tripsUpdateTrip,
    tripsDeleteTrip
};