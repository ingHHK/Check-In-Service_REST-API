package proj.checkIN.clientDTO;

public class M_LoginDTO extends AccountIdentityDTO{
    private String agentPW;
    private String deviceID;
    private String deviceName;
	private int result;

	
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
