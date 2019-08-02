package org.app.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.hyperledger.fabric.sdk.NetworkConfig;
import org.hyperledger.fabric.sdk.NetworkConfig.CAInfo;

public class ConnectionProfileLoader {

	private static ProfileVO profileVO = null;
	private static File configFile;

//	public static void main(String[] args) throws Exception{
//		configFile = new File("./connection_profile.json");
//		loadProfileData();
//	}

	/*
	 * ProfileVO is a class which has placeholders for all the parameters that are there 
	 * in connections profile of IBM Blockchain platform. Populate an object of this class
	 * so it gets easier for using Network Config data.
	 * While NetworkConfig class can provide the required data, it still needs to mature.
	 * As of version 1.4, there are no methods to get Pem certificates and also Peer URL in NetworkConfig
	 * Hence this custom class.
	 */
	public static ProfileVO loadProfileData() throws Exception{
		
		// Read the Connections Profile file, which hold network details
		ClassLoader classLoader = new ConnectionProfileLoader().getClass().getClassLoader();
		configFile = new File(classLoader.getResource("connection_profile.json").getFile());
		
		//configFile = new File("./connection_profile.json"); // Uncomment for local run
		
		if( profileVO != null ){ // Populate if not already done.
			return profileVO;
		}
		profileVO = new ProfileVO(); // initialize values holder.

		NetworkConfig networkConfig = NetworkConfig.fromJsonFile(configFile);
		setNodeData(networkConfig);

		return profileVO;
	}

	public static void setNodeData(NetworkConfig networkConfig) throws FileNotFoundException {
		/*
		 * Whereever possible, Hyperledger class NetworkConfig is being used.
		 * And wherever, NetworkConfig doesn't provide methods to get required data
		 * we build our own classes and methods
		 */
		Collection<NetworkConfig.OrgInfo> orgInfos = networkConfig.getOrganizationInfos();
		Iterator<NetworkConfig.OrgInfo> itr = orgInfos.iterator();
		while (itr.hasNext()) {
			NetworkConfig.OrgInfo orgInfo = itr.next();
			List<CAInfo> caInfos = orgInfo.getCertificateAuthorities();
			for (CAInfo caInfo : caInfos) {
				profileVO.setCaURL(caInfo.getUrl());
				profileVO.setCaKeyName(caInfo.getName());
				profileVO.setCaName(caInfo.getCAName());
				List<String> peerNames = orgInfo.getPeerNames();
				profileVO.setConnectingPeer(peerNames.get(0));
				
				Collection<String> ordererNames = networkConfig.getOrdererNames();
				Iterator<String> itrOrderer = ordererNames.iterator();
				while (itrOrderer.hasNext()) {
					profileVO.setOrdererName(itrOrderer.next());
					break; // only 1 orderer
				}
				
				profileVO.setConnectingOrgName(orgInfo.getName());
				profileVO.setConnectingOrgMSP(orgInfo.getMspId());
				
				setPropsData();
			}
		}
	}

	/*
	 * This method set's certificate details and channel name
	 */
	private static void setPropsData() throws FileNotFoundException {
		InputStream stream = new FileInputStream(configFile);
		JsonObject jsonConfig = null;
		try (JsonReader reader = Json.createReader(stream)) {
			jsonConfig = (JsonObject) reader.read();
		}
		if (jsonConfig != null) {
			String caPem = jsonConfig.getJsonObject("certificateAuthorities").getJsonObject(profileVO.getCaKeyName())
					.getJsonObject("tlsCACerts").getString("pem");
			profileVO.setCaPem(caPem);
			
			String peerPem = jsonConfig.getJsonObject("peers").getJsonObject(profileVO.getConnectingPeer())
					.getJsonObject("tlsCACerts").getString("pem");
			profileVO.setConnectingPeerPem(peerPem);

			String ordererPem = jsonConfig.getJsonObject("orderers").getJsonObject(profileVO.getOrdererName())
					.getJsonObject("tlsCACerts").getString("pem");
			profileVO.setOrdererPem(ordererPem);

			profileVO.setChannelName(jsonConfig.getString("name"));
		}
	}
	
}
