package nfyy.poct.web.api;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import nfyy.poct.domain.User;
import nfyy.poct.domain.UserRepository;

@RestController
@RequestMapping(path="/api")
public class UserController {
	
	@Autowired
	private UserRepository repository;

	@RequestMapping(path="/user/{id}")
	public User getUserByUsername(@PathVariable("id") User user) throws Exception {
		return user;
	}
	
	@RequestMapping(path="/users", method=RequestMethod.GET)
	public Page<User> getUsers(Pageable pageable) throws Exception {
		return repository.findAll(pageable);
	}
	
	@RequestMapping(path="/user", method=RequestMethod.POST)
	public ResponseEntity<?> add(@RequestBody User user) {
		User saved = repository.save(user);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}").buildAndExpand(saved.getId()).toUri();
		
		return ResponseEntity.created(location).build();
	}
	
}
