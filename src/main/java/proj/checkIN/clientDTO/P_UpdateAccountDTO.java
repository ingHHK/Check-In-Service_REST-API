package proj.checkIN.clientDTO;

public class P_UpdateAccountDTO extends AccountIdentityDTO{
	private String accountName;
	private String deviceID;
	private boolean deviceEnable;
	private boolean result;
	private int otpEnable;
	

	public String getAccountName() {
		return accountName;
	}
	
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	
	public String getDeviceID() {
		return deviceID;
	}
	
	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}
	
	public boolean isDeviceEnable() {
		return deviceEnable;
	}
	
	public void setDeviceEnable(boolean deviceEnable) {
		this.deviceEnable = deviceEnable;
	}
	
	public boolean isResult() {
		return result;
	}
	
	public void setResult(boolean result) {
		this.result = result;
	}
	
	public int getOtpEnable() {
		return otpEnable;
	}
	
	public void setOtpEnable(int otpEnable) {
		this.otpEnable = otpEnable;
	}
}
