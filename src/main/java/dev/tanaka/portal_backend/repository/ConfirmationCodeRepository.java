package dev.tanaka.portal_backend.repository;

import dev.tanaka.portal_backend.domain.ConfirmationCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfirmationCodeRepository extends JpaRepository<ConfirmationCode, Long> {
}
