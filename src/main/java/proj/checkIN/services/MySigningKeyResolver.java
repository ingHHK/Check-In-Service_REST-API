package proj.checkIN.services;

import java.sql.SQLException;
import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.SigningKeyResolverAdapter;
import proj.checkIN.DB.TokenKeyDAOImpl;
import proj.checkIN.DB.TokenKeyDTO;

public class MySigningKeyResolver extends SigningKeyResolverAdapter {
	private static String MOBILE = "_M";
	SecretKey key;
	private static String stringKey;
	private String agentID;
	private TokenKeyDTO dto;
	MySigningKeyResolver(TokenKeyDTO dto, boolean mobile_status){
		if(mobile_status) {
			RedisService redis = new RedisService();
			agentID = dto.getAgentID() + MOBILE;
			stringKey = redis.getToken(agentID);
		}
		else {
			TokenKeyDTO db = new TokenKeyDTO();
			TokenKeyDAOImpl token = TokenKeyDAOImpl.getInstance();
			try {
				db = token.read(dto);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			stringKey = db.getToken();
		}
	}
	
	@Override
	public SecretKey resolveSigningKey (JwsHeader jwsHeader, Claims claims) {
		byte[] decodedKey = Base64.getDecoder().decode(stringKey);
		key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");
		
		return key;
   	}
}