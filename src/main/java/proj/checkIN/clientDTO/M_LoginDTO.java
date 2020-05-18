package proj.checkIN.clientDTO;

public class M_LoginDTO {
	private String agentID;
    private String agentPW;
    private String deviceID;
    private String deviceName;
    private String jwt;    
	private int result;

	public String getJwt() {
		return jwt;
	}

	public void setJwt(String jwt) {
		this.jwt = jwt;
	}

	 public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
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

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }
}
