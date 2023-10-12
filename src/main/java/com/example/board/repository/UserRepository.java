package com.example.board.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.board.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
  public User findByEmailAndPwd(String email, String pwd);
  List<User> findByEmail(String email);
  User findByPwd(String pwd);
  User findById(long id);
  // Optional<User> findByEmail(String Email);
  
}