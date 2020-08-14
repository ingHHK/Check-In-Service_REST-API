package proj.checkIN.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import proj.checkIN.clientDTO.P_LoginDTO;
import proj.checkIN.clientDTO.M_AccessLogDTO;
import proj.checkIN.clientDTO.M_LoginDTO;
import proj.checkIN.clientDTO.M_LoginNumberDTO;
import proj.checkIN.clientDTO.M_RemoteSignOutDTO;
import proj.checkIN.clientDTO.P_UpdateAccountDTO;
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
	@Autowired
	ObjectMapper mapper;
	
	private static String MOBILE = "_M";
	private final static String XHEADER = "X-FORWARDED-FOR";

	@RequestMapping(value = "/signUp/verifyEmail", method = RequestMethod.POST)
	public Object verifyEmail(HttpServletRequest request) throws IOException, ServletException {		
		BufferedReader reader = request.getReader();
		AgentAccountDTO request_data = mapper.readValue(reader, AgentAccountDTO.class);
		AgentAccountDTO response_data = new AgentAccountDTO();
		final String agentID = request_data.getAgentID();
		
		
		if(email.isDuplicate(agentID)) {						//check if e-mail is exist in DB
			response_data.setResult(false);
		} else {
			String verify_code = email.signUpEmail(agentID);	//if not, e-mailing verify code
			response_data.setResult(true);
			response_data.setVerify_code(verify_code);
		}
		
		return response_data;
	}
	
	@RequestMapping(value = "/signUp/signAccount", method = RequestMethod.POST)
	public Object signAccount(HttpServletRequest request) throws IOException, ServletException, ClassNotFoundException, SQLException {		
		BufferedReader reader = request.getReader();
		AgentAccountDTO request_data = mapper.readValue(reader, AgentAccountDTO.class);
		AgentAccountDTO response_data = new AgentAccountDTO();
		
		
		if(account.insert(request_data) == 1) {		//insert the account information into DB
			response_data.setResult(true);
		} else {
			response_data.setResult(false);
		}
				
		return response_data;
	}
	
	@RequestMapping(value = "/signIn", method = { RequestMethod.GET,RequestMethod.POST})
	public Object signIn(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, ClassNotFoundException, SQLException, TimeoutException, InterruptedException {		
		BufferedReader reader = request.getReader();
		P_LoginDTO request_data = mapper.readValue(reader, P_LoginDTO.class);
		P_LoginDTO response_data = new P_LoginDTO();
		final String agentID = request_data.getAgentID();
		final String agentPW = request_data.getAgentPW();
		
		AgentAccountLogDTO logDTO = new AgentAccountLogDTO();		//access log DTO
		logDTO.setAgentID(agentID);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String time = format.format(date);
		logDTO.setAccessTime(time);
		logDTO.setAccessIP((null != request.getHeader(XHEADER)?request.getHeader(XHEADER):request.getRemoteAddr()));
		
		TokenKeyDTO token = new TokenKeyDTO();
		token.setAgentID(agentID);
		token.setToken(request_data.getJwt());
		AgentAccountDTO db_arg = new AgentAccountDTO();
		db_arg.setAgentID(agentID);
		AgentAccountDTO db_info = account.read(db_arg);
		
		
		if(agentPW.equals(db_info.getAgentPW())) {		//compare the request password with password in DB
			response_data.setResult(true);
			if(token.getToken() != null) {				//remove the remaining token in DB
				tokenKey.delete(token);
			}
			if(db_info.getOtpEnable() == 0) {
				String jwt = jws.create(agentID, false);//create new JSON Web Token(JWT) for login, save the key in DB
				response_data.setJwt(jwt);
				msgQ.connect(agentID);					//declare and bind the message queue which for the remote logout
				logDTO.setLoginStatus("Login(PC)");
			}
			else {
				logDTO.setLoginStatus("Login Wait-OTP(PC)");
			}
			response_data.setResult(true);
		} else {
			response_data.setResult(false);
			logDTO.setLoginStatus("Login Fail(PC)");
		}
		
		response_data.setName(db_info.getName());
		response_data.setOtpEnable(db_info.getOtpEnable());
		response_data.setAgentID(agentID);
		accountLog.insert(logDTO);						//update access log
		return response_data;
	}
	
	private P_LoginDTO signIn(String agentID) throws IOException, ServletException, ClassNotFoundException, SQLException, TimeoutException, InterruptedException {		
		P_LoginDTO response_data = new P_LoginDTO();
				
		TokenKeyDTO token = new TokenKeyDTO();
		token.setAgentID(agentID);
		AgentAccountDTO db_arg = new AgentAccountDTO();
		db_arg.setAgentID(agentID);
		
		
		if(token.getToken() != null) {		
			tokenKey.delete(token);
		}
		
		String jwt = jws.create(agentID, false);	
		
		msgQ.connect(agentID);
		response_data.setJwt(jwt);
		response_data.setAgentID(agentID);
		return response_data;
	}
	
	@RequestMapping(value = "/signOut", method = RequestMethod.POST)
	public Object signOut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, ClassNotFoundException, SQLException {		
		BufferedReader reader = request.getReader();
		P_LoginDTO request_data = mapper.readValue(reader, P_LoginDTO.class);
		P_LoginDTO response_data = new P_LoginDTO();
		final String agentID = request_data.getAgentID();
		final String jwt = request_data.getJwt();
		
		AgentAccountLogDTO logDTO = new AgentAccountLogDTO();
		logDTO.setAgentID(agentID);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String time = format.format(date);
		logDTO.setAccessTime(time);
		logDTO.setAccessIP((null != request.getHeader(XHEADER)?request.getHeader(XHEADER):request.getRemoteAddr()));
		
		TokenKeyDAOImpl token = TokenKeyDAOImpl.getInstance();
		TokenKeyDTO token_dto = new TokenKeyDTO();
		token_dto.setAgentID(agentID);
		
		
		if(jws.validation(jwt, agentID, false)) {		//validating JSON Web Token
			token.delete(token_dto);
			response_data.setResult(true);
			logDTO.setLoginStatus("Logout(PC)");
		}
		else {
			logDTO.setLoginStatus("Logout Fail(PC)");
			response_data.setResult(false);
		}
		
		response_data.setAgentID(agentID);
		response_data.setJwt(jwt);
		accountLog.insert(logDTO);
		return response_data;
	}
	
	@RequestMapping(value = "/update/accountName", method = RequestMethod.POST)
	public Object update_accountName(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, ClassNotFoundException, SQLException {		
		BufferedReader reader = request.getReader();
		P_UpdateAccountDTO request_data = mapper.readValue(reader, P_UpdateAccountDTO.class);
		P_UpdateAccountDTO response_data = new P_UpdateAccountDTO();
		final String agentID = request_data.getAgentID();
		final String jwt = request_data.getJwt();
		final String accountName = request_data.getAccountName();
		
		AgentAccountDTO accountDTO = new AgentAccountDTO();
		accountDTO.setAgentID(agentID);
		accountDTO.setName(accountName);
		
		
		if(jws.validation(jwt, agentID, false)) {
			account.updateAccountName(accountDTO);
			response_data.setResult(true);
		}
		else {
			response_data.setResult(false);
		}
		
		return response_data;
	}
	
	@RequestMapping(value = "/update/deviceEnable", method = RequestMethod.POST)
	public Object update_deviceEnable(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, ClassNotFoundException, SQLException {		
		BufferedReader reader = request.getReader();
		P_UpdateAccountDTO request_data = mapper.readValue(reader, P_UpdateAccountDTO.class);
		P_UpdateAccountDTO response_data = new P_UpdateAccountDTO();
		final String agentID = request_data.getAgentID();
		final String deviceID = request_data.getDeviceID();
		final String jwt = request_data.getJwt();
		
		RemoteDeviceDTO remoteDeviceDTO = new RemoteDeviceDTO();
		remoteDeviceDTO.setAgentID(agentID);
		remoteDeviceDTO.setDeviceID(deviceID);
		remoteDeviceDTO.setDeviceEnable(request_data.isDeviceEnable());

		
		if(jws.validation(jwt, agentID, false)) {
			remoteDevice.updateDeviceEnable(remoteDeviceDTO);
			response_data.setResult(true);
		}
		else {
			response_data.setResult(false);
		}
		
		return response_data;
	}
	
	@RequestMapping(value = "/update/otpEnable", method = RequestMethod.POST)
	public Object update_OTPEnable(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, ClassNotFoundException, SQLException {		
		BufferedReader reader = request.getReader();
		P_UpdateAccountDTO request_data = mapper.readValue(reader, P_UpdateAccountDTO.class);
		P_UpdateAccountDTO response_data = new P_UpdateAccountDTO();
		final String agentID = request_data.getAgentID();
		final int otpEnable = request_data.getOtpEnable();
		final String jwt = request_data.getJwt();
	
		AgentAccountDTO accountDTO = new AgentAccountDTO();
		accountDTO.setAgentID(agentID);
		accountDTO.setOtpEnable(otpEnable);
		
		
		if(jws.validation(jwt, agentID, false)) {
			account.updateOTPEnable(accountDTO);
			response_data.setResult(true);
		}
		else {
			response_data.setResult(false);
		}
		
		return response_data;
	}
	
	@RequestMapping(value = "/deviceRead", method = RequestMethod.POST)
	public Object deviceRead(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, ClassNotFoundException, SQLException {		
		BufferedReader reader = request.getReader();
		AgentAccountDTO request_data = mapper.readValue(reader, AgentAccountDTO.class);
		final String agentID = request_data.getAgentID();
		final String jwt = request_data.getJwt();
	
		RemoteDeviceDTO remoteDeviceDTO = new RemoteDeviceDTO();
		remoteDeviceDTO.setAgentID(agentID);
		
		List<RemoteDeviceDTO> devices = new ArrayList<>();
		
		
		if(jws.validation(jwt, agentID, false))
			devices = remoteDevice.readAll(remoteDeviceDTO);
		
		return devices;
	}
	
	@RequestMapping(value = "/siteAdd", method = RequestMethod.POST)
	public Object siteAdd(HttpServletRequest request) throws IOException, ServletException, ClassNotFoundException, SQLException {		
		BufferedReader reader = request.getReader();
		UserSiteInformationDTO request_data = mapper.readValue(reader, UserSiteInformationDTO.class);
		UserSiteInformationDTO response_data = new UserSiteInformationDTO();
		final String jwt = request_data.getJwt();
		final String agentID = request_data.getAgentID();
		
		
		if(jws.validation(jwt, agentID, false)) {
			siteInfo.insert(request_data);
			response_data.setResult(true);
		}
		else {
			response_data.setResult(false);
		}
		
		return response_data;
	}
	
	@RequestMapping(value = "/siteEdit", method = RequestMethod.POST)
	public Object siteEdit(HttpServletRequest request) throws IOException, ServletException, ClassNotFoundException, SQLException {		
		BufferedReader reader = request.getReader();
		UserSiteInformationDTO request_data = mapper.readValue(reader, UserSiteInformationDTO.class);
		UserSiteInformationDTO response_data = new UserSiteInformationDTO();
		final String jwt = request_data.getJwt();
		final String agentID = request_data.getAgentID();
		

		if(jws.validation(jwt, agentID, false)) {
			siteInfo.update(request_data);
			response_data.setResult(true);
		}
		else {
			response_data.setResult(false);
		}
		
		return response_data;
	}
	
	@RequestMapping(value = "/siteDelete", method = RequestMethod.POST)
	public Object siteDelete(HttpServletRequest request) throws IOException, ServletException, ClassNotFoundException, SQLException {		
		BufferedReader reader = request.getReader();
		UserSiteInformationDTO request_data = mapper.readValue(reader, UserSiteInformationDTO.class);
		UserSiteInformationDTO response_data = new UserSiteInformationDTO();
		final String jwt = request_data.getJwt();
		final String agentID = request_data.getAgentID();
		
		
		if(jws.validation(jwt, agentID, false)) {
			siteInfo.delete(request_data);
			response_data.setResult(true);
		}
		else {
			response_data.setResult(false);
		}
		
		return response_data;
	}
	
	@RequestMapping(value = "/siteRead", method = RequestMethod.POST)
	public Object siteRead(HttpServletRequest request) throws IOException, ServletException, ClassNotFoundException, SQLException {		
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
		
		return response_data;
	}
	
	@RequestMapping(value = "/loginNumber/verify", method = RequestMethod.POST)
	public Object loginNumber_verify(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, ClassNotFoundException, SQLException, InvalidKeyException, NoSuchAlgorithmException, TimeoutException, InterruptedException {		
		BufferedReader reader = request.getReader();
		AgentAccountDTO request_data = mapper.readValue(reader, AgentAccountDTO.class);
		P_LoginDTO response_data = new P_LoginDTO();
		final String verify_code = request_data.getVerify_code();
		final String agentID = redis.getCode(verify_code);		//get agentID from the Redis which key is login number that create from mobile application 
		
		AgentAccountLogDTO logDTO = new AgentAccountLogDTO();
		logDTO.setAgentID(agentID);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String time = format.format(date);
		logDTO.setAccessTime(time);
		logDTO.setAccessIP((null != request.getHeader(XHEADER)?request.getHeader(XHEADER):request.getRemoteAddr()));
		
		
		if(agentID != null) {									//if agentID is null, there has no matching key(login number) in the Redis 
			response_data = signIn(agentID);
			logDTO.setLoginStatus("Login-One Time Login(PC)");
			response_data.setResult(true);
		}
		else {
			logDTO.setLoginStatus("Login Fail-One Time Login(PC)");
			response_data.setResult(false);
		}
		
		accountLog.insert(logDTO);
		return response_data;
	}
	
	@RequestMapping(value = "/verifyCode", method = RequestMethod.POST)
	public Object verifyCode(HttpServletRequest request) throws IOException, ServletException {		
		BufferedReader reader = request.getReader();
		AgentAccountDTO request_data = mapper.readValue(reader, AgentAccountDTO.class);
		AgentAccountDTO response_data = new AgentAccountDTO();
		final String agentID = request_data.getAgentID();
		final String jwt = request_data.getJwt();
		
		
		if(jws.validation(jwt, agentID, false)) {
			String verify_code = email.verifyCodeEmail(agentID);		//generate and e-mailing a code, return value is the generated code
			response_data.setVerify_code(verify_code);
			response_data.setResult(true);
		} 
		else {
			response_data.setResult(false);
		}
		
		return response_data;
	}
	
	@RequestMapping(value = "/verifyOTP", method = RequestMethod.POST)
	public Object verifyOTP(HttpServletRequest request) throws IOException, ServletException, ClassNotFoundException, SQLException, TimeoutException, InterruptedException {		
		BufferedReader reader = request.getReader();
		AgentAccountDTO request_data = mapper.readValue(reader, AgentAccountDTO.class);
		P_LoginDTO response_data = new P_LoginDTO();
		final String agentID = request_data.getAgentID();
		final String verify_code = request_data.getVerify_code();
		
		AgentAccountLogDTO logDTO = new AgentAccountLogDTO();
		logDTO.setAgentID(agentID);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String time = format.format(date);
		logDTO.setAccessTime(time);
		logDTO.setAccessIP((null != request.getHeader(XHEADER)?request.getHeader(XHEADER):request.getRemoteAddr()));
		
		boolean verifying_result = false;
		
		RemoteDeviceDTO remoteDeviceDTO = new RemoteDeviceDTO();
		remoteDeviceDTO.setAgentID(agentID);
		
		List<RemoteDeviceDTO> remoteDevices = new ArrayList<RemoteDeviceDTO>(); 
		remoteDevices = remoteDevice.readAll(remoteDeviceDTO);	//read all the devices enrollment by the agentID from DB
		
		
		for(int i = 0; i < remoteDevices.size(); ++i) {			//try to put all deviceID and find matched device
			if(CreateOTP.checkCode(verify_code, remoteDevices.get(i).getDeviceID(), date.getTime()))
				verifying_result = true;
		}
		
		if(verifying_result) {
			response_data = signIn(agentID);					//if verifying result is true, sign-in to the service 
			logDTO.setLoginStatus("Login-OTP(PC)");
			response_data.setResult(true);
		} else {
			logDTO.setLoginStatus("Login Fail-OTP(PC)");
			response_data.setResult(false);
		}
		
		accountLog.insert(logDTO);
		return response_data;
	}
	

	//////Mobile Application API//////
	@RequestMapping(value = "/signIn_M", method = { RequestMethod.GET,RequestMethod.POST})
	public Object signIn_M(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, ClassNotFoundException, SQLException {		
		BufferedReader reader = request.getReader();
		M_LoginDTO request_data = mapper.readValue(reader, M_LoginDTO.class);
		M_LoginDTO response_data = new M_LoginDTO();
		final String agentID = request_data.getAgentID();
		final String agentPW = request_data.getAgentPW();
		final String deviceID = request_data.getDeviceID();
		final String deviceName = request_data.getDeviceName();
		final String jwt = request_data.getJwt();
		
		AgentAccountLogDTO logDTO = new AgentAccountLogDTO();
		logDTO.setAgentID(agentID);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String time = format.format(date);
		logDTO.setAccessTime(time);
		logDTO.setAccessIP((null != request.getHeader(XHEADER)?request.getHeader(XHEADER):request.getRemoteAddr()));
		
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
		
		

		if(agentPW.equals(db_info.getAgentPW())) {
			if(db_deviceDTO == null) {							//if there has no device information in DB, enroll it
				deviceDTO.setEnrollmentDate(time);
				deviceDTO.setDeviceName(deviceName);
				remoteDevice.insert(deviceDTO);
				db_deviceDTO = remoteDevice.read(deviceDTO);
			}
			else if (db_deviceDTO.getDeviceName() != request_data.getDeviceName()) {	//if deviceName is not matched, update it
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
				String new_jwt = jws.create(agentID, true);		//create the JWT for login, save the key in the Redis
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
		
		response_data.setAgentID(agentID);
		accountLog.insert(logDTO);
		return response_data;
	}

	@RequestMapping(value = "/signOut_M", method = RequestMethod.POST)
	public Object signOut_M(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, ClassNotFoundException, SQLException {		
		BufferedReader reader = request.getReader();
		M_LoginDTO request_data = mapper.readValue(reader, M_LoginDTO.class);
		M_LoginDTO response_data = new M_LoginDTO();
		final String agentID = request_data.getAgentID();
		final String jwt = request_data.getJwt();
	
		AgentAccountLogDTO logDTO = new AgentAccountLogDTO();
		logDTO.setAgentID(agentID);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String time = format.format(date);
		logDTO.setAccessTime(time);
		logDTO.setAccessIP((null != request.getHeader(XHEADER)?request.getHeader(XHEADER):request.getRemoteAddr()));

		
		if(jws.validation(jwt, agentID, true)) {
			redis.del(agentID+MOBILE);		//remove the JWT key from the Redis
			response_data.setResult(1);
			logDTO.setLoginStatus("Logout(Mobile)");
		}
		else {
			logDTO.setLoginStatus("Logout Fail(Mobile)");
			response_data.setResult(0);
		}
		
		response_data.setAgentID(agentID);
		response_data.setJwt(jwt);
		accountLog.insert(logDTO);
		return response_data;
	}

	@RequestMapping(value = "/remoteSignOut", method = RequestMethod.POST)
	public Object remoteSignOut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, ClassNotFoundException, SQLException, TimeoutException, InterruptedException {		
		BufferedReader reader = request.getReader();
		M_RemoteSignOutDTO request_data = mapper.readValue(reader, M_RemoteSignOutDTO.class);
		M_RemoteSignOutDTO response_data = new M_RemoteSignOutDTO();
		final String jwt = request_data.getJwt();
		final String agentID = request_data.getAgentID();
		
		AgentAccountLogDTO logDTO = new AgentAccountLogDTO();
		logDTO.setAgentID(agentID);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String time = format.format(date);
		logDTO.setAccessTime(time);
		logDTO.setAccessIP((null != request.getHeader(XHEADER)?request.getHeader(XHEADER):request.getRemoteAddr()));
		
		TokenKeyDAOImpl token = TokenKeyDAOImpl.getInstance();
		TokenKeyDTO token_dto = new TokenKeyDTO();
		token_dto.setAgentID(agentID);
		token_dto.setToken(jwt);
		

		if(jws.validation(jwt, agentID, true)) {
			if(msgQ.send(agentID)) {		//put "remote sign out" message on the queue 
				token.delete(token_dto);	//if success, remove the JWT key from the DB
				logDTO.setLoginStatus("Logout-Remote(Mobile)");
				response_data.setResult(true);
			}
			else {
				logDTO.setLoginStatus("Logout Fail-Remote(Mobile)");
				response_data.setResult(false);
			}
		}
		else {
			logDTO.setLoginStatus("Logout Fail-Remote(Mobile)");
			response_data.setResult(false);
		}
		
		response_data.setAgentID(agentID);
		accountLog.insert(logDTO);
		return response_data;
	}
	
	@RequestMapping(value = "/loginNumber/create", method = RequestMethod.POST)
	public Object loginNumber_create(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, ClassNotFoundException, SQLException, InvalidKeyException, NoSuchAlgorithmException {		
		BufferedReader reader = request.getReader();
		M_LoginNumberDTO request_data = mapper.readValue(reader, M_LoginNumberDTO.class);
		M_LoginNumberDTO response_data = new M_LoginNumberDTO();
		final String agentID = request_data.getAgentID();
		final String jwt = request_data.getJwt();
		
		String key = UUID.randomUUID().toString();
		long time = new Date().getTime();
		String number;
		
		
		if(jws.validation(jwt, agentID, true)) {
			number = String.valueOf(CreateLoginNumber.verify_code(key, time));	//create the login number
			redis.setCode(number, agentID);										//set login number on the Redis
			response_data.setLoginNumber(number);		
			response_data.setResult(1);
		}
		else {
			response_data.setResult(0);
		}
		
		response_data.setAgentID(agentID);
		response_data.setJwt(jwt);
		return response_data;
	}
	
	@RequestMapping(value = "/accessLog", method = RequestMethod.POST)
	public Object accessLog(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, ClassNotFoundException, SQLException {		
		BufferedReader reader = request.getReader();
		M_AccessLogDTO request_data = mapper.readValue(reader, M_AccessLogDTO.class);
		M_AccessLogDTO response_data = new M_AccessLogDTO();
		final String agentID = request_data.getAgentID();
		final String jwt = request_data.getJwt();
		
		AgentAccountLogDTO inputDTO = new AgentAccountLogDTO();
		inputDTO.setAgentID(agentID);
		
		
		if(jws.validation(jwt, agentID, true)) {
			response_data.setAccessLogItemArrayList(accountLog.readAll(inputDTO));
			response_data.setResult(true);
		}
		else {
			response_data.setResult(false);
		}
		
		response_data.setAgentID(agentID);
		response_data.setJwt("");
		return response_data;
	}
}