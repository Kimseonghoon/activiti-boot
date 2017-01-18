package com.partdb.repository.cloud;

import org.springframework.data.jpa.repository.JpaRepository;

import com.partdb.entity.cloud.Person;

public interface PersonRepository extends JpaRepository<Person, Integer>{

	Person findByEmail(String email);
}
