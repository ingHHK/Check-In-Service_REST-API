package proj.checkIN.clientDTO;

public class LoginJSONData {
    private String agentID;
    private String agentPW;
    private String UUID;
    private String jwtString;
	private boolean result;
    
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

	public LoginJSONData() {
    	
    }
    
    public LoginJSONData(String agentID, String agentPW, String UUID) {
        this.agentID = agentID;
        this.agentPW = agentPW;
        this.UUID = UUID;
    }

    public String getId() {
        return agentID;
    }

    public void setId(String id) {
        this.agentID = id;
    }

    public String getPwd() { return agentPW; }

    public void setPwd(String agentPW) {
        this.agentPW = agentPW;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }
}
