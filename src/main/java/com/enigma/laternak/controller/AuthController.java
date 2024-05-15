package com.enigma.laternak.controller;

import com.enigma.laternak.constant.ApiRoute;
import com.enigma.laternak.dto.request.AuthRequest;
import com.enigma.laternak.dto.request.LoginRequest;
import com.enigma.laternak.dto.request.RegisterSellerRequest;
import com.enigma.laternak.dto.response.*;
import com.enigma.laternak.service.AuthService;
import com.enigma.laternak.dto.request.LoginSellerRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiRoute.AUTH_API)
@Tag(name = "Auth", description = "Auth APi")
public class AuthController {
    private final AuthService authService;

    @Operation(
            summary = "Register new user",
            description = "Register new user"
    )
    @SecurityRequirement(name = "Authorization")
    @PostMapping(
            path = "/register",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<?>> register(@RequestBody AuthRequest request) {
        RegisterResponse register = authService.register(request);
        CommonResponse<RegisterResponse> response = CommonResponse.<RegisterResponse>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Created a new account successfully")
                .data(register)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Register new user as seller",
            description = "Register new user as seller"
    )
    @SecurityRequirement(name = "Authorization")
    @PostMapping(
            path = "/register-seller",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<?>> registerSeller(@RequestBody RegisterSellerRequest request) {
        RegisterSellerResponse register = authService.registerSeller(request);
        CommonResponse<RegisterSellerResponse> response = CommonResponse.<RegisterSellerResponse>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Created a new seller successfully, please verify your email")
                .data(register)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Login",
            description = "Login"
    )
    @SecurityRequirement(name = "Authorization")
    @PostMapping(
            path = "/login",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<?>> login(@RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.login(request);
        CommonResponse<LoginResponse> response = CommonResponse.<LoginResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Login successfully")
                .data(loginResponse)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Login Seller",
            description = "Login Seller"
    )
    @SecurityRequirement(name = "Authorization")
    @PostMapping(
            path = "/login-seller",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<?>> loginSeller(@RequestBody LoginRequest request) {
        LoginSellerResponse loginResponse = authService.loginSeller(request);
        CommonResponse<LoginSellerResponse> response = CommonResponse.<LoginSellerResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Login successfully")
                .data(loginResponse)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Verify account seller",
            description = "Verify account seller"
    )
    @SecurityRequirement(name = "Authorization")
    @GetMapping(
            path = "/verify-account-seller"
    )
    public RedirectView verifyAccount(
            @RequestParam(name = "email") String email,
            @RequestParam(name = "otp") String otp
    ) {
        authService.verifyAccountSeller(email, otp);
        return new RedirectView("http://10.10.102.94:5173/verification");
    }

    @Operation(
            summary = "Regenerate OTP",
            description = "Regenerate OTP"
    )
    @SecurityRequirement(name = "Authorization")
    @GetMapping(
            path = "/regenerate-otp"
    )
    public ResponseEntity<CommonResponse<?>> regenerateOtp(
            @RequestParam(name = "email") String email
    ) {
        String regenerateOtp = authService.regenerateOtp(email);
        CommonResponse<?> response = CommonResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .data(regenerateOtp)
                .message("regenerate otp successfully")
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @SecurityRequirement(name = "Authorization")
    @GetMapping(path = "validate-token", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> validateToken() {
        boolean isValid = authService.validateToken();
        if (isValid) {
            CommonResponse<String> response = CommonResponse.<String>builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("Get data successfully")
                    .build();
            return ResponseEntity.ok(response);
        } else {
            CommonResponse<String> response = CommonResponse.<String>builder()
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                    .message("Invalid jwt token")
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

}
