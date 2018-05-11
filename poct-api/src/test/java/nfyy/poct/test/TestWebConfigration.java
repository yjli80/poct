package nfyy.poct.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

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
public class TestWebConfigration {
	
	@Value("${security.jwt.client-id}")
	private String clientId;

	@Value("${security.jwt.client-secret}")
	private String clientSecret;

	@Value("${security.jwt.grant-type}")
	private String grantType;
	
	@LocalServerPort
	protected int port;
	
	@Autowired private UserRepository users;
	@Autowired private RoleRepository roles;
	@Autowired private PasswordEncoder encoder;
	
	protected String host = "http://localhost";
	
	private String username = "test";
	private String password = "test";
	
	private String token;
	protected RestTemplate rest; 
	
	@Before
	public void init() {
		
		User u = users.findByUsername(username);
		if (u == null) {
			
			Role role = roles.findOne("ROLE_USER");
			
			if (role == null) {
				role = new Role("ROLE_USER", "role user");
				roles.save(role);
			}
			
			User user = new User();
			user.addRole(role);
			user.setUsername(username);
			user.setName(username);
			user.setPassword(encoder.encode(password));
			user.setPasswordRaw(false);
			users.save(user);
		}
		
		rest = new RestTemplate();
		token = obtainAccessToken(username, password, getUrl("/oauth/token"));
	}
	
	public String getUrl(String uri) {
		return host + ":" + port + uri;
	}
	
	public String obtainAccessToken() {
		return obtainAccessToken(username, password, getUrl("/oauth/token"));
	}
	
	private String obtainAccessToken(String username, String password, String url) {
		Map<String, String> params = new HashMap<>();
		params.put("grant_type", grantType);
		params.put("username", username);
		params.put("password", password);
		Response response = RestAssured.given().auth().preemptive().basic(clientId, clientSecret).and().with()
				.params(params).when().post(url);
		return response.jsonPath().getString("access_token");
	}
	
	/*
	private String obtainAccessTokenWithHttpClient(String username, String password, String url) throws Exception {
		
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
		
		HttpPost post = new HttpPost(getUrl("/oauth/token"));
		
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
	*/
	
	public String getToken() {
		return token;
	}
	
	public HttpHeaders getHttpHeadersWithToken() {
		return toHttpHeaders(token);
	}
	
	public static HttpHeaders toHttpHeaders(String token) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
		return headers;
	}
	
	public static void addJsonHeader(HttpHeaders headers) {
		headers.add(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
	}
}
