package proj.checkIN.services;

import java.io.IOException;

public interface EmailHandler {
	public boolean isDuplicate(String agentID);
	public String signUpEmail(String e_mail) throws IOException;
	public String verifyCodeEmail(String e_mail) throws IOException;
}
