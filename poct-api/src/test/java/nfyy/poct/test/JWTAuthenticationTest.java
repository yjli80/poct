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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import nfyy.poct.Application;

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

	@Test
	public void whenTokenDoesNotContainIssuer_thenSuccess() throws Exception {
		String tokenValue = obtainAccessToken("test", "test");
		//String tokenValue = obtainToken("test", "test");
		
		OAuth2Authentication auth = tokenStore.readAuthentication(tokenValue);
		
		Assert.assertEquals(auth.getName(), "test");
		Assert.assertEquals(auth.getPrincipal(), "test");
	}
	
	//curl clientId:clientSecret@localhost:port/oauth/token -d grant_type=password -d username=test -d password=test
	private String obtainToken(String username, String password) throws Exception {
		
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
		nvps.add(new BasicNameValuePair("client_id", clientId));
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
		params.put("client_id", clientId);
		params.put("username", username);
		params.put("password", password);
		Response response = RestAssured.given().auth().preemptive().basic(clientId, clientSecret).and().with()
				.params(params).when().post("http://localhost:" + port + "/oauth/token");
		return response.jsonPath().getString("access_token");
	}
}
