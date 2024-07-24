package com.medilab.medschedule.repository;


import com.medilab.medschedule.Entity.Role;
import com.medilab.medschedule.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

     boolean existsByUsername(String username);
        List<User> findByRole(Role role);

//    Optional<User> findById(Long id);
}
