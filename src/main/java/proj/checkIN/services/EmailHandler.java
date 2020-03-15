package proj.checkIN.services;

import java.io.IOException;

public interface EmailHandler {
	public boolean isDuplicate(String agentID);
	public String mailSending(String e_mail)throws IOException;
}
