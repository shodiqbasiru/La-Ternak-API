package com.enigma.laternak.controller;

import com.enigma.laternak.constant.ApiRoute;
import com.enigma.laternak.constant.Message;
import com.enigma.laternak.dto.request.PaginationStoreRequest;
import com.enigma.laternak.dto.request.StoreRequest;
import com.enigma.laternak.dto.response.CommonResponse;
import com.enigma.laternak.dto.response.PagingResponse;
import com.enigma.laternak.dto.response.StoreResponse;
import com.enigma.laternak.service.StoreService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = ApiRoute.STORE_API)
@Tag(name = "Store", description = "Api for store")
public class StoreController {

    private final StoreService storeService;
    private final ObjectMapper objectMapper;

    @Operation(
            summary = "get all store",
            description = "get all store"
    )
    @SecurityRequirement(name = "Authorization")
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

    @Operation(
            summary = "Get Store",
            description = "Get store by id"
    )
    @SecurityRequirement(name = "Authorization")
    @GetMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<StoreResponse>> findById(@PathVariable String id) {
        StoreResponse result = storeService.getStoreById(id);
        CommonResponse<StoreResponse> response = CommonResponse.<StoreResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message(Message.SUCCESS_GET_DATA.getMessage())
                .data(result)
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Update Store",
            description = "Update store"
    )
    @SecurityRequirement(name = "Authorization")
    @PutMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<?>> update(
            @RequestPart(name = "store") String jsonStore,
            @RequestPart(name = "image", required = false) MultipartFile image
    ) {
        CommonResponse.CommonResponseBuilder<StoreResponse> builder = CommonResponse.builder();
        try {
            StoreRequest request = objectMapper.readValue(jsonStore, new TypeReference<>() {
            });
            request.setImage(image);

            StoreResponse response = storeService.updateStore(request);
            builder.statusCode(HttpStatus.CREATED.value());
            builder.message(Message.SUCCESS_UPDATE.getMessage());
            builder.data(response);
            return ResponseEntity.status(HttpStatus.CREATED).body(builder.build());
        }catch (Exception e){
            builder.message(e.getMessage());
            builder.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(builder.build());
        }
    }


    @Operation(
            summary = "Delete Store",
            description = "Delete store by id"
    )
    @SecurityRequirement(name = "Authorization")
    @DeleteMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<StoreResponse>> delete(@PathVariable String id) {
        storeService.deleteAccountSeller(id);
        CommonResponse<StoreResponse> response = CommonResponse.<StoreResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message(Message.SUCCESS_UNACTIVATED_ACCOUNT_SELLER.getMessage())
                .build();
        return ResponseEntity.ok(response);
    }

}
