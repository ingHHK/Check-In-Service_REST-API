package proj.checkIN.clientDTO;

import java.util.List;

import proj.checkIN.DB.AgentAccountLogDTO;

public class M_AccessLogDTO {
    private String agentID;
    private String jwt;
	private List<AgentAccountLogDTO> accessLogItemArrayList;
    private boolean result;

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
    
    public List<AgentAccountLogDTO> getAccessLogItemArrayList() {
		return accessLogItemArrayList;
	}

	public void setAccessLogItemArrayList(List<AgentAccountLogDTO> accessLogItemArrayList) {
		this.accessLogItemArrayList = accessLogItemArrayList;
	}

	public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}