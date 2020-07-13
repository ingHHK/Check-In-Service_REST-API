package proj.checkIN.DB;

public class AgentAccountDTO {
	private String agentID;
    private String agentPW;
    private String name;
    private String verify_code;
    private String jwt;
	private boolean result;
	private int errorCount;
    private int numberOfDevice;
    private int otpEnable;
    
    public AgentAccountDTO() {
    	
    }
    
	public AgentAccountDTO(String agentID, String agentPW, String name, String verify_code, String jwt, boolean result,
			int errorCount, int numberOfDevice) {
		super();
		this.agentID = agentID;
		this.agentPW = agentPW;
		this.name = name;
		this.verify_code = verify_code;
		this.jwt = jwt;
		this.result = result;
		this.errorCount = errorCount;
		this.numberOfDevice = numberOfDevice;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVerify_code() {
		return verify_code;
	}
	public void setVerify_code(String verify_code) {
		this.verify_code = verify_code;
	}
	public String getJwt() {
		return jwt;
	}
	public void setJwt(String jwt) {
		this.jwt = jwt;
	}
	public boolean isResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	public int getErrorCount() {
		return errorCount;
	}
	public void setErrorCount(int errorCount) {
		this.errorCount = errorCount;
	}
	public int getNumberOfDevice() {
		return numberOfDevice;
	}
	public void setNumberOfDevice(int numberOfDevice) {
		this.numberOfDevice = numberOfDevice;
	}

	public int getOtpEnable() {
		return otpEnable;
	}

	public void setOtpEnable(int i) {
		this.otpEnable = i;
	}
    
    
}
