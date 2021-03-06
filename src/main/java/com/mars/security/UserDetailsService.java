package com.mars.security;

import com.mars.domain.User;
import com.mars.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

	private final Logger log = LoggerFactory.getLogger(UserDetailsService.class);

	@Inject
	private UserRepository userRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(final String login) {
		log.debug("Authenticating {}", login);
		String lowercaseLogin = login.toLowerCase(Locale.ENGLISH);
		Optional<User> userFromDatabase = userRepository.findOneByLogin(lowercaseLogin);
		return userFromDatabase.map(user -> {
			if (!user.getActivated()) {
				throw new UserNotActivatedException("User " + lowercaseLogin + " was not activated");
			}
			List<GrantedAuthority> grantedAuthorities = new ArrayList();
			grantedAuthorities.add(new SimpleGrantedAuthority(user.getRole().name()));
			return new org.springframework.security.core.userdetails.User(lowercaseLogin,
				user.getPassword(), grantedAuthorities);
		}).orElseThrow(() -> new UsernameNotFoundException("User " + lowercaseLogin + " was not found in the "
			+ "database"));
	}
}
