package com.splitify.splitify.transaction.repository;

import com.splitify.splitify.transaction.domain.ExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Integer> {

  List<ExpenseEntity> findByGroupIdAndPaidBy(Integer groupId, Integer paidBy);
}
