var express = require('express');
var router = express.Router();
var bodyParser = require("body-parser");
var request = require("request");
const path = require('path');
var config = require("../config");
var executed = false;
var eventStartDate="";

router.post('/', function(req, res) {
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
  var today = new Date();
  var dd = today.getDate();
  var mm = today.getMonth() + 1;
  var yyyy = today.getFullYear();

  if (dd < 10) {
    dd = '0' + dd
  }

  if (mm < 10) {
    mm = '0' + mm
  }

  today = mm + '/' + dd + '/' + yyyy;

  const date1 = new Date(eventStartDate);
  const date2 = new Date(today);
  const diffTime = Math.abs(date2.getTime() - date1.getTime());
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  // var gender=req.body.gender;
  var amount=req.body.amount;
  var name=req.body.name;
  var phone=req.body.phone;
  var email=req.body.email;
  // var user={
  //   "name" : req.body.name,
  //   "email" : req.body.email,
  //   "phone" : req.body.phone,
  //   "amount" : req.body.amount,
  //   "note" : req.body.note
  // }

  var url = config.rest_base_url;
  console.log("Donation Operation");
  var options = {
        method : 'POST',
        url : url,
        body: {
           method: "invoke",
           params: {
               ctorMsg: {
                   function: "donateMoney",
                   args: [amount, name, phone, email]
               }
           }
        },
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        },
        json : true
      };

  var options1 = {
        url: url,
        method: "POST",
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

  if(diffDays <= 20){
  request.post(options, function(err,resp,b){
      console.log("QueryEvent Operation");
        
      request.post(options1, function(error,response,b1){
          var body = JSON.parse(b1);
          var raisedAmount = JSON.parse(body['donated']);
          res.render( 'donationForm', {title : 'Menu', raisedAmount : raisedAmount, date:eventStartDate});
      });
  });
}

});
module.exports = router;
