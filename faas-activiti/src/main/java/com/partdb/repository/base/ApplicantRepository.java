package com.partdb.repository.base;

import org.springframework.data.jpa.repository.JpaRepository;

import com.partdb.entity.base.Applicant;

public interface ApplicantRepository extends JpaRepository<Applicant, Long> {

}