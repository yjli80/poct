package nfyy.poct.web.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import nfyy.poct.domain.User;
import nfyy.poct.domain.UserRepository;

@RestController
@RequestMapping(path="/api")
public class UserController {
	
	@Autowired
	private UserRepository repository;

	@RequestMapping(path="/user/{username}")
	public User getUserByUsername(@PathVariable String username) throws Exception {
		User user = repository.findByUsername(username);
		return user;
	}
	
	@RequestMapping(path="/users", method=RequestMethod.GET)
	public Page<User> getUsers(Pageable pageable) throws Exception {
		return repository.findAll(pageable);
	}
}
