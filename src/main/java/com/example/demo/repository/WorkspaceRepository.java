package com.example.demo.repository;

import com.example.demo.model.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
    // Spring Data JPA 会自动提供所有基础的CRUD操作
}