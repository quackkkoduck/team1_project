package com.example.board.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.board.model.User;

public interface UserRepository extends JpaRepository<User, String> {
  public User findByEmail(String email
  // , String pwd
  );
  Optional<User> findByName(String name);
  User  findById(Long userId);
  // User findbyEmail(String Email);
}