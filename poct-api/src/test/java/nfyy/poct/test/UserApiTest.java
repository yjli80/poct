package nfyy.poct.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.StringContains.containsString;

import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import nfyy.poct.domain.User;

public class UserApiTest extends TestWebConfigration {

	@Test
	public void givenUserId_thenGetUser() {
		HttpHeaders headers = getHttpHeadersWithToken();
		addJsonHeader(headers);
		
		HttpEntity<?> entity = new HttpEntity<String>("", headers);
		ResponseEntity<Resource<User>> resp = rest.exchange(
				getUrl("/api/users/1"),
				HttpMethod.GET, entity,
				new ParameterizedTypeReference<Resource<User>>() {});
		
		System.out.println(resp.getBody());
		
		User user = resp.getBody().getContent();
		
		assertThat(user, notNullValue());
		assertThat(user.getUsername(), containsString("admin"));
		
	}
}
