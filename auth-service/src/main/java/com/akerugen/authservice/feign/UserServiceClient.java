package com.akerugen.authservice.feign;

import com.akerugen.authservice.feign.dto.UserRequestDto;
import com.akerugen.authservice.feign.dto.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-service", url = "${user-service.url}")
public interface UserServiceClient {

    @GetMapping("/api/users/{id}")
    ResponseEntity<UserResponseDto> getUser(@PathVariable Long id);

    @PostMapping("/api/users")
    ResponseEntity<UserResponseDto> createUser(@RequestBody UserRequestDto request);
}