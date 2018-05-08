package nfyy.poct.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import nfyy.poct.domain.Role;
import nfyy.poct.domain.RoleRepository;
import nfyy.poct.domain.User;
import nfyy.poct.domain.UserRepository;

@Component
public class TestDataInitRunner implements CommandLineRunner {
	
	private static final Logger logger = LoggerFactory.getLogger(TestDataInitRunner.class);
	
	@Autowired private UserRepository users;
	@Autowired private RoleRepository roles;
	@Autowired private PasswordEncoder encoder;

	@Override
	public void run(String... args) throws Exception {
		
		logger.info("init data...");
		
		if (users.count() == 0) {
			
			Role role = new Role("ROLE_USER", "user");
			roles.save(role);
			
			User user = new User();
			user.addRole(role);
			user.setUsername("test");
			user.setName("test");
			user.setPassword(encoder.encode("test"));
			users.save(user);
		}
	}

}
