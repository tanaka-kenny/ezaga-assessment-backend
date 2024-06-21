package dev.tanaka.portal_backend.repository;

import dev.tanaka.portal_backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
