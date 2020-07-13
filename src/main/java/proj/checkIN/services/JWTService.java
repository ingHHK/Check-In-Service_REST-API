package proj.checkIN.services;

public interface JWTService {
	public String create(String agentID, boolean mobile_status);
	public boolean validation(String jwsString, String agentID, boolean mobile_status);
}
