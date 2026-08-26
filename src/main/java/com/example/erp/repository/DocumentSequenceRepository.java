package com.example.erp.repository;
import com.example.erp.entity.DocumentSequence;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import java.util.*;
public interface DocumentSequenceRepository extends JpaRepository<DocumentSequence,DocumentSequence.Key> { @Lock(LockModeType.PESSIMISTIC_WRITE) Optional<DocumentSequence> findById(DocumentSequence.Key id); }
