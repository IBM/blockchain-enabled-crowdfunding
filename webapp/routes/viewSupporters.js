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

  var url = config.rest_base_url + "/QueryAllUsersServlet";
  var url1 =config.rest_base_url  + "/QueryAllDonationsServlet";
  console.log("Query Operation");
  var options = {
        method : 'POST',
        url : url1,
        body : user,
        json : true
      };
  var options1 = {
        method : 'POST',
        url : url,
        body : user,
        json : true
      };

  request.post(options, function(err1, response1, body){
    if (body != null){
      for (var i=0; i<body.length; i++){
        donation_id.push(body[i]['donation_id']);
        donated_amount.push(body[i]['donated_amount']);
      }
      request.post(options1, function(err2, response2, body1){
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
