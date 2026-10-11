require('dotenv').config();
const mongoose = require('mongoose');
const readLine = require('readline');

const {
    DB_HOST = '127.0.0.1',
    DB_NAME = 'travlr',
    DB_USER,
    DB_PASS,
    DB_AUTH_SOURCE = 'admin'
} = process.env;

const dbURI = `mongodb://${DB_HOST}/${DB_NAME}`;

// Only send credentials when they are configured
const options = DB_USER && DB_PASS
    ? { user: DB_USER, pass: DB_PASS, authSource: DB_AUTH_SOURCE }
    : {};

// Build the connection and set the connection delay.
// Delay is in milliseconds.
const connect = () => {
    setTimeout(() => mongoose.connect(dbURI, options), 1000);
};

// Monitor connection events
mongoose.connection.on('connected', () => {
    // Log host and database only, never credentials
    console.log(`Mongoose connected to ${DB_HOST}/${DB_NAME}`);
});
mongoose.connection.on('error', err => {
    console.log('Mongoose connection error: ', err.message);
});
mongoose.connection.on('disconnected', () => {
    console.log('Mongoose disconnected');
});

// Windows specific listener
if (process.platform === 'win32') {
    const r1 = readLine.createInterface({
        input: process.stdin,
        output: process.stdout
    });
    r1.on('SIGINT', () => {
        process.emit('SIGINT');
    });
}

// Configure for Graceful Shutdown
const gracefulShutdown = async (msg) => {
    await mongoose.connection.close();
    console.log(`Mongoose disconnected through ${msg}`);
};

// Event Listeners to process graceful shutdowns
// Shutdown invoked by nodemon signal
process.once('SIGUSR2', async () => {
    await gracefulShutdown('nodemon restart');
    process.kill(process.pid, 'SIGUSR2');
});

// Shutdown invoked by app termination
process.on('SIGINT', async () => {
    await gracefulShutdown('app termination');
    process.exit(0);
});

// Shutdown invoked by container termination
process.on('SIGTERM', async () => {
    await gracefulShutdown('app shutdown');
    process.exit(0);
});

// Make initial connection to DB
connect();

// Import Mongoose schema
require('./travlr');

module.exports = mongoose;