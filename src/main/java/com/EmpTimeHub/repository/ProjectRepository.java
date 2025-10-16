package com.EmpTimeHub.repository;

import com.EmpTimeHub.entity.Client;
import com.EmpTimeHub.entity.Employee;
import com.EmpTimeHub.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {

    public Project findByEmployeeAndClient(Employee employee, Client client);
}
