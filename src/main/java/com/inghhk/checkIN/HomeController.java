package com.inghhk.checkIN;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import JSONData.JSONData;
import JSONData.OtpJSONData;

/**
 * Handles requests for the application home page.
 */
@CrossOrigin(origins="*")
@RestController
public class HomeController {	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	
	@RequestMapping(value = "/page", method = RequestMethod.POST)
	public String page(Locale locale, Model model) {
		
		return "page";
	}

	@RequestMapping(value = "/send", method = RequestMethod.POST, consumes="application/json", headers = "content-type=application/x-www-form-urlencoded")
	public String send(HttpServletRequest request) throws IOException, ServletException {		
		ObjectMapper mapper = new ObjectMapper();
		BufferedReader reader = request.getReader();
		JSONData yourData = mapper.readValue(reader, JSONData.class);
		
		JSONData myData = new JSONData();
		String myid = "Hyun Ho";
		String mypwd = "987987";
		myData.setId(myid);
		myData.setPwd(mypwd);
		
		ArrayList<JSONData> list = new ArrayList<JSONData>();
		list.add(myData);
		list.add(yourData);
		String returnData = mapper.writeValueAsString(list);

		return returnData;
	}
	
	@RequestMapping(value = "/otp", method = RequestMethod.POST, consumes="application/json", headers = "content-type=application/x-www-form-urlencoded")
	public String otp(HttpServletRequest request) throws IOException, ServletException {		
		ObjectMapper mapper = new ObjectMapper();
		BufferedReader reader = request.getReader();
		OtpJSONData yourData = mapper.readValue(reader, OtpJSONData.class);
		
		String otpCode = yourData.getOtpCode();
		String otpKey = yourData.getOtpKey();
		String result;
		
		if(MainClass.checkCode(otpCode, otpKey)) {
			result = "suck sex";
		}
		else {
			result = "fail";
		}
		
		String returnData = mapper.writeValueAsString(result);

		return returnData;
	}
}