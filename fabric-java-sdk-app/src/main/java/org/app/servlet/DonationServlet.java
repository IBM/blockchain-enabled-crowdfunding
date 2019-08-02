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
import java.util.concurrent.CompletableFuture;
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
import org.hyperledger.fabric.sdk.BlockEvent.TransactionEvent;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.ChaincodeResponse.Status;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.json.JSONObject;

/**
 * Servlet implementation class DonationServlet
 */
@WebServlet("/DonationServlet")
public class DonationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public DonationServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		StringBuilder sb = new StringBuilder();
		String s;
		while ((s = request.getReader().readLine()) != null) {
			sb.append(s);
		}
		Logger.getLogger(getServletName()).log(Level.INFO, "Received request data - " + sb.toString());
		JSONObject req = new JSONObject(sb.toString());
		try{
			createPledge(req);
		}catch(Exception e){
			response.getWriter().append( "Internal Server Error " + e.getMessage() );
			e.printStackTrace();
		}

		response.getWriter().append("Donated Successfully");

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	
	/*
	// main method for local test
	public static void main(String[] args){
		try {
			createPledge(new JSONObject());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	*/
	
	/*
	 * This method invokes donateMoney method of smart contract
	 */
	private static String createPledge(JSONObject req) throws Exception {
		
		ProfileVO profileData = ConnectionProfileLoader.loadProfileData();
		
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
		orderer_properties.put("pemBytes", profileData.getOrdererPem().getBytes());
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
		request.setFcn("donateMoney");
		String[] arguments = { req.getString("amount"), req.getString("name"), req.getString("phone"), req.getString("email")};
		//String[] arguments = { "500","ghi","F","1234","qa@gmail.com"}; // for local testing
		request.setArgs(arguments);
		request.setProposalWaitTime(3000);

		Collection<ProposalResponse> responses = channel.sendTransactionProposal(request);
		for (ProposalResponse res : responses) {
			Status status = res.getStatus();
			Logger.getLogger(DonationServlet.class.getName()).log(Level.INFO,
					"Invoked createNeed on " + cc + ". Status - " + status);
		}

		CompletableFuture<TransactionEvent> cf = channel.sendTransaction(responses);
		Logger.getLogger(DonationServlet.class.getName()).log(Level.INFO, "cf = " + cf.toString());
		
		
		return null;
	}


}
