package br.com.astro.operations.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.astro.operations.domain.dto.response.UserDTO;
import br.com.astro.operations.security.CurrentUser;
import br.com.astro.operations.service.UserService;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@CurrentUser UUID userId) {
        UserDTO user = userService.getCurrentUser(userId);
        return ResponseEntity.ok(user);
    }

}
