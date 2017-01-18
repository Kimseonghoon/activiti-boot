package com.partdb.security;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.partdb.entity.cloud.Person;
import com.partdb.repository.cloud.PersonRepository;

@Service("myUserDetailsService")
public class MyUserDetailService implements UserDetailsService {

	@Autowired
	private PersonRepository personRepository;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Person person = personRepository.findByEmail(email);
		
		List<GrantedAuthority> authorities = buildUserAuthority(person.getRole());
		
		return buildUserForAuthentication(person, authorities);
		
	}

	// Converts com.mkyong.users.model.User user to
	// org.springframework.security.core.userdetails.User
	private User buildUserForAuthentication(Person person, List<GrantedAuthority> authorities) {
		return new User(person.getEmail(), person.getPassword(), true, true, true, true, authorities);
	}

	private List<GrantedAuthority> buildUserAuthority(String userRole) {

		Set<GrantedAuthority> setAuths = new HashSet<GrantedAuthority>();

		if(userRole.equals("admin")) {
			userRole = "ROLE_ADMIN";
		}
		
		// Build user's authorities
		//for (UserRole userRole : userRoles) {
			setAuths.add(new SimpleGrantedAuthority(userRole));
		//}

		List<GrantedAuthority> Result = new ArrayList<GrantedAuthority>(setAuths);

		return Result;
	}
}
