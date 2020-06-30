const express = require('express');
const router = express.Router();
const request = require('request')
const hfc = require('fabric-client')
const CAClient = require('fabric-ca-client')
const fs = require('fs')
const cors = require('cors')
const _ = require('underscore')
const util = require('util')
const async = require('async')
const exec = require('child_process').exec;
const glob = require("glob")
const path = require('path');
const os = require('os');

router.all('*', cors())
module.exports = router;


function enrollUser(username, client, networkId, client_crypto_suite) {
  return new Promise((resolve, reject) => {
    console.log("Monitoring Client User doesn't exist. Loading CA client to enroll")
    var org = Object.keys(client._network_config._network_config.organizations)[0]
    var certificateAuthorities = client._network_config._network_config.certificateAuthorities
    var certificateAuthorityName = Object.keys(certificateAuthorities)[0]
    var certificateAuthObj = certificateAuthorities[certificateAuthorityName]
    var registrar = client._network_config._network_config.certificateAuthorities[certificateAuthorityName].registrar[0]
    var mspId = client._network_config._network_config.organizations[org]['mspid']
    var ca = new CAClient(certificateAuthObj.url, {
      trustedRoots: [],
      verify: false
    }, certificateAuthObj.caName, client_crypto_suite)
    enrollment = ca.enroll({
      enrollmentID: registrar.enrollId,
      enrollmentSecret: registrar.enrollSecret
    }).then((result) => {
      console.log("Enrolling client")
      return client.createUser({
        username: username,
        mspid: mspId,
        cryptoContent: {
          privateKeyPEM: result.key.toBytes(),
          signedCertPEM: result.certificate
        }
      })
    }).then((user) => {
      client.setUserContext(user).then(() => {
        console.log(username + " enrolled. Upload following certificate via blockchain UI: \n " + 'https://ibmblockchain-starter.ng.bluemix.net' + "/network/" + networkId + "/members/certificates")
        console.log(user._signingIdentity._certificate + '\n')
        resolve()
        // res.send("Upload this certificate \n" + user._signingIdentity._certificate )
        // uploadAdminCert(req, mspId)
      })
    }).catch((err) => {
      reject()
      console.error('Failed to enroll and persist admin. Error: ' + err.stack ? err.stack : err);
      throw new Error('Failed to enroll admin');
    });
  })
}

function requestConnectionProfile(req, res) {
  return new Promise((resolve, reject) => {
    console.log("requesting connection profile")
    if (!req.body.api_endpoint.includes('/api/v1')) {
      var api_endpoint = req.body.api_endpoint + '/api/v1'
    } else {
      var api_endpoint = req.body.api_endpoint
    }
    var options = {
      url: api_endpoint + '/networks/' + req.body.network_id + '/connection_profile',
      method: 'GET',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
        'Accept-Charset': 'utf-8',
        "Authorization": "Basic " + new Buffer(req.body.key + ":" + req.body.secret, "utf8").toString("base64")
      }
    }
    request(options, function(err, res, body) {
      let json = JSON.parse(body);
      console.log("body")
      console.log(body)
      if (err) {
        reject("Error fetching connection profile")
      }
      fs.writeFile('./connection_profile.json', body, 'utf8', function(err) {
        if (err) {
          console.log(err)
          console.log(err)
          reject("Error writing connection profile")
        } else {
          resolve()
        }
      })
    })
  })
}

function loadConnectionProfile() {
  if (fs.existsSync('./connection_profile.json')) {
    console.log("Local connection profile loading")
    client = hfc.loadFromConfig('../connection_profile.json')
    var username = "monitoring_admin"
    client.loadUserFromStateStore("./hfc-key-store/hosted/")
    client.getUserContext(username, true).then((user) => {
      console.log("Loading user context")
      console.log(user)
      if (user && user.isEnrolled()) {
        console.log("Client Loaded From Persistence")
      }
    })
  }
}
loadConnectionProfile()

function loadChaincodeInfo() {
  if (fs.existsSync('./chaincode_info.json')) {
    sec_chaincode = JSON.parse(fs.readFileSync('./chaincode_info.json', 'utf8'))
  }
}
loadChaincodeInfo()

function initializeHostedClient(req, res) {
  console.log("Initializing HFC client")
  if (fs.existsSync('./connection_profile.json')) {
    console.log("Local connection profile loading")
    client = hfc.loadFromConfig('./connection_profile.json')
    org = Object.keys(client._network_config._network_config.organizations)[0]
    certificateAuthorities = client._network_config._network_config.certificateAuthorities
    certificateAuthorityName = Object.keys(certificateAuthorities)[0]
    certificateAuthObj = certificateAuthorities[certificateAuthorityName]
    var mspId = client._network_config._network_config.organizations[org]['mspid']
    var storePath = './hfc-key-store/hosted/'
    var client_crypto_suite = hfc.newCryptoSuite()
    var crypto_store = hfc.newCryptoKeyStore({
      path: storePath
    })
    var crypto_suite = hfc.newCryptoSuite()
    var username = "monitoring_admin"

    async.series([
        function(callback) {
          console.log("Set CryptoKeyStore")
          crypto_suite.setCryptoKeyStore(crypto_store)
          callback()
        },
        function(callback) {
          console.log("Set CryptoSuite")
          client.setCryptoSuite(crypto_suite)
          callback()
        },
        function(callback) {
          hfc.newDefaultKeyValueStore({
            path: storePath
          }).then((store) => {
            console.log("Set default keystore")
            client.setStateStore(store)
            callback()
          })
        },
        function(callback) {
          client.getUserContext(username, true).then((user) => {
            console.log("Loading user context")
            if (user && user.isEnrolled()) {
              console.log("Client Loaded From Persistence")
              console.log("Be sure to upload following cert via blockchain UI: \n")
              console.log(user._signingIdentity._certificate + '\n')
              callback()
            } else {
              enrollUser(username, client, client._network_config._network_config['x-networkId'], client_crypto_suite).then(() => {
                callback()
              })
            }
          })
        },
        function(callback) {
          console.log("Requesting Chaincode information")
          peer = client.getPeersForOrgOnChannel()[0]._name
          channel = client.getChannel()
          sec_chaincode = {
            name: req.body.chaincode_id,
            version: req.body.chaincode_version
          }
          fs.writeFile('chaincode_info.json', JSON.stringify(sec_chaincode), 'utf8', function() {})
          console.log("chaincode info, channel, peers set")
        }
      ],
      function(err) {
        console.log(err)
      })
    console.log("end of init_client")
  } else {
    console.log("connection profile doesn't exist, exiting")
    return
  }
}

if (process.env.DEPLOY_TYPE == 'local') {
  console.log("initializing local hfc client")
  initializeLocalClient()
}

function initializeLocalClient() {
  var endpoint = "http://localhost"
  console.log("Initializing Local HFC client")
  client = hfc.loadFromConfig('../local/connection.json')
  ca = new CAClient(endpoint + ":17050", {
    trustedRoots: [],
    verify: false
  }, 'ca', client_crypto_suite)
  var storePath = './hfc-key-store/local'
  var client_crypto_suite = hfc.newCryptoSuite()
  var crypto_store = hfc.newCryptoKeyStore({
    path: storePath
  })
  var crypto_suite = hfc.newCryptoSuite()
  var username = "monitoring_admin"
  sec_chaincode = {
    name: "test",
    version: "1.0"
  }
  async.series([
      function(callback) {
        console.log("Set CryptoKeyStore")
        crypto_suite.setCryptoKeyStore(crypto_store)
        callback()
      },
      function(callback) {
        console.log("Set CryptoSuite")
        client.setCryptoSuite(crypto_suite)
        callback()
      },
      function(callback) {
        hfc.newDefaultKeyValueStore({
          path: storePath
        }).then((store) => {
          console.log("Set default keystore")
          client.setStateStore(store)
          callback()
        })
      },
      function(callback) {
        client.getUserContext(username, true).then((user) => {
          console.log("Loading user context")
          console.log(user)
          if (user && user.isEnrolled()) {
            console.log("in if")
            console.log("Client Loaded From Persistence")
            console.log("Be sure to upload following cert via blockchain UI: \n")
            console.log(user._signingIdentity._certificate + '\n')
            channel = client.getChannel()
            callback()
          } else {
            channel = client.getChannel()
            // console.log("Printing channel details")
            // console.log(channel)
            ca.enroll({
              enrollmentID: 'admin',
              enrollmentSecret: 'adminpw'
            }).then((enrollment) => {
              console.log('Successfully enrolled admin user "admin"');
              return client.createUser({
                username: 'admin',
                mspid: 'Org1MSP',
                cryptoContent: {
                  privateKeyPEM: enrollment.key.toBytes(),
                  signedCertPEM: enrollment.certificate
                }
              });
            }).then((user) => {
              client.setUserContext(user)

            }).catch((err) => {
              console.error('Failed to enroll and persist admin. Error: ' + err.stack ? err.stack : err);
              throw new Error('Failed to enroll admin');
            })
          }
        })
      }
    ],
    function(err) {
      console.log(err)
    })
  console.log("end of init_client")
}


// When server first starts up, we'll check for hosted HFC configure files/certs
// If the hosted files do not exist, check for local hyperledger.
var checkHFCConfig = function() {
  var hfc_name = "monitoring_admin"
  if (fs.existsSync('./hfc-key-store/hosted/')) {
    var privKey = fs.readdirSync('./hfc-key-store/hosted/').filter(fn => fn.endsWith('-priv'));
    var pubKey = fs.readdirSync('./hfc-key-store/hosted/').filter(fn => fn.endsWith('-pub'));
    if ( (fs.existsSync('./hfc-key-store/hosted/' + hfc_name)) && (pubKey.length > 0) && (privKey.length > 0)) {
      loadHFC()
    }
  }
}

checkHFCConfig()

var loadHFC = function() {
  var storePath = './hfc-key-store/hosted/'
  var client_crypto_suite = hfc.newCryptoSuite()
  var crypto_store = hfc.newCryptoKeyStore({
    path: storePath
  })
  var crypto_suite = hfc.newCryptoSuite()
  async.series([
      function(callback) {
        console.log("Set CryptoKeyStore")
        crypto_suite.setCryptoKeyStore(crypto_store)
        callback()
      },
      function(callback) {
        console.log("Set CryptoSuite")
        client.setCryptoSuite(crypto_suite)
        callback()
      },
      function(callback) {
        hfc.newDefaultKeyValueStore({
          path: storePath
        }).then((store) => {
          console.log("Set default keystore")
          client.setStateStore(store)
          callback()
        })
      },
      function(callback) {
        var username = "monitoring_admin"
        client.getUserContext(username, true).then((user) => {
          console.log("Loading user context")
          if (user && user.isEnrolled()) {
            console.log("Client Loaded From Persistence")
            console.log("Be sure to upload following cert via blockchain UI: \n") //+ req.body.urlRestRoot + "/network/" + req.body.networkId + "/members/certificates")
            console.log(user._signingIdentity._certificate + '\n')
            res.json({
              "msg": "Please upload following cert to IBM Blockchain UI",
              "certificate": user._signingIdentity._certificate
            })
            callback()
          } else {
            enrollUser(username, client, client._network_config._network_config['x-networkId'], client_crypto_suite).then(() => {
              callback()
            })
          }
        })
      },
      function(callback) {
        console.log("Requesting Chaincode information")
        peer = client.getPeersForOrgOnChannel()[0]._name
        channel = client.getChannel()
        sec_chaincode = {
          name: req.body.chaincode_id,
          version: req.body.chaincode_version
        }
        fs.writeFile('chaincode_info.json', JSON.stringify(sec_chaincode), 'utf8', function() {})
        console.log("chaincode info, channel, peers set")
      }
    ],
    function(err) {
      console.log(err)
    })
}

router.post('/api/init_hfc_client', function(req, res) {
  console.log("request received to initialize client")
  if (fs.existsSync('./connection_profile.json')) {
    console.log("Loading connection profile from local file")
    loadConnectionProfile()
  } else {
    console.log("Requesting connection profile")
    requestConnectionProfile(req, res).then(() => {
      initializeHostedClient(req, res)
    })
  }
});

router.post('/api/client', function(req, res) {
  console.log(client)
});

router.post('/api/chaincode', function(req, res) {
  console.log("chaincode request received")
  console.log(req.body)
  var chaincode = req.body.params.ctorMsg
  var chaincode_query = JSON.stringify({
    "Args": [chaincode.function].concat(chaincode.args)
  })
  if (typeof(client) !== 'undefined') {
    console.log("invoking chaincode with hfc client")
    console.log("req")
    console.log(req.body)
    console.log("req.body.method")
    console.log(req.body.method)
    if (req.body.method && req.body.method.includes('invoke')) {
      console.log("invoking request")
      var transaction_id = client.newTransactionID(true)
      var txRequest = {
        chaincodeId: sec_chaincode.name,
        chaincodeVersion: sec_chaincode.version,
        txId: transaction_id,
        fcn: req.body.params.ctorMsg.function,
        args: req.body.params.ctorMsg.args
      }
      console.log(txRequest)
      var txResult = proposeAndSubmitTransaction(txRequest)
      res.send(200)
    } else {
      console.log("querying chaincode with hfc client")
      var txRequest = {
        chaincodeId: sec_chaincode.name,
        chaincodeVersion: sec_chaincode.version,
        fcn: req.body.params.ctorMsg.function,
        args: req.body.params.ctorMsg.args
      }
      console.log("txRequest")
      console.log(txRequest)
      channel.queryByChaincode(txRequest).then((cc_response) => {
        console.log("cc query response received")
        console.log(cc_response[0].toString())
        res.json(cc_response[0].toString())
      }).catch((err) => {
        console.log("cc query failed")
        console.log(err)
        res.json(err)
      })
    }
  }

});

function submitTransaction(txRequest) {
  console.log(util.format('Successfully sent Proposal and received ProposalResponse: Status - %s, message - "%s"', proposalResponses[0].response.status, proposalResponses[0].response.message));
  var promises = []
  var sendPromise = channel.sendTransaction({
    proposalResponses: proposalResponses,
    proposal: proposal
  })
  sendPromise.then((result) => {
    console.log("transaction result")
    console.log(result)
    res.send(result)
  })
}

function proposeAndSubmitTransaction(txRequest) {
  console.log("sending transaction proposal")
  channel.sendTransactionProposal(txRequest).then((proposalRes) => {
    console.log("response received")
    var proposalResponses = proposalRes[0];
    var proposal = proposalRes[1];
    let isProposalGood = false;
    console.log("proposalResponses[0].response")
    console.log(proposalResponses[0].response)
    if (proposalResponses && proposalResponses[0].response && proposalResponses[0].response.status === 200) {
      console.log('Transaction proposal was accepted');
      channel.sendTransaction({
        proposalResponses: proposalResponses,
        proposal: proposal
      }).then((res) => {
        console.log("Transaction result was accepted")
        return true
      })
    } else {
      console.log('Transaction proposal was rejected');
      return false
    }
  }).catch((err) => {
    return false
    console.log(err)
  });
}

function uploadAdminCert(req, mspId) {
  var uploadAdminCertReq = {
    "msp_id": mspId,
    "adminCertName": "admin_cert" + Math.floor(Math.random() * 1000),
    "adminCertificate": user._signingIdentity._certificate,
    "peer_names": Object.keys(client._network_config._network_config.peers),
    "SKIP_CACHE": true
  }
  if (! req.body.api_endpoint.includes('/api/v1')) {
    var api_endpoint = req.body.api_endpoint + '/api/v1'
  } else {
    var api_endpoint = req.body.api_endpoint
  }
  var options = {
    url: api_endpoint + '/networks/' + req.body.network_id + '/certificates',
    method: 'POST',
    headers: {
      'Accept': 'application/json',
      'Content-Type': 'application/json',
      'Accept-Charset': 'utf-8',
      "Authorization": "Basic " + new Buffer(req.body.key + ":" + req.body.secret, "utf8").toString("base64")
    },
    body: uploadAdminCertReq
  }
  console.log("uploading admin cert")
  request(options, function(err, res, body) {
    console.log("res")
    console.log(res)
    if (err) {
      console.log(err)
    }
  })
}
