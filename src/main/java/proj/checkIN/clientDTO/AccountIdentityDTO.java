package proj.checkIN.clientDTO;

public class AccountIdentityDTO {
	private String agentID;
    private String jwt;
    
    
	public String getAgentID() {
		return agentID;
	}
	public void setAgentID(String agentID) {
		this.agentID = agentID;
	}
	public String getJwt() {
		return jwt;
	}
	public void setJwt(String jwt) {
		this.jwt = jwt;
	}
}
