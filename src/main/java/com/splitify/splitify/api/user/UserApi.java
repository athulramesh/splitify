package com.splitify.splitify.api.user;

import com.splitify.splitify.api.security.dto.UserDetailsDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public interface UserApi {
  @GetMapping()
  UserDetailsDto getUserByUserName(@RequestParam String userName) throws Exception;
}
