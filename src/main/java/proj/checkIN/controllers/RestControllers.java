package proj.checkIN.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.check_in.dao.AgentAccountDAO;
import com.check_in.dao.TokenKeyDAOImpl;
import com.check_in.dao.UserSiteInformationDAO;
import com.check_in.dto.AgentAccountDTO;
import com.check_in.dto.TokenKeyDTO;
import com.check_in.dto.UserSiteInformationDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import proj.checkIN.androidDTO.LoginJSONData;
import proj.checkIN.services.EmailHandlerImpl;
import proj.checkIN.services.JWTServiceImpl;

@CrossOrigin
@RestController
public class RestControllers {
	@Autowired
	EmailHandlerImpl email;
	@Autowired
	AgentAccountDAO account;
	@Autowired
	UserSiteInformationDAO siteInfo;
	@Autowired
	JWTServiceImpl jws;
	
	@RequestMapping(value = "/signUp/verifyEmail", method = RequestMethod.POST, consumes="application/json", headers = "content-type=application/x-www-form-urlencoded")
	public String verifyEmail(HttpServletRequest request) throws IOException, ServletException {		
		ObjectMapper mapper = new ObjectMapper();
		BufferedReader reader = request.getReader();
		AgentAccountDTO request_data = mapper.readValue(reader, AgentAccountDTO.class);
		AgentAccountDTO response_data = new AgentAccountDTO();
		
		final String agentID = request_data.getAgentID();
		
		if(email.isDuplicate(agentID)) {	//이메일 중복 여부 확인
			response_data.setResult(false);
			String returnData = mapper.writeValueAsString(response_data);
			return returnData;
		} else {
			String verify_code = email.mailSending(agentID);	//중복이 아니라면, 인증 코드 이메일 보내기
			response_data.setResult(true);
			response_data.setVerify_code(verify_code);	//인증 코드 response 객체에 담아서 보내기
			String returnData = mapper.writeValueAsString(response_data);
			return returnData;
		}
	}
	
	@RequestMapping(value = "/signUp/signAccount", method = RequestMethod.POST, consumes="application/json", headers = "content-type=application/x-www-form-urlencoded")
	public String signAccount(HttpServletRequest request) throws IOException, ServletException, ClassNotFoundException, SQLException {		
		ObjectMapper mapper = new ObjectMapper();
		BufferedReader reader = request.getReader();
		AgentAccountDTO request_data = mapper.readValue(reader, AgentAccountDTO.class);
		AgentAccountDTO response_data = new AgentAccountDTO();
		
		int result = account.insert(request_data);	//request 데이터를 그대로 insert 함수로 전달
		
		if(result == 1) {	//함수 결과가 1이면 성공, 0이면 실패
			response_data.setResult(true);
		} else {
			response_data.setResult(false);
		}
		
		String returnData = mapper.writeValueAsString(response_data);
		
		return returnData;
	}
	
	@RequestMapping(value = "/signIn", method = RequestMethod.POST, consumes="application/json", headers = "content-type=application/x-www-form-urlencoded")
	public String signIn(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, ClassNotFoundException, SQLException {		
		ObjectMapper mapper = new ObjectMapper();
		BufferedReader reader = request.getReader();
		LoginJSONData request_data = mapper.readValue(reader, LoginJSONData.class);
		
		LoginJSONData response_data = new LoginJSONData();
		
		final String agentID = request_data.getId();
		final String agentPW = request_data.getPwd();
		
		AgentAccountDTO db_arg = new AgentAccountDTO();
		db_arg.setAgentID(agentID);
		AgentAccountDTO db_info = account.read(db_arg);
		
		if(agentPW.equals(db_info.getAgentPW())) {	//비밀번호 일치 확인
			response_data.setResult(true);
			String jwt = jws.create(agentID);		//로그인 토큰 생성
			response.setHeader("Authorization",jwt);

			String returnData = mapper.writeValueAsString(response_data);
			return returnData;
		} else {
			response_data.setResult(false);
			String returnData = mapper.writeValueAsString(response_data);
			return returnData;
		}
	}
	
	@RequestMapping(value = "/signOut", method = RequestMethod.POST, consumes="application/json", headers = "content-type=application/x-www-form-urlencoded")
	public String signOut(HttpServletRequest request) throws IOException, ServletException, ClassNotFoundException, SQLException {		
		ObjectMapper mapper = new ObjectMapper();
		BufferedReader reader = request.getReader();
		LoginJSONData request_data = mapper.readValue(reader, LoginJSONData.class);
		LoginJSONData response_data = new LoginJSONData();
		
		TokenKeyDAOImpl token = TokenKeyDAOImpl.getInstance();
		TokenKeyDTO token_dto = new TokenKeyDTO();		
		
		final String jwsString = request.getHeader("Authorization");
		final String agentID = request_data.getId();
		
		response_data.setJwtString(jwsString);

		token_dto.setAgentID(agentID);
		
		response_data.setJwtString(jwsString);
		if(jws.validation(jwsString, agentID)) {
			token.delete(token_dto);
			//--> jwt expiration
			response_data.setResult(true);
		}
		else {
			response_data.setResult(false);
		}
		
		String returnData = mapper.writeValueAsString(response_data);
		return returnData;
	}
	
	@RequestMapping(value = "/siteAdd", method = RequestMethod.POST, consumes="application/json", headers = "content-type=application/x-www-form-urlencoded")
	public String siteAdd(HttpServletRequest request) throws IOException, ServletException, ClassNotFoundException, SQLException {		
		ObjectMapper mapper = new ObjectMapper();
		BufferedReader reader = request.getReader();
		UserSiteInformationDTO request_data = mapper.readValue(reader, UserSiteInformationDTO.class);
		UserSiteInformationDTO response_data = new UserSiteInformationDTO();
		
		
		final String jwsString = request.getHeader("jwt");
		final String agentID = request_data.getAgentID();
		
		if(jws.validation(jwsString, agentID)) {
			siteInfo.insert(request_data);
			response_data.setResult(true);
		}
		else {
			response_data.setResult(false);
		}
		
		String returnData = mapper.writeValueAsString(response_data);
		return returnData;
	}
	
	@RequestMapping(value = "/siteEdit", method = RequestMethod.POST, consumes="application/json", headers = "content-type=application/x-www-form-urlencoded")
	public String siteEdit(HttpServletRequest request) throws IOException, ServletException, ClassNotFoundException, SQLException {		
		ObjectMapper mapper = new ObjectMapper();
		BufferedReader reader = request.getReader();
		UserSiteInformationDTO request_data = mapper.readValue(reader, UserSiteInformationDTO.class);
		UserSiteInformationDTO response_data = new UserSiteInformationDTO();
		
		final String jwsString = request.getHeader("jwt");
		final String agentID = request_data.getAgentID();

		if(jws.validation(jwsString, agentID)) {
			siteInfo.update(request_data);
			response_data.setResult(true);
		}
		else {
			response_data.setResult(false);
		}
		
		String returnData = mapper.writeValueAsString(response_data);
		return returnData;
	}
	
	@RequestMapping(value = "/siteDelete", method = RequestMethod.POST, consumes="application/json", headers = "content-type=application/x-www-form-urlencoded")
	public String siteDelete(HttpServletRequest request) throws IOException, ServletException, ClassNotFoundException, SQLException {		
		ObjectMapper mapper = new ObjectMapper();
		BufferedReader reader = request.getReader();
		UserSiteInformationDTO request_data = mapper.readValue(reader, UserSiteInformationDTO.class);
		UserSiteInformationDTO response_data = new UserSiteInformationDTO();
		
		final String jwsString = request.getHeader("jwt");
		final String agentID = request_data.getAgentID();
		
		if(jws.validation(jwsString, agentID)) {
			siteInfo.delete(request_data);
			response_data.setResult(true);
		}
		else {
			response_data.setResult(false);
		}
		
		String returnData = mapper.writeValueAsString(response_data);
		return returnData;
	}
}
