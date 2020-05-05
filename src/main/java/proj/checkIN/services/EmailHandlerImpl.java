package proj.checkIN.services;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Random;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import proj.checkIN.DB.AgentAccountDAOImpl;
import proj.checkIN.DB.AgentAccountDTO;

@Service
public class EmailHandlerImpl implements EmailHandler{
	public EmailHandlerImpl() {
		
	}
	@Autowired
	AgentAccountDAOImpl accountdao;
	
	@Override
	public boolean isDuplicate(String agentID) {
		boolean result = false;
		AgentAccountDTO accountdto = new AgentAccountDTO();
		accountdto.setAgentID(agentID);
		try {
			result = accountdao.isKey(accountdto);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public String mailSending(String e_mail) throws IOException{
		Random r = new Random();
		int dice = r.nextInt(4589362) + 49311;
		
		Properties prop = new Properties();
		prop.put("mail.smtp.host", "smtp.gmail.com");
		prop.put("mail.smtp.socketFactory.port", "465");
		prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.smtp.port", "465");
		prop.put("mail.smtp.ssl.checkserveridentity", "true");
		prop.put("mail.debug", "true");
		
		Session session = Session.getDefaultInstance(prop, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("checkin.service.team@gmail.com", "jgpfswziiadqxzfj");
			}
		});
		
		String setfrom = "checkin.service.team@gmail.com";
		String tomail = e_mail;
		String title = "Check-IN 서비스 회원가입 인증 이메일 입니다.";
		String content =
				"안녕하세요 회원님, Check-IN 서비스를 찾아주셔서 감사합니다."
				+System.getProperty("line.separator") +
				System.getProperty("line.separator") +
				"인증번호는 " + dice + "입니다."
				+System.getProperty("line.separator") +
				System.getProperty("line.separator") +
				"받으신 인증 번호를 에이전트에 입력해주시면 다음 단계로 넘어갑니다.";
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(setfrom));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(e_mail));
			message.setSubject(title);
			message.setText(content);
			Transport.send(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return Integer.toString(dice);
	}
}
