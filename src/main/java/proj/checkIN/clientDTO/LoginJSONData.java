package proj.checkIN.clientDTO;

public class LoginJSONData {
    private String agentID;
    private String agentPW;
    private String UUID;
    private String jwtString;
	private boolean result;	
	
	public LoginJSONData() {
		
	}
    
    public LoginJSONData(String agentID, String agentPW, String UUID) {
        this.agentID = agentID;
        this.agentPW = agentPW;
        this.UUID = UUID;
    }

	public String getAgentID() {
		return agentID;
	}

	public void setAgentID(String agentID) {
		this.agentID = agentID;
	}

	public String getAgentPW() {
		return agentPW;
	}

	public void setAgentPW(String agentPW) {
		this.agentPW = agentPW;
	}

	public String getUUID() {
		return UUID;
	}

	public void setUUID(String uUID) {
		UUID = uUID;
	}

	public String getJwtString() {
		return jwtString;
	}

	public void setJwtString(String jwtString) {
		this.jwtString = jwtString;
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}
}
