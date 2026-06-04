package com.example.devlogjava.mapper;

import com.example.devlogjava.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper extends JpaRepository<User, Long> {
    User findByPhone(String phone);
}
