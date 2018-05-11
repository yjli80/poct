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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import nfyy.poct.domain.User;
import nfyy.poct.domain.UserRepository;
import nfyy.poct.exception.EntityNotFoundException;

public class UserController {
	
	@Autowired
	private UserRepository repository;

	@RequestMapping(path="/user/{id}", method=RequestMethod.GET)
	public User getUserById(@PathVariable Integer id) throws Exception {
		User user = repository.findOne(id);
		if (user == null) {
			new EntityNotFoundException(User.class, "id", id);
		}
		return user;
	}
	
	@RequestMapping(path="/user", method=RequestMethod.GET)
	public User getUserByUsername(@RequestParam String username) throws Exception {
		User user = repository.findByUsername(username);
		if (user == null) {
			new EntityNotFoundException(User.class, "username", username);
		}
		return user;
	}
	
	@RequestMapping(path="/user", method=RequestMethod.GET)
	public Page<User> getUsers(Pageable pageable) throws Exception {
		return repository.findAll(pageable);
	}
	
	@RequestMapping(path="/user", method=RequestMethod.POST)
	public ResponseEntity<?> save(@RequestBody User user) {
		
		
		
		User saved = repository.save(user);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}").buildAndExpand(saved.getId()).toUri();
		
		return ResponseEntity.created(location).build();
	}
	
	@RequestMapping(path="/user/{id}", method=RequestMethod.DELETE)
	public ResponseEntity<?> delete(@PathVariable Integer id) {
		repository.delete(id);
		return ResponseEntity.ok().build();
	}
}
