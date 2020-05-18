package proj.checkIN.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import proj.checkIN.DB.AgentAccountDAOImpl;
import proj.checkIN.DB.AgentAccountDTO;
import proj.checkIN.DB.RemoteDeviceDAOImpl;
import proj.checkIN.DB.RemoteDeviceDTO;
import proj.checkIN.DB.TokenKeyDAOImpl;
import proj.checkIN.DB.TokenKeyDTO;
import proj.checkIN.DB.UserSiteInformationDAOImpl;
import proj.checkIN.DB.UserSiteInformationDTO;
import proj.checkIN.clientDTO.LoginJSONData;
import proj.checkIN.clientDTO.M_LoginDTO;
import proj.checkIN.clientDTO.M_LoginNumberDTO;
import proj.checkIN.services.CreateLoginNumber;
import proj.checkIN.services.EmailHandlerImpl;
import proj.checkIN.services.JWTServiceImpl;
import proj.checkIN.services.RedisService;

@CrossOrigin(origins="*")
@RestController
public class RestControllers {
	@Autowired
	EmailHandlerImpl email;
	@Autowired
	AgentAccountDAOImpl account;
	@Autowired
	UserSiteInformationDAOImpl siteInfo;
	@Autowired
	RemoteDeviceDAOImpl remoteDevice;
	@Autowired
	JWTServiceImpl jws;
	@Autowired
	TokenKeyDAOImpl tokenKey;
	@Autowired
	RedisService redis;
	
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
	
	@RequestMapping(value = "/signIn", method = { RequestMethod.GET,RequestMethod.POST}, consumes="application/json", headers = "content-type=application/x-www-form-urlencoded")
	public String signIn(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, ClassNotFoundException, SQLException {		
		ObjectMapper mapper = new ObjectMapper();
		BufferedReader reader = request.getReader();
		LoginJSONData request_data = mapper.readValue(reader, LoginJSONData.class);
		LoginJSONData response_data = new LoginJSONData();
		
		final String agentID = request_data.getAgentID();
		final String agentPW = request_data.getAgentPW();
		
		TokenKeyDTO token = new TokenKeyDTO();
		token.setAgentID(agentID);
		token.setToken(request_data.getJwt());
		AgentAccountDTO db_arg = new AgentAccountDTO();
		db_arg.setAgentID(agentID);
		AgentAccountDTO db_info = account.read(db_arg);
		response_data.setAgentID(request_data.getAgentID());
				
		if(agentPW.equals(db_info.getAgentPW())) {	//비밀번호 일치 확인
			response_data.setResult(true);
			if(token.getToken() != null) {
				tokenKey.delete(token);
			}
			String jwt = jws.create(agentID);		//로그인 토큰 생성
			response_data.setJwt(jwt);
			response_data.setAgentID(agentID);
			
			String returnData = mapper.writeValueAsString(response_data);
			return returnData;
		} else {
			response_data.setResult(false);
			String returnData = mapper.writeValueAsString(response_data);
			response_data.setAgentID(agentID);

			return returnData;
		}
	}

	@RequestMapping(value = "/signOut", method = RequestMethod.POST, consumes="application/json", headers = "content-type=application/x-www-form-urlencoded")
	public String signOut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, ClassNotFoundException, SQLException {		
		ObjectMapper mapper = new ObjectMapper();
		BufferedReader reader = request.getReader();
		LoginJSONData request_data = mapper.readValue(reader, LoginJSONData.class);
		LoginJSONData response_data = new LoginJSONData();
		
		TokenKeyDAOImpl token = TokenKeyDAOImpl.getInstance();
		TokenKeyDTO token_dto = new TokenKeyDTO();		
		
		final String agentID = request_data.getAgentID();
		response_data.setAgentID(agentID);

		final String jwt = request_data.getJwt();

		
		token_dto.setAgentID(agentID);
		
		response_data.setJwt(jwt);
		if(jws.validation(jwt, agentID)) {
			token.delete(token_dto);
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
	
	@RequestMapping(value = "/siteRead", method = RequestMethod.POST, consumes="application/json", headers = "content-type=application/x-www-form-urlencoded")
	public String siteRead(HttpServletRequest request) throws IOException, ServletException, ClassNotFoundException, SQLException {		
		ObjectMapper mapper = new ObjectMapper();
		BufferedReader reader = request.getReader();
		UserSiteInformationDTO request_data = mapper.readValue(reader, UserSiteInformationDTO.class);
		UserSiteInformationDTO response_data = new UserSiteInformationDTO();
		
		final String jwt = request_data.getJwt();
		final String agentID = request_data.getAgentID();
		
		if(jws.validation(jwt, agentID)) {
			response_data.setList(siteInfo.readAll(request_data));
			response_data.setResult(true);
		}
		else {
			response_data.setResult(false);
		}
		
		String returnData = mapper.writeValueAsString(response_data);
		return returnData;
	}
	
	//Mobile App API//
	@RequestMapping(value = "/signIn_M", method = { RequestMethod.GET,RequestMethod.POST}, consumes="application/json", headers = "content-type=application/x-www-form-urlencoded")
	public String signIn_M(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, ClassNotFoundException, SQLException {		
		ObjectMapper mapper = new ObjectMapper();
		BufferedReader reader = request.getReader();
		M_LoginDTO request_data = mapper.readValue(reader, M_LoginDTO.class);
		M_LoginDTO response_data = new M_LoginDTO();
		
		final String agentID = request_data.getAgentID();
		final String agentPW = request_data.getAgentPW();
		final String deviceID = request_data.getDeviceID();
		final String deviceName = request_data.getDeviceName();
		final String jwt = request_data.getJwt();
		
		TokenKeyDTO token = new TokenKeyDTO();
		token.setToken(jwt);
		token.setAgentID(agentID);
		RemoteDeviceDTO deviceDTO = new RemoteDeviceDTO();
		deviceDTO.setAgentID(agentID);
		deviceDTO.setDeviceID(deviceID);
		RemoteDeviceDTO db_deviceDTO = new RemoteDeviceDTO();
		db_deviceDTO = remoteDevice.read(deviceDTO);
		AgentAccountDTO db_arg = new AgentAccountDTO();
		db_arg.setAgentID(agentID);
		AgentAccountDTO db_info = account.read(db_arg);
		response_data.setAgentID(request_data.getAgentID());
		
		if(agentPW.equals(db_info.getAgentPW())) {	//비밀번호 일치 확인
			if(db_deviceDTO == null) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date d_time = new Date();
				String time = format.format(d_time);
				deviceDTO.setEnrollmentDate(time);
				deviceDTO.setDeviceName(deviceName);
				remoteDevice.insert(deviceDTO);
				db_deviceDTO = remoteDevice.read(deviceDTO);
			}
			if(db_deviceDTO.isDeviceEnable()) {
				String new_jwt = jws.create(agentID);		//로그인 토큰 생성
				response_data.setJwt(new_jwt);
				response_data.setResult(1);
				response_data.setAgentID(agentID);
			}
			else {
				response_data.setResult(2);
				response_data.setAgentID(agentID);
			}
			String returnData = mapper.writeValueAsString(response_data);
			return returnData;
		} else {
			response_data.setAgentID(agentID);
			response_data.setResult(0);
			String returnData = mapper.writeValueAsString(response_data);
			response_data.setAgentID(agentID);

			return returnData;
		}
	}

	@RequestMapping(value = "/signOut_M", method = RequestMethod.POST, consumes="application/json", headers = "content-type=application/x-www-form-urlencoded")
	public String signOut_M(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, ClassNotFoundException, SQLException {		
		ObjectMapper mapper = new ObjectMapper();
		BufferedReader reader = request.getReader();
		M_LoginDTO request_data = mapper.readValue(reader, M_LoginDTO.class);
		M_LoginDTO response_data = new M_LoginDTO();
		
		TokenKeyDAOImpl token = TokenKeyDAOImpl.getInstance();
		TokenKeyDTO token_dto = new TokenKeyDTO();		
		
		final String agentID = request_data.getAgentID();
		final String jwt = request_data.getJwt();
		response_data.setAgentID(agentID);
		response_data.setJwt(jwt);
		
		token_dto.setAgentID(agentID);
		token_dto.setToken(jwt);
		
		if(jws.validation(jwt, agentID)) {
			token.delete(token_dto);
			response_data.setResult(1);
		}
		else {
			response_data.setResult(0);
		}
		
		String returnData = mapper.writeValueAsString(response_data);
		return returnData;
	}

	@RequestMapping(value = "/remoteSignOut", method = RequestMethod.POST, consumes="application/json", headers = "content-type=application/x-www-form-urlencoded")
	public String remoteSignOut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, ClassNotFoundException, SQLException {		
		ObjectMapper mapper = new ObjectMapper();
		BufferedReader reader = request.getReader();
		M_LoginDTO request_data = mapper.readValue(reader, M_LoginDTO.class);
		M_LoginDTO response_data = new M_LoginDTO();
		
		final String agentID = request_data.getAgentID();
		response_data.setAgentID(agentID);
				
		String returnData = mapper.writeValueAsString(response_data);
		return returnData;
	}
	
	@RequestMapping(value = "/loginNumber/create", method = RequestMethod.POST, consumes="application/json", headers = "content-type=application/x-www-form-urlencoded")
	public String loginNumber_create(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, ClassNotFoundException, SQLException, InvalidKeyException, NoSuchAlgorithmException {		
		ObjectMapper mapper = new ObjectMapper();
		BufferedReader reader = request.getReader();
		M_LoginNumberDTO request_data = mapper.readValue(reader, M_LoginNumberDTO.class);
		M_LoginNumberDTO response_data = new M_LoginNumberDTO();
		
		String key = UUID.randomUUID().toString();
		long time = new Date().getTime();
		String number;
		
		final String agentID = request_data.getAgentID();
		final String jwt = request_data.getJwt();
		response_data.setAgentID(agentID);
		response_data.setJwt(jwt);
		
		if(jws.validation(jwt, agentID)) {
			number = String.valueOf(CreateLoginNumber.verify_code(key, time));
			redis.setData(agentID, number);
			response_data.setLoginNumber(number);
			response_data.setResult(1);
		}
		else {
			response_data.setResult(0);
		}
		
		String returnData = mapper.writeValueAsString(response_data);
		return returnData;
	}
	
	@RequestMapping(value = "/loginNumber/verify", method = RequestMethod.POST, consumes="application/json", headers = "content-type=application/x-www-form-urlencoded")
	public String loginNumber_verify(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, ClassNotFoundException, SQLException, InvalidKeyException, NoSuchAlgorithmException {		
		ObjectMapper mapper = new ObjectMapper();
		BufferedReader reader = request.getReader();
		M_LoginNumberDTO request_data = mapper.readValue(reader, M_LoginNumberDTO.class);
		M_LoginNumberDTO response_data = new M_LoginNumberDTO();
		
		final String agentID = request_data.getAgentID();
		final String jwt = request_data.getJwt();
		response_data.setAgentID(agentID);
		response_data.setJwt(jwt);
		
		if(jws.validation(jwt, agentID)) {
			if(request_data.getLoginNumber() == redis.getData(agentID)) {
				response_data.setResult(1);
			}
			else response_data.setResult(2);
		}
		else {
			response_data.setResult(0);
		}
		
		String returnData = mapper.writeValueAsString(response_data);
		return returnData;
	}
	
	@RequestMapping(value = "/accessLog", method = RequestMethod.POST, consumes="application/json", headers = "content-type=application/x-www-form-urlencoded")
	public String accessLog(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, ClassNotFoundException, SQLException {		
		ObjectMapper mapper = new ObjectMapper();
		BufferedReader reader = request.getReader();
		M_LoginDTO request_data = mapper.readValue(reader, M_LoginDTO.class);
		M_LoginDTO response_data = new M_LoginDTO();
		
		final String agentID = request_data.getAgentID();
		response_data.setAgentID(agentID);

		String returnData = mapper.writeValueAsString(response_data);
		return returnData;
	}
}