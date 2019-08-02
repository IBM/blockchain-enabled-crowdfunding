/******************************************************
 *  Copyright 2019 IBM Corporation
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.app.servlet;

import java.io.IOException;
import java.util.Collection;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.app.config.ConnectionProfileLoader;
import org.app.config.ProfileVO;
import org.app.user.UserContext;
import org.app.util.Util;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.QueryByChaincodeRequest;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.json.JSONObject;

/**
 * Servlet implementation class QueryEventServlet
 */
@WebServlet("/QueryEventServlet")
public class QueryEventServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public QueryEventServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			StringBuilder sb = new StringBuilder();
			String s;
			while ((s = request.getReader().readLine()) != null) {
				sb.append(s);
			}
			Logger.getLogger(getServletName()).log(Level.INFO, "Received request data - " + sb.toString());
			JSONObject req = new JSONObject(sb.toString());
			String res = queryNeed(req);
			response.getWriter().append(res);
		} catch (Exception e) {
			response.getWriter().append( "Internal Server Error " + e.getMessage() );
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
	
	/*
	// main method for local testing
	public static void main(String[] args){
		try {
			queryNeed(new JSONObject());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	*/

	/*
	 * This method invokes queryEvent method of smart contract
	 */
	private static String queryNeed(JSONObject req) throws Exception{
		
		ProfileVO profileData = ConnectionProfileLoader.loadProfileData();
		
		String stringResponse = "";
		String CHANNEL_NAME = profileData.getChannelName();

		
		// Get stored user context
		UserContext adminUserContext = Util.readUserContext(profileData.getOrgAffiliation(), profileData.getAdmin());

		// Create an Object to interact with Hyperldger Fabric network
		// and set necessary data
		CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
		HFClient hfClient = HFClient.createNewInstance();
		hfClient.setCryptoSuite(cryptoSuite);
		hfClient.setUserContext(adminUserContext);


		Channel channel = hfClient.newChannel(CHANNEL_NAME);

		Properties peer_properties = new Properties();
		String pemStr = profileData.getConnectingPeerPem();
		peer_properties.put("pemBytes", pemStr.getBytes());
		peer_properties.setProperty("sslProvider", "openSSL");
		peer_properties.setProperty("negotiationType", "TLS");
		Peer peer = hfClient.newPeer(profileData.getConnectingPeer(), profileData.getConnectingPeerURL(), peer_properties);

		Properties orderer_properties = new Properties();
		orderer_properties.setProperty("sslProvider", "openSSL");
		orderer_properties.setProperty("negotiationType", "TLS");
		Orderer orderer = hfClient.newOrderer(profileData.getOrdererName(), profileData.getOrdererURL(), orderer_properties);

		channel.addPeer(peer);
		channel.addOrderer(orderer);
		channel.initialize();


		TransactionProposalRequest request = hfClient.newTransactionProposalRequest();
		String cc = profileData.getChainCodeName();
		ChaincodeID ccid = ChaincodeID.newBuilder().setName(cc).build();

		request.setChaincodeID(ccid);


		// Query
		QueryByChaincodeRequest queryRequest = hfClient.newQueryProposalRequest();
		queryRequest.setChaincodeID(ccid);
		queryRequest.setFcn("queryEvent");
		
		/*
		 * There is only one event in the smart contract and it's ID is E1.
		 * Directly querying E1. If the ID is changed in smart contract,
		 * ID here also needs a change
		 */
		String[] args1 = { "E1"};

		if (args1 != null){
			queryRequest.setArgs(args1);
		}
		Collection<ProposalResponse> responses1Query = channel.queryByChaincode(queryRequest);
		for (ProposalResponse pres : responses1Query) {
			stringResponse = new String(pres.getChaincodeActionResponsePayload());
			Logger.getLogger(QueryEventServlet.class.getName()).log(Level.INFO, "Query response: " + stringResponse);
		}
		
		return stringResponse;
	}

}
