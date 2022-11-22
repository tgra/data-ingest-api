package com.example.filedemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.filedemo.model.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

}
