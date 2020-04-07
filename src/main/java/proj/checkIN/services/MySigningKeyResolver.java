package proj.checkIN.services;

import java.sql.SQLException;
import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.check_in.dto.TokenKeyDTO;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.SigningKeyResolverAdapter;
import proj.checkIN.DB.TokenKeyDAOImpl;

public class MySigningKeyResolver extends SigningKeyResolverAdapter {
	private TokenKeyDTO dto;
	SecretKey key;
//	@Autowired
//	TokenKeyDAOImpl token;
	
	MySigningKeyResolver(TokenKeyDTO dto){
		this.dto = dto;
	}
	
	@Override
	public SecretKey resolveSigningKey (JwsHeader jwsHeader, Claims claims) {
		TokenKeyDTO db = new TokenKeyDTO();
		try {
			TokenKeyDAOImpl token = TokenKeyDAOImpl.getInstance();
			db = token.read(dto);
			String  stringKey = db.getToken();
			byte[] decodedKey = Base64.getDecoder().decode(stringKey);
			key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");
			
			return key;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
   	}
}
