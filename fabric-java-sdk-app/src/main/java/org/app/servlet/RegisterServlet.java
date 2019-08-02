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
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.json.JSONObject;

/**
 * Servlet implementation class RegisterServlet
 */
@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			StringBuilder sb = new StringBuilder();
			String s;
			while ((s = request.getReader().readLine()) != null) {
				sb.append(s);
			}
			Logger.getLogger(getServletName()).log(Level.INFO, "Received request data - " + sb.toString());
			JSONObject req = new JSONObject(sb.toString());
			enrolladmin(req);
		} catch (Exception e) {
			response.getWriter().append( "Internal Server Error " + e.getMessage() );
			e.printStackTrace();
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	
	/*
	// main method used for testing
	public static void main(String[] args){
		try {
			enrolladmin(new JSONObject());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	 */
	
	/*
	 * This method invokes admin registration of HFCA client
	 */
	private static void enrolladmin(JSONObject req) throws Exception{
		ProfileVO profileData = ConnectionProfileLoader.loadProfileData();

		// Create an Object to interact with Hyperldger Fabric network
		// and set necessary data
		Properties properties = new Properties();
        String pemStr = profileData.getCaPem();
        properties.put("pemBytes", pemStr.getBytes());
		HFCAClient hfcaClient = HFCAClient.createNewInstance(profileData.getCaName(), profileData.getCaURL(), properties);
		CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
		hfcaClient.setCryptoSuite(cryptoSuite);


		UserContext adminUserContext = new UserContext();
		adminUserContext.setName(profileData.getAdmin());
		adminUserContext.setAffiliation(profileData.getOrgAffiliation());
		adminUserContext.setMspId(profileData.getConnectingOrgMSP());

		// Enroll Admin
		Enrollment adminEnrollment = hfcaClient.enroll(profileData.getAdmin(), profileData.getAdminpw());
		adminUserContext.setEnrollment(adminEnrollment);

		/*
		 * After admin registration, the certificates needs to be stored
		 * in file system. The below line stores admin user's context in 
		 * the project's runtime folder. Certificates will subsequently
		 * be retrieved for interacting with the blockchain network
		 */
		Util.writeUserContext(adminUserContext);
		Logger.getLogger(RegisterServlet.class.getName()).log(Level.INFO, "Admin enrolled");
	}


}
