package nfyy.poct;

import java.util.Date;

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
public class InitDataRunner implements CommandLineRunner {
	
	private static final Logger logger = LoggerFactory.getLogger(InitDataRunner.class);
	
	@Autowired private UserRepository users;
	@Autowired private RoleRepository roles;
	@Autowired private PasswordEncoder encoder;

	@Override
	public void run(String... args) throws Exception {
		
		logger.info("init data...");
		
		Role role = null;
		if (roles.count() == 0) {
			role = new Role("ROLE_USER", "user");
			roles.save(role);
		} else {
			role = roles.findOne("ROLE_USER");
		}
		
		if (users.count() == 0) {
			User user = new User();
			user.addRole(role);
			user.setUsername("admin");
			user.setName("admin");
			user.setBirthDate(new Date());
			user.setEmail("admin@example.com");
			user.setPassword(encoder.encode("adscret"));
			user.setPasswordRaw(false);
			users.save(user);
		}
	}

}
