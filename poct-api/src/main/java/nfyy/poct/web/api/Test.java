package nfyy.poct.web.api;

import java.util.Base64;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Test {

	public static void main(String[] args) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		System.out.println(encoder.encode("jwtpass"));
		
		System.out.println(new String(Base64.getDecoder().decode("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9")));
		System.out.println(new String(Base64.getDecoder().decode("eyJhdWQiOlsidGVzdGp3dHJlc291cmNlaWQiXSwidXNlcl9uYW1lIjoiam9obi5kb2UiLCJzY29wZSI6WyJyZWFkIiwid3JpdGUiXSwiZXhwIjoxNTI1NzQyMTkxLCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiXSwianRpIjoiYzU0YjE3YzctMDgwMC00OGRkLWE5YTQtMDRjN2I2YjU2NmRiIiwiY2xpZW50X2lkIjoidGVzdGp3dGNsaWVudGlkIn0")));
		
	}
}
