package nfyy.poct.test;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;

public class JWTAuthenticationTest extends TestWebConfigration {

	@Autowired
	private TokenStore tokenStore;
	
	@Test
	public void whenTokenDoesNotContainIssuer_thenSuccess() throws Exception {
		String tokenValue = obtainAccessToken();
		
		System.out.println("token: " + tokenValue);
		
		OAuth2Authentication auth = tokenStore.readAuthentication(tokenValue);
		
		Assert.assertEquals(auth.getName(), "test");
		Assert.assertEquals(auth.getPrincipal(), "test");
	}
}
