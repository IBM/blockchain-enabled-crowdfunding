var express = require('express');
var router = express.Router();
var bodyParser = require("body-parser");
var request = require("request");
const path = require('path');
var config = require("../config");


/* GET home page. */
var executed = false;
var eventStartDate="";
router.get('/', function(req, res, next) {
  if (!executed) {
      executed = true;
      var startDate = new Date();
      var dd = startDate.getDate();
      var mm = startDate.getMonth() + 1;
      var yyyy = startDate.getFullYear();

      if (dd < 10) {
        dd = '0' + dd
      }

      if (mm < 10) {
        mm = '0' + mm
      }

      startDate= mm + '/' + dd + '/' + yyyy;
      eventStartDate=startDate;
    }
  var user={}
  
  var url = config.rest_base_url;
      console.log("Query Operation");
      var options1 = {
        method : 'POST',
        url : url,
        body: {
             method: "query",
             params: {
                ctorMsg: {
                   function: "queryEvent",
                   args: ["E1"]
                }
             }
        },
        headers: {
           'Accept': 'application/json',
           'Content-Type': 'application/json'
        },
        json : true
      };

      request.post(options1, function(error,response,b){
        var body = JSON.parse(b);
        var raisedAmount = JSON.parse(body['donated']);
        res.render( 'donationForm', {title : 'donationForm', raisedAmount : raisedAmount, date:eventStartDate});
      });
});

module.exports = router;
