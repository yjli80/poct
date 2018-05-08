package nfyy.poct.test;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import nfyy.poct.Application;
import nfyy.poct.domain.Role;
import nfyy.poct.domain.RoleRepository;
import nfyy.poct.domain.User;
import nfyy.poct.domain.UserRepository;
import nfyy.poct.rsql.CustomRsqlVisitor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIn.isIn;
import static org.hamcrest.core.IsNot.not;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { Application.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@Transactional
public class UserRepositoryTest {

	@Autowired
	private UserRepository repo;
	
	@Autowired
	private RoleRepository roles;

	@Autowired
	private PasswordEncoder encoder;

	User userAvy;
	User userAmi;
	User userAlex;
	
	Role roleUser;
	
	private User buildUser(String name, String gender) {
		User user = new User();
		user.setBirthDate(new Date());
		user.setEmail(name + "@test.com");
		user.setGender(gender);
		user.setMobile(RandomStringUtils.randomNumeric(11));
		user.setName(name);
		user.setPassword(encoder.encode(name));
		user.setUsername(name);
		user.addRole(roleUser);

		repo.save(user);
		
		return user;
	}

	@Before
	public void init() {
		roleUser = roles.findOne("ROLE_USER");
		userAvy = buildUser("avy", "女");
		userAmi = buildUser("ami", "女");
		userAlex = buildUser("alex", "男");
	}

	@After
	public void clean() {
		if (userAmi != null && userAmi.getId() != null) {
			repo.delete(userAmi);
		}
		if (userAvy != null && userAvy.getId() != null) {
			repo.delete(userAvy);
		}
		if (userAlex != null && userAlex.getId() != null) {
			repo.delete(userAlex);
		}
	}

	@Test
	public void testSearch() {
		Node rootNode = new RSQLParser().parse("gender==女;name==a*");
		Specification<User> spec = rootNode.accept(new CustomRsqlVisitor<User>());
		List<User> results = repo.findAll(spec);

		assertThat(userAvy, isIn(results));
		assertThat(userAmi, isIn(results));
		assertThat(userAlex, not(isIn(results)));
	}
}
