package com.enigma.laternak.controller;

import com.enigma.laternak.constant.ApiRoute;
import com.enigma.laternak.dto.request.PaginationUserRequest;
import com.enigma.laternak.dto.request.UpdateUserRequest;
import com.enigma.laternak.dto.response.CommonResponse;
import com.enigma.laternak.dto.response.PagingResponse;
import com.enigma.laternak.dto.response.UserResponse;
import com.enigma.laternak.entity.User;
import com.enigma.laternak.service.UserService;
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
public class UserController {

    private final UserService userService;

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<List<UserResponse>>> getAll(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "sortBy", defaultValue = "customerName") String sortBy,
            @RequestParam(name = "direction", defaultValue = "asc") String direction,
            @RequestParam(name = "name", required = false) String name
    ) {
        PaginationUserRequest pageRequest = PaginationUserRequest.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .direction(direction)
                .customerName(name)
                .build();
        Page<UserResponse> result = userService.getAll(pageRequest);

        PagingResponse paging = PagingResponse.builder()
                .page(result.getNumber() + 1)
                .size(result.getSize())
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

    @GetMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<User>> findById(@PathVariable String id) {
        User result = userService.getById(id);
        CommonResponse<User> response = CommonResponse.<User>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Get data successfully")
                .data(result)
                .build();
        return ResponseEntity.ok(response);
    }

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
