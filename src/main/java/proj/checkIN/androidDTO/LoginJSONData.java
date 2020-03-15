package proj.checkIN.androidDTO;

public class LoginJSONData {
    private String id;
    private String pwd;
    private String UUID;
    private String jwtString;
	private boolean result;
    
    public String getJwtString() {
		return jwtString;
	}

	public void setJwtString(String jwtString) {
		this.jwtString = jwtString;
	}

    public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public LoginJSONData() {
    	
    }
    
    public LoginJSONData(String id, String pwd, String UUID) {
        this.id = id;
        this.pwd = pwd;
        this.UUID = UUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPwd() { return pwd; }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }
}
