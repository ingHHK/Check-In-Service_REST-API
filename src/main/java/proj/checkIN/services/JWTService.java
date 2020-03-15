package proj.checkIN.services;

public interface JWTService {
	public String create(String agentID);
	public boolean validation(String jwsString);
}
