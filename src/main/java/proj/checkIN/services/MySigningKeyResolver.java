package proj.checkIN.services;

import java.security.Key;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.SigningKeyResolverAdapter;

public class MySigningKeyResolver extends SigningKeyResolverAdapter {
	private Key key;
	
	MySigningKeyResolver(){
		
	}
	
	void setKey(Key key) {
		this.key = key;
	}
	
	@Override
	public Key resolveSigningKey (JwsHeader jwsHeader, Claims claims) {
		String keyId = jwsHeader.getKeyId();
		
		//Key key = lookupVerificationKey(keyId); --> DB에서 코드 받아서 교체
		
		return key;	
	}

}
