package com.hrmManagement.repository;

import com.hrmManagement.entity.ContractDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContractDocumentRepository extends JpaRepository<ContractDocument, Long> {

    List<ContractDocument> findByContractIdOrderByUploadedAtDesc(Long contractId);
}
