package proj.checkIN.clientDTO;

import java.util.List;

import proj.checkIN.DB.AgentAccountLogDTO;

public class M_AccessLogDTO extends AccountIdentityDTO{
	private List<AgentAccountLogDTO> accessLogItemArrayList;
    private boolean result;
    
    
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