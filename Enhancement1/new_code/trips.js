const mongoose = require('mongoose');
const Trip = require('../models/travlr'); // Register model
const Model = mongoose.model('trips');

// GET: /trips - lists all the trips
// Regardless of outcome, response must include HTML status code
// and JSON message to the requesting client
const tripsList = async(req, res, next) => {
    try {
        const q = await Model
            .find({}) // No filter, return all records
            .exec();

            // Uncomment the following line to show results of querey
            // on the console
            // console.log(q);

        return res
            .status(200)
            .json(q); // Return resulting trip list
    } catch (err) {
        // Was referencing an undefined err variable here instead of
        // catching a real one, which threw a reference error instead
        // of returning a clean response. Now forwards to the
        // centralized handler in app.js.
        return next(err);
    }
};

// GET: /trips/:tripCode - lists a single trip
// Regardless of outcome, response must include HTML status code
// and JSON message to the requesting client
const tripsFindByCode = async (req, res, next) => {
    try {
        const q = await Model
            .find({'code' : req.params.tripCode}) // Return single record
            .exec();

        // Uncomment the following line to show results of query
        // on the console
        // console.log(q);

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
const tripsAddTrip = async(req, res, next) => {
    try {
        const newTrip = new Trip({
            code: req.body.code,
            name: req.body.name,
            length: req.body.length,
            start: req.body.start,
            resort: req.body.resort,
            perPerson: req.body.perPerson,
            image: req.body.image,
            description: req.body.description
        });

        const q = await newTrip.save();

        return res
            .status(201)
            .json(q); // Return new trip

        // Uncomment the following line to show results of operation
        // on the console
        // console.log(q);
    } catch (err) {
        return next(err);
    }
};

// PUT: /trips/:tripCode - Updates an existing Trip
const tripsUpdateTrip = async (req, res, next) => {
    try {
        const q = await Trip.findOneAndUpdate(
            { 'code': req.params.tripCode },
            {
                code: req.body.code,
                name: req.body.name,
                length: req.body.length,
                start: req.body.start,
                resort: req.body.resort,
                perPerson: req.body.perPerson,
                image: req.body.image,
                description: req.body.description
            },
            { new: true } // Returns the updated document instead of the pre-update document
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
            .findOneAndDelete({ 'code': req.params.tripCode })
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