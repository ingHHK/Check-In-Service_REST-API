package proj.checkIN.services;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

	//mobile request : verify_code( UUID, java.time)
	//pc request : checkCode(input_code, UUID)


	public class CreateLoginNumber {
	  public static boolean checkCode(String userCode, String otpkey) {
		  
		  //result : request => UUID is Key, and userCode 
		  
		  
		  
	        long otpnum = Integer.parseInt(userCode);
	        long wave = new Date().getTime();
	        boolean result = false;
	        try {
	            int window = 1000 * 60;
	            for (int i = -window; i <= 1000; i += 1000) {
	                long hash = verify_code(otpkey, wave + i);
	                if (hash == otpnum)
	                    result = true;
	            }

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return result;
	    }
	  
	public static int verify_code(String key, long t) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] data = new byte[8];
        long value = t / 1000;

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
        truncatedHash %= 90000000;
        truncatedHash += 10000000;

        return (int)truncatedHash;
    }

  
}
