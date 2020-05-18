package proj.checkIN.DB;

public class RemoteDeviceDTO {
    private String agentID;
    private String deviceID;
    private String deviceName;
    private String enrollmentDate;
    private boolean deviceEnable;

    public String getAgentID() {
        return agentID;
    }

    public void setAgentID(String agentID) {
        this.agentID = agentID;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(String enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

	public boolean isDeviceEnable() {
		return deviceEnable;
	}

	public void setDeviceEnable(boolean deviceEnable) {
		this.deviceEnable = deviceEnable;
	}
}
