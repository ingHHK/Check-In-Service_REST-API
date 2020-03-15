package com.inghhk.checkIN;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class MainClass {
	
	private static String otpkey = "[B@3429208";
	
	public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException {
		
		//otpkey 생성 -> 일련번호로 대체가능
		HashMap<String, String> map = generate("userName", "hostName");
		//String otpkey = map.get("encodedKey");
		
		
		System.out.println("otpkey:" + otpkey);
		
		//클라리언트에서 otp 생성하는 코드
		int otp6 = verify_code(otpkey, new Date().getTime()/30000);
		System.out.println("client OTP:" + otp6);
		
		//서버에서 검증
		boolean check = checkCode(Integer.toString(otp6), otpkey);
		System.out.println(check);
	}

	//서버에서 코드가 맞는지 검사한다. (OTP앱 6자리, otp키)
	public static boolean checkCode(String userCode, String otpkey) {
		long otpnum = Integer.parseInt(userCode);
		long wave = new Date().getTime() / 30000;
		boolean result = false;
		try {
			//otp키를 decode
			//Decoder decoder = Base64.getDecoder();
			//byte[] decodedKey = decoder.decode(otpkey);
			//System.out.println("decoded Key : " + decodedKey);
			
			int window = 3;
			for (int i = -window; i <= window; i++) {
				long hash = verify_code(otpkey, wave + i);
				System.out.println(hash + " " + otpnum);
				if (hash == otpnum)
					result = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	//6자리 코드를 생성한다. 입력값으로는 otp키값과 시간값
	public static int verify_code(String key, long t) throws NoSuchAlgorithmException, InvalidKeyException {
		byte[] data = new byte[8];
		long value = t;
		
		for (int i = 8; i-- > 0; value >>>= 8) {
			data[i] = (byte) value;
		}
		
		SecretKey signKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(signKey);
		byte[] hash = mac.doFinal(data);

		int offset = hash[20 - 1] & 0xF;

		long truncatedHash = 0;
		for (int i = 0; i < 4; i++) {
			truncatedHash <<= 8;
			truncatedHash |= (hash[offset + i] & 0xFF);
		}

		truncatedHash &= 0x7FFFFFFF;
		truncatedHash %= 1000000;

		return (int) truncatedHash;
	}
	
	// 고유키값을 생성하는 method이다. 이름 두개를 받아서 비밀키를 생성하는원리
		public static HashMap<String, String> generate(String userName, String hostName) {
			HashMap<String, String> map = new HashMap<String, String>();
			
			//버퍼에 할당, 버퍼를 난수로 셋팅
			byte[] buffer = new byte[5 + 5 * 5];
			new Random().nextBytes(buffer);
			
			//버퍼의 일부분을 가져와 비밀키로 채택
			Encoder encoder = Base64.getEncoder();
			Decoder decoder = Base64.getDecoder();
			byte[] secretKey = Arrays.copyOf(buffer, 10);
			System.out.println("secretKey : " + secretKey);
			byte[] bEncodedKey = encoder.encode(secretKey);
			System.out.println("EncodedKey : " + bEncodedKey);
			
			System.out.println("Encoded -> secretKey : " + decoder.decode(bEncodedKey));
			
			//생성된 key
			String encodedKey = new String(bEncodedKey);

			map.put("encodedKey", encodedKey);

			return map;
		}
}
