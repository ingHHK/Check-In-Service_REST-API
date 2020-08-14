package proj.checkIN.clientDTO;

public class M_RemoteSignOutDTO extends AccountIdentityDTO{
    private String deviceID;
	private boolean result;

	
	public String getDeviceID() {
		return deviceID;
	}
	
	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}
	
    public boolean isResult() {
        return result;
    }
    
    public void setResult(boolean result) {
        this.result = result;
    }
}
