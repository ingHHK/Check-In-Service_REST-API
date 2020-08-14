package proj.checkIN.clientDTO;

public class M_LoginNumberDTO extends AccountIdentityDTO{
	private String deviceID;
    private String loginNumber;
    private int result;
    
    
    public String getDeviceID() {
		return deviceID;
	}
    
	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
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
