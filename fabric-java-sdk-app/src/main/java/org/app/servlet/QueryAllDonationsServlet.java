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
 * Servlet implementation class QueryAllDonationsServlet
 */
@WebServlet("/QueryAllDonationsServlet")
public class QueryAllDonationsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public QueryAllDonationsServlet() {
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
	 * This method invokes queryAllDonations method of smart contract
	 */
	private static String queryNeed(JSONObject req) throws Exception {
		
		ProfileVO profileData = ConnectionProfileLoader.loadProfileData();
		
		String stringResponse2 = "";
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
		peer_properties.put("pemBytes", profileData.getConnectingPeerPem().getBytes());
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

		QueryByChaincodeRequest queryRequest1 = hfClient.newQueryProposalRequest();
		queryRequest1.setChaincodeID(ccid);
		queryRequest1.setFcn("queryAllDonations");

		Collection<ProposalResponse> responses2Query = channel.queryByChaincode(queryRequest1);
		for (ProposalResponse pres : responses2Query) {
			stringResponse2 = new String(pres.getChaincodeActionResponsePayload());
			Logger.getLogger(QueryAllDonationsServlet.class.getName()).log(Level.INFO, "Query response: " + stringResponse2);
		}		

		return stringResponse2;
	}

}
