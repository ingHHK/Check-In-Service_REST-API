package proj.checkIN.controllers;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import proj.checkIN.DB.AgentAccountDAO;
import proj.checkIN.DB.TokenKeyDAOImpl;
import proj.checkIN.DB.TokenKeyDTO;
import proj.checkIN.DB.UserSiteInformationDAO;
import proj.checkIN.DB.UserSiteInformationDTO;
import proj.checkIN.services.EmailHandlerImpl;
import proj.checkIN.services.Encoder;
import proj.checkIN.services.JWTServiceImpl;
import proj.checkIN.services.RedisService;

@CrossOrigin(origins="*")
@Controller
public class PageController {
	@Autowired
	EmailHandlerImpl email;
	@Autowired
	AgentAccountDAO agentDAO;
	@Autowired
	UserSiteInformationDAO infoDAO;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		
		return "home";
	}
	
	@RequestMapping(value = "/testEmail", method = RequestMethod.GET)
	public String testEmail(HttpServletRequest request) throws IOException, ServletException, ClassNotFoundException, SQLException {		
		String mail = "inghyunho@naver.com";
		System.out.println(mail);
		email.mailSending(mail);
		
		return "sendOK";
	}
	
	@RequestMapping(value = "/testAdd", method = RequestMethod.GET)
	public String testAdd(HttpServletRequest request) throws IOException, ServletException, ClassNotFoundException, SQLException {		
		String agentID = "abcd@naver.com";
		String name = "상명대";
		String URL = "www.smu.ac.kr";
		String ID = "SNU";
		String PW = "674839";
		
		UserSiteInformationDTO info = new UserSiteInformationDTO();
		info.setAgentID(agentID);
		info.setID(ID);
		info.setName(name);
		info.setPW(PW);
		info.setURL(URL);
		info.setResult(true);
		
		infoDAO.insert(info);
		
		return "sendOK";
	}
	
	@RequestMapping(value = "/testDB", method = RequestMethod.GET)
	public String testDB(HttpServletRequest request) throws IOException, ServletException, ClassNotFoundException, SQLException {		
		System.out.println("testDB()");
		UserSiteInformationDTO dto = new UserSiteInformationDTO();
		UserSiteInformationDTO dbdto = new UserSiteInformationDTO();
		dto.setAgentID("abcd@naver.com");
		dto.setName("구글");
		dto.setID("abcd@gmail.com");
		dto.setPW("4321");
		dto.setURL("https://naver.com/");
		System.out.println("call infoDAO()");
		System.out.println(infoDAO.insert(dto));
		
		return "sendOK";
	}
	
	@RequestMapping(value = "/jwt", method = RequestMethod.GET)
	public String testJWT(HttpServletRequest request) throws IOException, ServletException, ClassNotFoundException, SQLException {		
		System.out.println("testJWT()");
		JWTServiceImpl jwtService = new JWTServiceImpl();
		String result = jwtService.create("abcd@naver.com");
		System.out.println(result);
		System.out.println(jwtService.validation(result, "abcd@naver.com"));
		
		return "sendOK";
	}
	@RequestMapping(value = "/jwtd", method = RequestMethod.GET)
	public String tesJWT(HttpServletRequest request) throws IOException, ServletException, ClassNotFoundException, SQLException {		
		System.out.println("Delete JWT()");
		String agentID = "abcd@naver.com";
		TokenKeyDAOImpl token = TokenKeyDAOImpl.getInstance();
		TokenKeyDTO token_dto = new TokenKeyDTO();
		
		token_dto.setAgentID(agentID);
		token.delete(token_dto);
		
		return "sendOK";
	}
	
	@RequestMapping(value = "/sha", method = RequestMethod.GET)
	public String testSHA(HttpServletRequest request) throws IOException, ServletException, ClassNotFoundException, SQLException, NoSuchAlgorithmException {		
		System.out.println("testSHA()");
		Encoder encoder = new Encoder();
		System.out.println(encoder.sha256("inghyunho@naver.com"));
		System.out.println(encoder.sha256("inghyunho@naver.com"));
		System.out.println(encoder.sha256("inghyunho@naver.con"));
		return "sendOK";
	}
	
	@RequestMapping(value = "/redis_set", method = RequestMethod.GET)
	public String redis_set(HttpServletRequest request) throws IOException, ServletException, ClassNotFoundException, SQLException, NoSuchAlgorithmException {		
		RedisService redis = new RedisService();
		redis.test_set();
		return "sendOK";
	}
	
	@RequestMapping(value = "/redis_del", method = RequestMethod.GET)
	public String redis_del(HttpServletRequest request) throws IOException, ServletException, ClassNotFoundException, SQLException, NoSuchAlgorithmException {		
		RedisService redis = new RedisService();
		redis.test_del();
		return "sendOK";
	}
}
