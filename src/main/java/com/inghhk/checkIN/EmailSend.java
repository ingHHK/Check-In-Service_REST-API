package com.inghhk.checkIN;

import java.io.IOException;
import java.util.Random;

import javax.inject.Inject;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

public class EmailSend {
	@Inject
	JavaMailSender mailSender;
	
	public void mailSending(String e_mail) throws IOException{
		Random r = new Random();
		int dice = r.nextInt(4589362) + 49311;
		
		String setfrom = "checkin.service.team@gmail.com";
		String tomail = e_mail;
		String title = "Check-IN 서비스 회원가입 인증 이메일 입니다.";
		String content =
//				System.getProperty("line.seperator") +
//				System.getProperty("line.seperator") +
				"안녕하세요 회원님, Check-IN 서비스를 찾아주셔서 감사합니다."
				+System.getProperty("line.separator") +
				System.getProperty("line.separator") +
				"인증번호는 " + dice + "입니다."
				+System.getProperty("line.separator") +
				System.getProperty("line.separator") +
				"받으신 인증 번호를 에이전트에 입력해주시면 다음 단계로 넘어갑니다.";
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
			messageHelper.setFrom(setfrom);
			messageHelper.setTo(tomail);
			messageHelper.setSubject(title);
			messageHelper.setText(content);
			mailSender.send(message);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
