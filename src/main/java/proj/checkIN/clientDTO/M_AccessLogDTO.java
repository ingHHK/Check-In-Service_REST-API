package proj.checkIN.clientDTO;

import java.util.ArrayList;

public class M_AccessLogDTO {
    private String agentID;
    private String jwt;
	private ArrayList<M_AccessLogItem> accessLogItemArrayList;
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

    public ArrayList<M_AccessLogItem> getAccessLogItemArrayList() {
        return accessLogItemArrayList;
    }

    public void setAccessLogItemArrayList(ArrayList<M_AccessLogItem> accessLogItemArrayList) {
        this.accessLogItemArrayList = accessLogItemArrayList;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}