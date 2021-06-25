package com.splitify.splitify.api.connection;

import com.splitify.splitify.api.connection.dto.ConnectionIdDto;
import com.splitify.splitify.api.connection.dto.TargetUserDto;
import com.splitify.splitify.connection.enums.ConnectionStatus;
import com.splitify.splitify.connection.service.ConnectionDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/api/connections/{userId}")
public interface ConnectionApi {

  @PostMapping()
  ConnectionIdDto sendConnectionRequest(
      @PathVariable("userId") Integer fromUserId, @RequestBody TargetUserDto targetUserDto);

  @GetMapping()
  List<ConnectionDetails> fetchConnectionRequests(
      @PathVariable("userId") Integer userId, @RequestParam ConnectionStatus type);

  @PutMapping("/approve")
  String acceptConnectionRequest(@RequestBody ConnectionIdDto connectionIdDto);

  @PutMapping("/reject")
  String rejectConnectionRequest(@RequestBody ConnectionIdDto connectionIdDto);
}
