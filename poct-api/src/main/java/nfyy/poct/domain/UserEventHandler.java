package nfyy.poct.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler(User.class)
public class UserEventHandler {

	protected static final Logger logger = LoggerFactory.getLogger(UserEventHandler.class);
	
	@Autowired
	private PasswordEncoder encoder;
	
	@HandleBeforeCreate
	public void handleBeforeCreate(User user) {
		encodePassword(user);
	}
	
	@HandleBeforeSave
	public void handleBeforeSave(User user) {
		encodePassword(user);
	}
	
	private void encodePassword(User user) {
		if (user.isPasswordRaw()) {
			user.setPassword(encoder.encode(user.getPassword()));
			user.setPasswordRaw(false);
		}
	}
	
}
