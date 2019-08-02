var createError = require('http-errors');
var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');

var eventDetailsRouter = require('./routes/eventDetails');
var donationFormRouter = require('./routes/donationForm');
var viewSupportersRouter = require('./routes/viewSupporters');
var donateRouter = require('./routes/donateFund');

var app = express();

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static('public'));
app.use(express.static('images'));

app.use('/', eventDetailsRouter);
app.use('/donationForm', donationFormRouter);
app.use('/viewSupporters', viewSupportersRouter);
app.use('/donateFund', donateRouter);

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  next(createError(404));
});

// error handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error');
});

module.exports = app;
