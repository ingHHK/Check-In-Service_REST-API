package proj.checkIN.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

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
import proj.checkIN.DB.AgentAccountLogDAOImpl;
import proj.checkIN.DB.AgentAccountLogDTO;
import proj.checkIN.DB.RemoteDeviceDAOImpl;
import proj.checkIN.DB.RemoteDeviceDTO;
import proj.checkIN.DB.TokenKeyDAOImpl;
import proj.checkIN.DB.TokenKeyDTO;
import proj.checkIN.DB.UserSiteInformationDAOImpl;
import proj.checkIN.DB.UserSiteInformationDTO;
import proj.checkIN.clientDTO.LoginDTO;
import proj.checkIN.clientDTO.M_AccessLogDTO;
import proj.checkIN.clientDTO.M_LoginDTO;
import proj.checkIN.clientDTO.M_LoginNumberDTO;
import proj.checkIN.clientDTO.M_RemoteSignOutDTO;
import proj.checkIN.services.CreateLoginNumber;
import proj.checkIN.services.CreateOTP;
import proj.checkIN.services.EmailHandlerImpl;
import proj.checkIN.services.JWTServiceImpl;
import proj.checkIN.services.RabbitMQ;
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
	@Autowired
	RabbitMQ msgQ;
	@Autowired
	AgentAccountLogDAOImpl accountLog;
	@Autowired
	CreateOTP otp;
	
	private static String MOBILE = "_M";
	private final static String XHEADER = "X-FORWARDED-FOR";
	
	@RequestMapping(value="/testing", method = RequestMethod.POST)
	public Object jacksonTest(HttpServletRequest request){
		AgentAccountDTO dto = new AgentAccountDTO();
		dto.setAgentID("hyunho");
		dto.setAgentPW("123123");
		dto.setResult(true);
		return dto;
	}

	@RequestMapping(value = "/signUp/verifyEmail", method = RequestMethod.POST)
	public String verifyEmail(HttpServletRequest request) throws IOException, ServletException {		
		ObjectMapper mapper = new ObjectMapper();
		BufferedReader reader = request.getReader();
		AgentAccountDTO request_data = mapper.readValue(reader, AgentAccountDTO.class);
		AgentAccountDTO response_data = new AgentAccountDTO();
		
		final String agentID = request_data.getAgentID();
		
		if(email.isDuplicate(agentID)) {	//이메일 중복 여부 확인
			response_data.setResult(false);
		} else {
			String verify_code = email.signUpEmail(agentID);	//중복이 아니라면, 인증 코드 이메일 보내기
			response_data.setResult(true);
			response_data.setVerify_code(verify_code);	//인증 코드 response 객체에 담아서 보내기
		}
		
		String returnData = mapper.writeValueAsString(response_data);
		return returnData;
	}
	
	@RequestMapping(value = "/signUp/signAccount", method = RequestMethod.POST)
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
	
	@RequestMapping(value = "/signIn", method = { RequestMethod.GET,RequestMethod.POST})
	public String signIn(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, ClassNotFoundException, SQLException, TimeoutException, InterruptedException {		
		ObjectMapper mapper = new ObjectMapper();
		BufferedReader reader = request.getReader();
		LoginDTO request_data = mapper.readValue(reader, LoginDTO.class);
		LoginDTO response_data = new LoginDTO();
		
		final String agentID = request_data.getAgentID();
		final String agentPW = request_data.getAgentPW();
		
		TokenKeyDTO token = new TokenKeyDTO();
		token.setAgentID(agentID);
		token.setToken(request_data.getJwt());
		AgentAccountDTO db_arg = new AgentAccountDTO();
		db_arg.setAgentID(agentID);
		AgentAccountDTO db_info = account.read(db_arg);
		response_data.setAgentID(request_data.getAgentID());
		
		AgentAccountLogDTO logDTO = new AgentAccountLogDTO();
		logDTO.setAgentID(agentID);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String time = format.format(date);
		logDTO.setAccessTime(time);
		logDTO.setAccessIP((null != request.getHeader(XHEADER)?request.getHeader(XHEADER):request.getRemoteAddr()));
		
		if(agentPW.equals(db_info.getAgentPW())) {	//비밀번호 일치 확인
			response_data.setResult(true);
			if(token.getToken() != null) {			//이전 토큰이 남아있다면 삭제
				tokenKey.delete(token);
			}
			if(db_info.getOtpEnable() == 0) {
				String jwt = jws.create(agentID, false);		//로그인 토큰 생성
				response_data.setJwt(jwt);
				msgQ.connect(agentID);
				logDTO.setLoginStatus("Login(PC)");
			}
			response_data.setResult(true);
		} else {
			response_data.setResult(false);
			logDTO.setLoginStatus("Login Fail(PC)");
		}
		
		response_data.setOtpEnable(db_info.getOtpEnable());
		response_data.setAgentID(agentID);
		accountLog.insert(logDTO);
		String returnData = mapper.writeValueAsString(response_data);
		return returnData;
	}
	
	public LoginDTO signIn(String agentID) throws IOException, ServletException, ClassNotFoundException, SQLException, TimeoutException, InterruptedException {		
		LoginDTO response_data = new LoginDTO();
				
		TokenKeyDTO token = new TokenKeyDTO();
		token.setAgentID(agentID);
		AgentAccountDTO db_arg = new AgentAccountDTO();
		db_arg.setAgentID(agentID);
		
		if(token.getToken() != null) {			//이전 토큰이 남아있다면 삭제
			tokenKey.delete(token);
		}
		String jwt = jws.create(agentID, false);		//로그인 토큰 생성
		response_data.setJwt(jwt);
		response_data.setAgentID(agentID);

		msgQ.connect(agentID);
		
		return response_data;
	}
	
	@RequestMapping(value = "/signOut", method = RequestMethod.POST)
	public String signOut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, ClassNotFoundException, SQLException {		
		ObjectMapper mapper = new ObjectMapper();
		BufferedReader reader = request.getReader();
		LoginDTO request_data = mapper.readValue(reader, LoginDTO.class);
		LoginDTO response_data = new LoginDTO();
			
		
		final String agentID = request_data.getAgentID();
		response_data.setAgentID(agentID);
		final String jwt = request_data.getJwt();
		
		TokenKeyDAOImpl token = TokenKeyDAOImpl.getInstance();
		TokenKeyDTO token_dto = new TokenKeyDTO();
		token_dto.setAgentID(agentID);
		response_data.setJwt(jwt);
		
		AgentAccountLogDTO logDTO = new AgentAccountLogDTO();
		logDTO.setAgentID(agentID);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String time = format.format(date);
		logDTO.setAccessTime(time);
		logDTO.setAccessIP((null != request.getHeader(XHEADER)?request.getHeader(XHEADER):request.getRemoteAddr()));
		
		if(jws.validation(jwt, agentID, false)) {
			token.delete(token_dto);
			response_data.setResult(true);
			logDTO.setLoginStatus("Logout(PC)");
		}
		else {
			logDTO.setLoginStatus("Logout Fail(PC)");
			response_data.setResult(false);
		}
		
		accountLog.insert(logDTO);
		String returnData = mapper.writeValueAsString(response_data);
		return returnData;
	}
	
	@RequestMapping(value = "/siteAdd", method = RequestMethod.POST)
	public String siteAdd(HttpServletRequest request) throws IOException, ServletException, ClassNotFoundException, SQLException {		
		ObjectMapper mapper = new ObjectMapper();
		BufferedReader reader = request.getReader();
		UserSiteInformationDTO request_data = mapper.readValue(reader, UserSiteInformationDTO.class);
		UserSiteInformationDTO response_data = new UserSiteInformationDTO();
		
		final String jwsString = request.getHeader("jwt");
		final String agentID = request_data.getAgentID();
		
		if(jws.validation(jwsString, agentID, false)) {
			siteInfo.insert(request_data);
			response_data.setResult(true);
		}
		else {
			response_data.setResult(false);
		}
		
		String returnData = mapper.writeValueAsString(response_data);
		return returnData;
	}
	
	@RequestMapping(value = "/siteEdit", method = RequestMethod.POST)
	public String siteEdit(HttpServletRequest request) throws IOException, ServletException, ClassNotFoundException, SQLException {		
		ObjectMapper mapper = new ObjectMapper();
		BufferedReader reader = request.getReader();
		UserSiteInformationDTO request_data = mapper.readValue(reader, UserSiteInformationDTO.class);
		UserSiteInformationDTO response_data = new UserSiteInformationDTO();
		
		final String jwsString = request.getHeader("jwt");
		final String agentID = request_data.getAgentID();

		if(jws.validation(jwsString, agentID, false)) {
			siteInfo.update(request_data);
			response_data.setResult(true);
		}
		else {
			response_data.setResult(false);
		}
		
		String returnData = mapper.writeValueAsString(response_data);
		return returnData;
	}
	
	@RequestMapping(value = "/siteDelete", method = RequestMethod.POST)
	public String siteDelete(HttpServletRequest request) throws IOException, ServletException, ClassNotFoundException, SQLException {		
		ObjectMapper mapper = new ObjectMapper();
		BufferedReader reader = request.getReader();
		UserSiteInformationDTO request_data = mapper.readValue(reader, UserSiteInformationDTO.class);
		UserSiteInformationDTO response_data = new UserSiteInformationDTO();
		
		final String jwsString = request.getHeader("jwt");
		final String agentID = request_data.getAgentID();
		
		if(jws.validation(jwsString, agentID, false)) {
			siteInfo.delete(request_data);
			response_data.setResult(true);
		}
		else {
			response_data.setResult(false);
		}
		
		String returnData = mapper.writeValueAsString(response_data);
		return returnData;
	}
	
	@RequestMapping(value = "/siteRead", method = RequestMethod.POST)
	public String siteRead(HttpServletRequest request) throws IOException, ServletException, ClassNotFoundException, SQLException {		
		ObjectMapper mapper = new ObjectMapper();
		BufferedReader reader = request.getReader();
		UserSiteInformationDTO request_data = mapper.readValue(reader, UserSiteInformationDTO.class);
		UserSiteInformationDTO response_data = new UserSiteInformationDTO();
		
		final String jwt = request_data.getJwt();
		final String agentID = request_data.getAgentID();
		
		if(jws.validation(jwt, agentID, false)) {
			response_data.setList(siteInfo.readAll(request_data));
			response_data.setResult(true);
		}
		else {
			response_data.setResult(false);
		}
		
		String returnData = mapper.writeValueAsString(response_data);
		return returnData;
	}
	
	@RequestMapping(value = "/loginNumber/verify", method = RequestMethod.POST)
	public String loginNumber_verify(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, ClassNotFoundException, SQLException, InvalidKeyException, NoSuchAlgorithmException, TimeoutException, InterruptedException {		
		ObjectMapper mapper = new ObjectMapper();
		BufferedReader reader = request.getReader();
		AgentAccountDTO request_data = mapper.readValue(reader, AgentAccountDTO.class);
		LoginDTO response_data = new LoginDTO();
		
		final String verify_code = request_data.getVerify_code();
		final String agentID = redis.getCode(verify_code);
		
		AgentAccountLogDTO logDTO = new AgentAccountLogDTO();
		logDTO.setAgentID(agentID);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String time = format.format(date);
		logDTO.setAccessTime(time);
		logDTO.setAccessIP((null != request.getHeader(XHEADER)?request.getHeader(XHEADER):request.getRemoteAddr()));
		
		if(agentID != null) {
			response_data = signIn(agentID);
			logDTO.setLoginStatus("Login-One Time Login(PC)");
			response_data.setResult(true);
		}
		else {
			logDTO.setLoginStatus("Login Fail-One Time Login(PC)");
			response_data.setResult(false);
		}
		
		accountLog.insert(logDTO);
		
		String returnData = mapper.writeValueAsString(response_data);
		return returnData;
	}
	
	@RequestMapping(value = "/verifyCode", method = RequestMethod.POST)
	public String verifyCode(HttpServletRequest request) throws IOException, ServletException {		
		ObjectMapper mapper = new ObjectMapper();
		BufferedReader reader = request.getReader();
		AgentAccountDTO request_data = mapper.readValue(reader, AgentAccountDTO.class);
		AgentAccountDTO response_data = new AgentAccountDTO();
		
		final String agentID = request_data.getAgentID();
		final String jwt = request_data.getJwt();
		
		if(jws.validation(jwt, agentID, false)) {
			response_data.setResult(false);
		} else {
			String verify_code = email.verifyCodeEmail(agentID);
			response_data.setResult(true);
			response_data.setVerify_code(verify_code);
		}
		
		String returnData = mapper.writeValueAsString(response_data);
		return returnData;
	}
	
	@RequestMapping(value = "/verifyOTP", method = RequestMethod.POST)
	public Object verifyOTP(HttpServletRequest request) throws IOException, ServletException, ClassNotFoundException, SQLException, TimeoutException, InterruptedException {		
		ObjectMapper mapper = new ObjectMapper();
		BufferedReader reader = request.getReader();
		AgentAccountDTO request_data = mapper.readValue(reader, AgentAccountDTO.class);
		AgentAccountDTO response_data = new AgentAccountDTO();
		LoginDTO returnData = new LoginDTO();
		
		final String agentID = request_data.getAgentID();
		final String verify_code = request_data.getVerify_code();
		boolean verifying_result = false;
		RemoteDeviceDTO remoteDeviceDTO = new RemoteDeviceDTO();
		remoteDeviceDTO.setAgentID(agentID);
		
		List<RemoteDeviceDTO> remoteDevices = remoteDevice.readAll(remoteDeviceDTO);
		
		AgentAccountLogDTO logDTO = new AgentAccountLogDTO();
		logDTO.setAgentID(agentID);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String time = format.format(date);
		logDTO.setAccessTime(time);
		logDTO.setAccessIP((null != request.getHeader(XHEADER)?request.getHeader(XHEADER):request.getRemoteAddr()));
		
		for(int i = 0; i < remoteDevices.size(); ++i) {
			if(otp.checkCode(verify_code, remoteDevices.get(i).getDeviceID()))
				verifying_result = true;
		}
		
		if(otp.checkCode(verify_code, remoteDeviceDTO.getDeviceID())) {
			returnData = signIn(agentID);
			logDTO.setLoginStatus("Login-OTP(PC)");
		} else {
			response_data.setResult(false);
			logDTO.setLoginStatus("Login Fail-OTP(PC)");
		}
		
		accountLog.insert(logDTO);
		return returnData;
	}
	
	//////Mobile App API//////
	@RequestMapping(value = "/signIn_M", method = { RequestMethod.GET,RequestMethod.POST})
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
		deviceDTO.setDeviceName(deviceName);
		
		RemoteDeviceDTO db_deviceDTO = new RemoteDeviceDTO();
		db_deviceDTO = remoteDevice.read(deviceDTO);
		
		AgentAccountDTO db_arg = new AgentAccountDTO();
		db_arg.setAgentID(agentID);
		
		AgentAccountDTO db_info = account.read(db_arg);
		response_data.setAgentID(request_data.getAgentID());
		
		AgentAccountLogDTO logDTO = new AgentAccountLogDTO();
		logDTO.setAgentID(agentID);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String time = format.format(date);
		logDTO.setAccessTime(time);
		logDTO.setAccessIP((null != request.getHeader(XHEADER)?request.getHeader(XHEADER):request.getRemoteAddr()));

		if(agentPW.equals(db_info.getAgentPW())) {	//비밀번호 일치 확인
			if(db_deviceDTO == null) {
				deviceDTO.setEnrollmentDate(time);
				deviceDTO.setDeviceName(deviceName);
				remoteDevice.insert(deviceDTO);
				db_deviceDTO = remoteDevice.read(deviceDTO);
			}
			else if (db_deviceDTO.getDeviceName() != request_data.getDeviceName()) {
					boolean enable = false;
					if(db_deviceDTO.isDeviceEnable()) enable = true;
					remoteDevice.delete(deviceDTO);
					deviceDTO.setEnrollmentDate(time);
					deviceDTO.setDeviceName(deviceName);
					deviceDTO.setDeviceEnable(enable);
					remoteDevice.insert(deviceDTO);
					db_deviceDTO = remoteDevice.read(deviceDTO);
			}
			
			if(db_deviceDTO.isDeviceEnable()) {
				String new_jwt = jws.create(agentID, true);		//로그인 토큰 생성
				response_data.setJwt(new_jwt);
				response_data.setResult(1);
				logDTO.setLoginStatus("Login(Mobile)");
			}
			else {
				response_data.setResult(2);
				logDTO.setLoginStatus("Login Fail-DeviceEnable False(Mobile)");
			}
		} else {
			response_data.setResult(0);
			logDTO.setLoginStatus("Login Fail-(Mobile)");

		}
		String returnData = mapper.writeValueAsString(response_data);
		accountLog.insert(logDTO);
		return returnData;
	}

	@RequestMapping(value = "/signOut_M", method = RequestMethod.POST)
	public String signOut_M(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, ClassNotFoundException, SQLException {		
		ObjectMapper mapper = new ObjectMapper();
		BufferedReader reader = request.getReader();
		M_LoginDTO request_data = mapper.readValue(reader, M_LoginDTO.class);
		M_LoginDTO response_data = new M_LoginDTO();

		final String agentID = request_data.getAgentID();
		final String jwt = request_data.getJwt();
		response_data.setAgentID(agentID);
		response_data.setJwt(jwt);
				
		AgentAccountLogDTO logDTO = new AgentAccountLogDTO();
		logDTO.setAgentID(agentID);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String time = format.format(date);
		logDTO.setAccessTime(time);
		logDTO.setAccessIP((null != request.getHeader(XHEADER)?request.getHeader(XHEADER):request.getRemoteAddr()));

		if(jws.validation(jwt, agentID, true)) {
			redis.del(agentID+MOBILE);
			response_data.setResult(1);
			logDTO.setLoginStatus("Logout(Mobile)");
		}
		else {
			logDTO.setLoginStatus("Logout Fail(Mobile)");
			response_data.setResult(0);
		}
		
		accountLog.insert(logDTO);
		String returnData = mapper.writeValueAsString(response_data);
		return returnData;
	}

	@RequestMapping(value = "/remoteSignOut", method = RequestMethod.POST)
	public String remoteSignOut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, ClassNotFoundException, SQLException, TimeoutException, InterruptedException {		
		ObjectMapper mapper = new ObjectMapper();
		BufferedReader reader = request.getReader();
		M_RemoteSignOutDTO request_data = mapper.readValue(reader, M_RemoteSignOutDTO.class);
		M_RemoteSignOutDTO response_data = new M_RemoteSignOutDTO();
		
		final String jwt = request_data.getJwt();
		final String agentID = request_data.getAgentID();
		response_data.setAgentID(agentID);
		
		TokenKeyDAOImpl token = TokenKeyDAOImpl.getInstance();
		TokenKeyDTO token_dto = new TokenKeyDTO();
		token_dto.setAgentID(agentID);
		token_dto.setToken(jwt);
		
		AgentAccountLogDTO logDTO = new AgentAccountLogDTO();
		logDTO.setAgentID(agentID);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String time = format.format(date);
		logDTO.setAccessTime(time);
		logDTO.setAccessIP((null != request.getHeader(XHEADER)?request.getHeader(XHEADER):request.getRemoteAddr()));

		if(jws.validation(jwt, agentID, true)) {
			if(msgQ.send(agentID)) {
				token.delete(token_dto);
				response_data.setResult(true);
				logDTO.setLoginStatus("Logout-Remote(Mobile)");
				response_data.setResult(true);
			}
			else {
				response_data.setResult(false);
				logDTO.setLoginStatus("Logout Fail-Remote(Mobile)");
				response_data.setResult(false);
			}
		}
		else {
			logDTO.setLoginStatus("Logout Fail-Remote(Mobile)");
			response_data.setResult(false);
		}
		
		accountLog.insert(logDTO);
		String returnData = mapper.writeValueAsString(response_data);
		return returnData;
	}
	
	@RequestMapping(value = "/loginNumber/create", method = RequestMethod.POST)
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
		
		if(jws.validation(jwt, agentID, true)) {
			number = String.valueOf(CreateLoginNumber.verify_code(key, time));
			redis.setCode(number, agentID);
			response_data.setLoginNumber(number);
			response_data.setResult(1);
		}
		else {
			response_data.setResult(0);
		}
		
		String returnData = mapper.writeValueAsString(response_data);
		return returnData;
	}
	
	@RequestMapping(value = "/accessLog", method = RequestMethod.POST)
	public String accessLog(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, ClassNotFoundException, SQLException {		
		ObjectMapper mapper = new ObjectMapper();
		BufferedReader reader = request.getReader();
		M_AccessLogDTO request_data = mapper.readValue(reader, M_AccessLogDTO.class);
		M_AccessLogDTO response_data = new M_AccessLogDTO();
		AgentAccountLogDTO inputDTO;
		
		final String agentID = request_data.getAgentID();
		response_data.setAgentID(agentID);
		response_data.setJwt("");
		final String jwt = request_data.getJwt();
		inputDTO = new AgentAccountLogDTO();
		inputDTO.setAgentID(agentID);
		
		if(jws.validation(jwt, agentID, true)) {
			response_data.setAccessLogItemArrayList(accountLog.readAll(inputDTO));
			response_data.setResult(true);
		}
		else {
			response_data.setResult(false);
		}

		String returnData = mapper.writeValueAsString(response_data);
		return returnData;
	}
}