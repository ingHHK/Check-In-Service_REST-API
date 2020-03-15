package com.inghhk.checkIN;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import javax.inject.Inject;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class EmailTest {
	@Inject
	JavaMailSender mailSender;
	
	@RequestMapping(value="/auth.do", method = RequestMethod.POST)
	public ModelAndView mailSending(HttpServletRequest request, String e_mail, HttpServletResponse response_email) throws IOException{
		Random r = new Random();
		int dice = r.nextInt(4589362) + 49311;
		
		String setfrom = "checkin.service.team@gmail.com";
		String tomail = request.getParameter("e_mail");
		String title = "회원가입 인증 이메일 입니다.";
		String content =
				
				System.getProperty("line.seperator") +
				System.getProperty("line.seperator") +
				"안녕하세요 회원님, Check-IN 서비스를 찾아주셔서 감사합니다."
				+System.getProperty("line.separator") +
				System.getProperty("line.separator") +
				"인증번호는 " + dice + "입니다."
				+System.getProperty("line.separator") +
				System.getProperty("line.separator") +
				"받으신 인증번호를 에이전트에 입력해주시면 다음으로 넘어갑니다.";
		
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
		
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/email_injeung");
		mv.addObject("dice", dice);
		
		System.out.println("mv: " + mv);
		
		response_email.setContentType("text/html; charset=UTF-8");
		PrintWriter out_email = response_email.getWriter();
		out_email.println("<script>alert('이메일이 발송되었습니다. 인증번호를 입력해주세요.');</script>");
		out_email.flush();
		
		return mv;
	}
	
	@RequestMapping("/email.do")
	public String email() {
		return "/email";
	}
	
	@RequestMapping(value = "/join_injeung.do{dice}", method = RequestMethod.POST)
	public ModelAndView join_injeung(String email_injeung, @PathVariable String dice, HttpServletResponse response_equals) throws IOException{
		System.out.println("마지막 : email_injeung : " + email_injeung);
		System.out.println("마지막 : dice : " + dice);
		
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/join.do");
		mv.addObject("e_mail", email_injeung);
		if(email_injeung.equals(dice)) {
			mv.setViewName("join");
			mv.addObject("e_mail", email_injeung);
			response_equals.setContentType("text/html; charset=UTF-8");
			PrintWriter out_equals = response_equals.getWriter();
			out_equals.println("<script>alert('인증번호가 일치하였습니다. 회원가입창으로 이동합니다.');</script>");
			out_equals.flush();
			
			return mv;
		}
		else if (email_injeung != dice) {
			ModelAndView mv2 = new ModelAndView();
			mv2.setViewName("member/email_injeung");
			response_equals.setContentType("text/html; charset=UTF-8");
			PrintWriter out_equals = response_equals.getWriter();
			out_equals.println("<script>alert('인증번호가 일치하지 않습니다. 인증번호를 다시 입력해주세요.');history.go(-1);</script>");
			out_equals.flush();
			
			return mv2;
		}
		return mv;
	}
}
