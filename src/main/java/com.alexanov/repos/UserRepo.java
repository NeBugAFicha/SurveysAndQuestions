package com.alexanov.repos;

import com.alexanov.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Integer> {
    User findByUsername(String username);
    User getById(Integer id);
}
