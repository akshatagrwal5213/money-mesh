package com.bank.repository;

import com.bank.model.PendingTransfer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PendingTransferRepository
extends JpaRepository<PendingTransfer, Long> {
}
