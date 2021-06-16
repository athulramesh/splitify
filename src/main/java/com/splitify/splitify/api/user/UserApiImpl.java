package com.splitify.splitify.api.user;

import com.splitify.splitify.api.security.assembler.SecurityAssembler;
import com.splitify.splitify.api.security.dto.UserDto;
import com.splitify.splitify.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserApiImpl implements UserApi {


    @Autowired
    private UserService userService;

    @Autowired
    private SecurityAssembler assembler;

    @Override
    public UserDto getUser(int id) throws Exception {
        return assembler.assembleUserDetails(userService.getUser(id));
    }
}
