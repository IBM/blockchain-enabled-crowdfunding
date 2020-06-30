var express = require('express');
var router = express.Router();
var bodyParser = require("body-parser");
var request = require("request");
var config = require("../config");
const path = require('path');

router.post('/', function(req, res) {
  var user={};
  var user1={};
  var donated_amount=[];
  var donation_id=[];
  var user_name=[];

  var url = config.rest_base_url;
  // var url1 =config.rest_base_url  + "/QueryAllDonationsServlet";
  console.log("Query Operation");
  var options = {
        method : 'POST',
        url : url,
        body: {
          method: "query",
          params: {
             ctorMsg: {
                function: "queryAllDonations",
                args: []
              }
          }
        },
        headers: {
           'Accept': 'application/json',
           'Content-Type': 'application/json'
        },
        json : true
      };

  var options1 = {
        method : 'POST',
        url : url,
        body: {
          method: "query",
          params: {
             ctorMsg: {
                function: "queryAllUsers",
                args: []
              }
          }
        },
        headers: {
           'Accept': 'application/json',
           'Content-Type': 'application/json'
        },
        json : true
      };

  request.post(options, function(err1, response1, bodyRes){
    var body = JSON.parse(bodyRes);
    if (body != null){
      for (var i=0; i<body.length; i++){
        donation_id.push(body[i]['donation_id']);
        donated_amount.push(body[i]['donated_amount']);
      }
      request.post(options1, function(err2, response2, bodyRes1){
        var body1 = JSON.parse(bodyRes1);
        for (var j=0; j<body1.length; j++){
          user_name.push(body1[j]['user_name']);
        }
        res.render('viewSupporters',{donations : donation_id, amount : donated_amount, names: user_name});
      });
    } else {
      res.send("No Supporters yet.");
    }
  });
});

module.exports = router;
