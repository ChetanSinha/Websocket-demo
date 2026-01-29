package com.example.web.socket_demo.repository;

import com.example.web.socket_demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}