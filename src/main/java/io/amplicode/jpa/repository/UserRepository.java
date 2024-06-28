package io.amplicode.jpa.repository;

import io.amplicode.jpa.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}