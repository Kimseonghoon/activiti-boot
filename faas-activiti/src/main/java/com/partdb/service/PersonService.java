package com.partdb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.partdb.entity.cloud.Person;
import com.partdb.repository.cloud.PersonRepository;

@Component
public class PersonService {

	@Autowired
	PersonRepository personRepository;
	
	public Person login(Person person) {
		person = personRepository.findByEmail(person.getEmail());
		if(person != null) {
			return person;
		} else {
			return null;
		}
	}
	
	public Person signIn(Person person) {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		person.setPassword(passwordEncoder.encode(person.getPassword()));
		person.setState("인증");
		return personRepository.save(person);
	}
	
	public Person findByEmail(String email) {
		return personRepository.findByEmail(email);
	}
}
