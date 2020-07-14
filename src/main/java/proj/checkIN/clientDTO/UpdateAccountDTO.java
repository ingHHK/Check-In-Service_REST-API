package proj.checkIN.clientDTO;

public class UpdateAccountDTO {
	private String agentID;
	private String accountName;
	private String deviceID;
	private String jwt;
	private boolean deviceEnable;
	private boolean result;
	private int otpEnable;
	
	public String getAgentID() {
		return agentID;
	}
	public void setAgentID(String agentID) {
		this.agentID = agentID;
	}
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
	public String getJwt() {
		return jwt;
	}
	public void setJwt(String jwt) {
		this.jwt = jwt;
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
