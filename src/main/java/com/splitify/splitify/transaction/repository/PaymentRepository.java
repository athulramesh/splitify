package com.splitify.splitify.transaction.repository;

import com.splitify.splitify.transaction.domain.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository
    extends JpaRepository<PaymentEntity, Integer>, PaymentRepositoryCustom {}
