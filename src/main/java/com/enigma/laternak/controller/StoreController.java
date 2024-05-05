package com.enigma.laternak.controller;

import com.enigma.laternak.constant.ApiRoute;
import com.enigma.laternak.dto.request.PaginationStoreRequest;
import com.enigma.laternak.dto.response.CommonResponse;
import com.enigma.laternak.dto.response.PagingResponse;
import com.enigma.laternak.dto.response.StoreResponse;
import com.enigma.laternak.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = ApiRoute.STORE_API)
public class StoreController {

    private final StoreService storeService;

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<List<StoreResponse>>> getAll(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "sortBy", defaultValue = "storeName") String sortBy,
            @RequestParam(name = "direction", defaultValue = "asc") String direction,
            @RequestParam(name = "storeName", required = false) String name
    ) {
        PaginationStoreRequest pageRequest = PaginationStoreRequest.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .direction(direction)
                .storeName(name)
                .build();
        Page<StoreResponse> result = storeService.getAll(pageRequest);

        PagingResponse paging = PagingResponse.builder()
                .page(result.getNumber() + 1)
                .size(result.getSize())
                .totalPages(result.getTotalPages())
                .totalElement(result.getTotalElements())
                .hasNext(result.hasNext())
                .hasPrevious(result.hasPrevious())
                .build();

        CommonResponse<List<StoreResponse>> response = CommonResponse.<List<StoreResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .data(result.getContent())
                .message("Get all data successfully")
                .pagingResponse(paging)
                .build();

        return ResponseEntity.ok(response);

    }

    @DeleteMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<StoreResponse>> delete(@PathVariable String id) {
        storeService.deleteAccountSeller(id);
        CommonResponse<StoreResponse> response = CommonResponse.<StoreResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Unactivated account seller successfully")
                .build();
        return ResponseEntity.ok(response);
    }

}
