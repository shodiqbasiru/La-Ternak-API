package com.enigma.laternak.controller;

import com.enigma.laternak.constant.ApiRoute;
import com.enigma.laternak.dto.request.PaginationUserRequest;
import com.enigma.laternak.dto.request.UpdateUserRequest;
import com.enigma.laternak.dto.response.CommonResponse;
import com.enigma.laternak.dto.response.PagingResponse;
import com.enigma.laternak.dto.response.UserResponse;
import com.enigma.laternak.entity.User;
import com.enigma.laternak.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = ApiRoute.USER_API)
@Tag(name = "User", description = "API for user")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Get All",
            description = "Get all user"
    )
    @SecurityRequirement(name = "Authorization")
    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<List<UserResponse>>> getAll(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "sortBy", defaultValue = "customerName") String sortBy,
            @RequestParam(name = "direction", defaultValue = "asc") String direction,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "role", required = false) String role
    ) {
        PaginationUserRequest pageRequest = PaginationUserRequest.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .direction(direction)
                .customerName(name)
                .role(role)
                .build();
        Page<UserResponse> result = userService.getAll(pageRequest);

        PagingResponse paging = PagingResponse.builder()
                .page(result.getPageable().getPageNumber() +1)
                .size(result.getPageable().getPageSize())
                .totalPages(result.getTotalPages())
                .totalElement(result.getTotalElements())
                .hasNext(result.hasNext())
                .hasPrevious(result.hasPrevious())
                .build();

        CommonResponse<List<UserResponse>> response = CommonResponse.<List<UserResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .data(result.getContent())
                .message("Get all data successfully")
                .pagingResponse(paging)
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get User",
            description = "Get user by id"
    )
    @SecurityRequirement(name = "Authorization")
    @GetMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<UserResponse>> findById(@PathVariable String id) {
        UserResponse result = userService.getUserById(id);
        CommonResponse<UserResponse> response = CommonResponse.<UserResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Get data successfully")
                .data(result)
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Update User",
            description = "Update user"
    )
    @SecurityRequirement(name = "Authorization")
    @PutMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<UserResponse>> update(@RequestBody UpdateUserRequest request) {
        User result = userService.update(request);
        CommonResponse<UserResponse> response = CommonResponse.<UserResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Update data successfully")
                .data(UserResponse.builder()
                        .id(result.getId())
                        .customerName(result.getCustomerName())
                        .phoneNumber(result.getPhoneNumber())
                        .address(result.getAddress())
                        .build())
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Delete User",
            description = "Delete user by id"
    )
    @SecurityRequirement(name = "Authorization")
    @DeleteMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<UserResponse>> deleteById(@PathVariable String id) {
        userService.deleteAccountUser(id);

        CommonResponse<UserResponse> response = CommonResponse.<UserResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Unactivated account user successfully")
                .build();
        return ResponseEntity.ok(response);
    }
}
