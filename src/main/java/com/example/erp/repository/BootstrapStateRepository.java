package com.example.erp.repository;

import com.example.erp.entity.BootstrapState;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface BootstrapStateRepository extends JpaRepository<BootstrapState, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<BootstrapState> findByStateKey(String stateKey);
}