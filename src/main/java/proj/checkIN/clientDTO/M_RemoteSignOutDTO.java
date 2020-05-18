package proj.checkIN.clientDTO;

public class M_RemoteSignOutDTO {
	private String agentID;
    private String deviceID;
    private String jwt;
	private boolean result;

    public String getJwt() {
		return jwt;
	}

	public void setJwt(String jwt) {
		this.jwt = jwt;
	}

	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

    public String getAgentID() {
        return agentID;
    }

    public void setAgentID(String agentID) {
        this.agentID = agentID;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
