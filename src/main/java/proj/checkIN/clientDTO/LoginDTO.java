package proj.checkIN.clientDTO;

public class LoginDTO {
    private String agentID;
    private String agentPW;
    private String name;
    private String jwt;
	private boolean result;	
	private int otpEnable;
	
	public LoginDTO() {
		
	}
    
    public LoginDTO(String agentID, String agentPW, String name) {
        this.agentID = agentID;
        this.agentPW = agentPW;
        this.name = name;
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

	public int getOtpEnable() {
		return otpEnable;
	}

	public void setOtpEnable(int i) {
		this.otpEnable = i;
	}
}
