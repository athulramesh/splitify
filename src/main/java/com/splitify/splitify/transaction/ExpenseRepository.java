package com.splitify.splitify.transaction;

import com.splitify.splitify.transaction.domain.ExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Integer> {}
