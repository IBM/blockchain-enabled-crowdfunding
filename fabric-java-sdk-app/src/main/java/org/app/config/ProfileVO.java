package org.app.config;

public class ProfileVO {
	private String caKeyName;
	private String caURL;
	private String caPem;
	private String caName;
	private String mspId;
	private String connectingPeer;
	private String connectingPeerURL;
	private String connectingPeerPem;
	private String channelName;
	private String ordererName;
	private String ordererURL;
	private String ordererPem;
	private String connectingOrgName;
	private String connectingOrgMSP;
	private String admin;
	private String adminpw;
	private String orgAffiliation;
	private String chainCodeName;
	
	/*
	 * (non-Javadoc)
	 * Override toString() method so that it can be easily logged with all the required values
	 */
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("caKeyName = " + caKeyName + ", ");
		sb.append("caURL = " + caURL + ", ");
		sb.append("caName = " + caName + ", ");
		sb.append("caPem = " + caPem + ", ");
		sb.append("connectingPeer = " + connectingPeer + ", ");
		sb.append("ordererName = " + ordererName + ", ");
		sb.append("connectingOrgName = " + connectingOrgName + ", ");
		sb.append("admin = " + admin + ", ");
		sb.append("channelName = " + channelName + ", ");
		sb.append("chainCodeName = " + chainCodeName + ", ");
		return null;
	}
	
	public ProfileVO() throws Exception{
		// Get Org Affiliation from Environment Variables.
		// If not available, use a default one
		String affiliation = System.getenv("OrgAffiliation");
		if( affiliation != null ){
			this.orgAffiliation = affiliation;
		}else{
			this.orgAffiliation = "org1";
		}
		
		// Get Admin username from Environment Variables.
		// If not available, throw error
		String admin = System.getenv("admin");
		if( admin != null ){
			this.admin = admin;
		}else{
			throw new Exception("Could not find Admin Username, in env vars, for registration");
			//this.admin = "admin";
		}
		
		// Get Admin password from Environment Variables.
		// If not available, throw error
		String adminpw = System.getenv("adminpw");
		if( adminpw != null ){
			this.adminpw = adminpw;
		}else{
			throw new Exception("Could not find Admin Password, in env vars, for regisration");
			//this.adminpw = "admin";
		}
		
		// Get Chaincode name from Environment Variables.
		// If not available, throw error
		String ccName = System.getenv("ChainCodeName");
		if( ccName != null ){
			this.chainCodeName = ccName;
		}else{
			//this.chainCodeName = "go71"; // TODO remove this line after testing and uncomment below line
			throw new Exception("ChainCode Name not found in Environment variables");
		}
	}
	
	public String getChainCodeName() {
		return chainCodeName;
	}

	public String getOrgAffiliation() {
		return orgAffiliation;
	}

	public String getAdmin() {
		return admin;
	}
	public String getAdminpw() {
		return adminpw;
	}
	public String getCaName() {
		return caName;
	}
	public void setCaName(String caCAName) {
		this.caName = caCAName;
	}
	public String getConnectingOrgName() {
		return connectingOrgName;
	}
	public void setConnectingOrgName(String connectingOrgName) {
		this.connectingOrgName = connectingOrgName;
	}
	public String getConnectingOrgMSP() {
		return connectingOrgMSP;
	}
	public void setConnectingOrgMSP(String connectingOrgMSP) {
		this.connectingOrgMSP = connectingOrgMSP;
	}
	public String getOrdererURL() {
		return ordererURL;
	}
	public void setOrdererURL(String ordererURL) {
		this.ordererURL = ordererURL;
	}
	public String getOrdererPem() {
		return ordererPem;
	}
	public void setOrdererPem(String ordererPem) {
		this.ordererPem = ordererPem;
	}
	public String getCaPem() {
		return caPem;
	}
	public void setCaPem(String caPem) {
		this.caPem = caPem;
	}
	public String getCaKeyName() {
		return caKeyName;
	}
	public void setCaKeyName(String caName) {
		this.caKeyName = caName;
	}
	public String getCaURL() {
		return caURL;
	}
	public void setCaURL(String caURL) {
		this.caURL = caURL;
	}
	public String getMspId() {
		return mspId;
	}
	public void setMspId(String mspId) {
		this.mspId = mspId;
	}
	public String getConnectingPeer() {
		return connectingPeer;
	}
	public void setConnectingPeer(String connectingPeer) {
		this.connectingPeer = connectingPeer;
		this.connectingPeerURL = "grpcs://" + this.connectingPeer;
	}
	public String getConnectingPeerURL() {
		return connectingPeerURL;
	}
	public void setConnectingPeerURL(String connectingPeerURL) {
		this.connectingPeerURL = connectingPeerURL;
	}
	public String getConnectingPeerPem() {
		return connectingPeerPem;
	}
	public void setConnectingPeerPem(String connectingPeerPem) {
		this.connectingPeerPem = connectingPeerPem;
	}
	public String getChannelName() {
		return channelName;
	}
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
	public String getOrdererName() {
		return ordererName;
	}
	public void setOrdererName(String ordererName) {
		this.ordererName = ordererName;
		this.ordererURL = "grpcs://" + this.ordererName;
	}
	
	
}
