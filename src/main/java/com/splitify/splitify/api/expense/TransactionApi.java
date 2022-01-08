package com.splitify.splitify.api.expense;

import com.splitify.splitify.api.expense.dto.GroupTransactionDetailsDto;
import com.splitify.splitify.api.expense.dto.IndividualTransactionDetailsDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/transactions")
@CrossOrigin(origins = "https://simplifysplit.web.app/", allowedHeaders = "*")
public interface TransactionApi {
  /**
   * Gets the group wise transactions
   *
   * @param userId fromId
   * @return group wise transaction
   */
  @GetMapping("/{userId}")
  GroupTransactionDetailsDto getGroupWiseTransactions(@PathVariable("userId") Integer userId);

  /**
   * Gets the individual transaction.
   *
   * @param userId fromId
   * @param groupId groupId
   * @return individual transaction.
   */
  @GetMapping("/{userId}/group/{groupId}")
  IndividualTransactionDetailsDto getIndividualTransaction(
      @PathVariable("userId") Integer userId, @PathVariable("groupId") Integer groupId);
}
