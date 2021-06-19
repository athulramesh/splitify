package com.splitify.splitify.api.user;

import com.splitify.splitify.api.security.dto.UserDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public interface UserApi {
  @GetMapping("/{id}")
  UserDto getUser(@PathVariable int id) throws Exception;
}
