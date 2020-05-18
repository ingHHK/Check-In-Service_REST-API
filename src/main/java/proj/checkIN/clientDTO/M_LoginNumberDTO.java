package proj.checkIN.clientDTO;

public class M_LoginNumberDTO {
	private String agentID;
	private String deviceID;
    private String loginNumber;
    private String jwt;
    private int result;
    
    public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	public String getJwt() {
		return jwt;
	}

	public void setJwt(String jwt) {
		this.jwt = jwt;
	}

	public String getAgentID() {
        return agentID;
    }

    public void setAgentID(String agentID) {
        this.agentID = agentID;
    }

	public String getLoginNumber() {
		return loginNumber;
	}

	public void setLoginNumber(String loginNumber) {
		this.loginNumber = loginNumber;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}
}
