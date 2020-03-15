package proj.checkIN.services;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SigningKeyResolver;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTServiceImpl implements JWTService{
	private Key key;
	public Key getKey() {
		return this.key;
	}
	
	public String create(String agentID) {
		String keyId = agentID;
		key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
		Date date = new Date(System.currentTimeMillis() + 600000); //1시간
		String jws = Jwts.builder()
				.setHeaderParam(JwsHeader.KEY_ID, keyId)
				.setExpiration(date)
				.setSubject("check-in_token")
				.signWith(key)
				.compact();
		return jws;
	}
	
	public boolean validation(String jwsString) {
		Jws<Claims> jws;
		try {
			SigningKeyResolver signingKeyResolver = new MySigningKeyResolver();
			((MySigningKeyResolver) signingKeyResolver).setKey(key);
			jws = Jwts.parserBuilder()
			.setSigningKeyResolver(signingKeyResolver)
			.build()
			.parseClaimsJws(jwsString);
			return true;
		} catch (JwtException ex) {
			return false;
		}	
	}
}
