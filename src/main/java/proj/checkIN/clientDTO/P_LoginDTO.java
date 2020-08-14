package proj.checkIN.clientDTO;

public class P_LoginDTO extends AccountIdentityDTO{
    private String agentPW;
    private String name;
	private boolean result;	
	private int otpEnable;
	

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
