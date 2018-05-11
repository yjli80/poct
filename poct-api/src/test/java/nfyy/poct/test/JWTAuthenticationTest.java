package nfyy.poct.test;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import nfyy.poct.Application;
import nfyy.poct.domain.Role;
import nfyy.poct.domain.RoleRepository;
import nfyy.poct.domain.User;
import nfyy.poct.domain.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(
		classes = {Application.class},
		webEnvironment = WebEnvironment.RANDOM_PORT)
public class JWTAuthenticationTest {

	@Autowired
	private TokenStore tokenStore;
	
	@LocalServerPort
    private int port;
	
	@Value("${security.jwt.client-id}")
	private String clientId;

	@Value("${security.jwt.client-secret}")
	private String clientSecret;

	@Value("${security.jwt.grant-type}")
	private String grantType;
	
	@Autowired private UserRepository users;
	@Autowired private RoleRepository roles;
	@Autowired private PasswordEncoder encoder;
	
	public static void main(String[] args) {
		System.out.println(new String(Base64.getDecoder().decode("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9")));
		System.out.println(new String(Base64.getDecoder().decode("eyJhdWQiOlsiUE9DVC1BUEktTkZZWS0yMDE4Il0sInVzZXJfbmFtZSI6InRlc3QiLCJzY29wZSI6WyJyZWFkIiwid3JpdGUiXSwiZXhwIjoxNTI2MDI0NzA1LCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiXSwianRpIjoiMjI0NDRhYTYtZjc2ZS00Y2RmLWIwZGItZWQyMTg3ZTEyODQ1IiwiY2xpZW50X2lkIjoiUE9DVC1ORllZLTIwMTgifQ")));
	}
	
	@Before
	public void init() {
		User u = users.findByUsername("test");
		if (u == null) {
			
			Role role = roles.findOne("ROLE_USER");
			
			if (role == null) {
				role = new Role("ROLE_USER", "role user");
				roles.save(role);
			}
			
			User user = new User();
			user.addRole(role);
			user.setUsername("test");
			user.setName("test");
			user.setPassword(encoder.encode("test"));
			user.setPasswordRaw(false);
			users.save(user);
		}
	}
	
	@Test
	public void whenTokenDoesNotContainIssuer_thenSuccess() throws Exception {
		String tokenValue = obtainAccessToken("test", "test");
		//String tokenValue = obtainToken("test1", "test1");
		
		System.out.println("token: " + tokenValue);
		
		OAuth2Authentication auth = tokenStore.readAuthentication(tokenValue);
		
		Assert.assertEquals(auth.getName(), "test");
		Assert.assertEquals(auth.getPrincipal(), "test");
	}
	
	//curl clientId:clientSecret@localhost:port/oauth/token -d grant_type=password -d username=test -d password=test
	protected String obtainToken(String username, String password) throws Exception {
		
		String auth = clientId + ":" + clientSecret;
		byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
		String authHeader = "Basic " + new String( encodedAuth );
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", authHeader);
		
		CredentialsProvider provider = new BasicCredentialsProvider();
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(clientId, clientSecret);
		provider.setCredentials(AuthScope.ANY, credentials);
		
		HttpClient client = HttpClientBuilder.create()
				.setDefaultCredentialsProvider(provider)
				.build();
		
		HttpPost post = new HttpPost("http://localhost:" + port + "/oauth/token");
		
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("grant_type", grantType));
		nvps.add(new BasicNameValuePair("username", username));
		nvps.add(new BasicNameValuePair("password", password));

		post.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
		
		HttpResponse response = client.execute(post);
		HttpEntity entity = response.getEntity();
		
		String body = EntityUtils.toString(entity, "UTF-8");
		EntityUtils.consume(entity);
		
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readTree(body).path("access_token").textValue();
	}

	private String obtainAccessToken(String username, String password) {
		Map<String, String> params = new HashMap<>();
		params.put("grant_type", grantType);
		params.put("username", username);
		params.put("password", password);
		Response response = RestAssured.given().auth().preemptive().basic(clientId, clientSecret).and().with()
				.params(params).when().post("http://localhost:" + port + "/oauth/token");
		return response.jsonPath().getString("access_token");
	}
}
