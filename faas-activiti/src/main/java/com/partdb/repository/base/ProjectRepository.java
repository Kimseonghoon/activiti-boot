package com.partdb.repository.base;

import org.springframework.data.jpa.repository.JpaRepository;

import com.partdb.entity.base.Project;

public interface ProjectRepository extends JpaRepository<Project, Integer>{

}
