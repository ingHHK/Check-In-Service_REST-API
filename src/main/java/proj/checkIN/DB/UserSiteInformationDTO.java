package proj.checkIN.DB;

public class UserSiteInformationDTO {
    private String agentID;
    private String name;
    private String URL;
    private String ID;
    private String PW;
    private boolean result;

    public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public String getAgentID() {
        return agentID;
    }

    public String getName() {
        return name;
    }

    public String getURL() {
        return URL;
    }

    public String getID() {
        return ID;
    }

    public String getPW() {
        return PW;
    }

    public void setAgentID(String agentID) {
        this.agentID = agentID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setPW(String PW) {
        this.PW = PW;
    }
}
