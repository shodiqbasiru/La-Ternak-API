package com.enigma.laternak.service.impl;

import com.enigma.laternak.dto.request.PaginationStoreRequest;
import com.enigma.laternak.dto.request.StoreRequest;
import com.enigma.laternak.dto.response.ImageResponse;
import com.enigma.laternak.dto.response.ProductResponse;
import com.enigma.laternak.dto.response.StoreResponse;
import com.enigma.laternak.entity.ImageStore;
import com.enigma.laternak.entity.Product;
import com.enigma.laternak.entity.Store;
import com.enigma.laternak.repository.StoreRepository;
import com.enigma.laternak.service.ImageStoreService;
import com.enigma.laternak.service.StoreService;
import com.enigma.laternak.util.ValidationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class StoreServiceImplTest {

    @Mock
    private StoreRepository storeRepository;
    @Mock
    private ImageStoreService imageStoreService;
    @Mock
    private ValidationUtil validationUtil;
    private StoreService storeService;

    @BeforeEach
    void setUp() {
        storeService = new StoreServiceImpl(storeRepository, imageStoreService, validationUtil);
    }

    @Test
    void shouldReturnStoreWhenCreateStore() {
        Store store = new Store();
        store.setId("1");
        store.setStoreName("Store");

        Mockito.when(storeRepository.saveAndFlush(Mockito.any())).thenReturn(store);

        storeService.createStore(store);

        assertNotNull(store);
    }

    @Test
    void shouldReturnStoreWhenGetByEmail() {
        Store store = new Store();
        store.setId("1");
        store.setStoreName("Store");

        Mockito.when(storeRepository.findByEmail(Mockito.anyString())).thenReturn(java.util.Optional.of(store));

        Store result = storeService.getByEmail("bla@test.com");

        assertEquals(store, result);
    }


    @Test
    void shouldReturnStoreWhenGetById() {
        Store store = new Store();
        store.setId("1");
        store.setStoreName("Store");

        Mockito.when(storeRepository.findById(Mockito.any())).thenReturn(Optional.of(store));

        Store result = storeService.getById("1");

        assertEquals(store, result);
    }

    @Test
    void shouldReturnStoreResponseWithCorrectProductDetailsAndImageStoreWhenGetStoreById() {
        Store store = new Store();
        store.setId("1");
        store.setStoreName("Store");

        Product product1 = new Product();
        product1.setId("product1");
        product1.setProductName("Product 1");
        product1.setPrice(100);
        product1.setStock(10);
        product1.setDescription("Description 1");
        product1.setStore(store);

        Product product2 = new Product();
        product2.setId("product2");
        product2.setProductName("Product 2");
        product2.setPrice(200);
        product2.setStock(20);
        product2.setDescription("Description 2");
        product2.setStore(store);

        store.setProducts(List.of(product1, product2));

        ImageStore imageStore = new ImageStore();
        imageStore.setId("image1");
        imageStore.setName("Image 1");
        store.setImageStore(imageStore);

        Mockito.when(storeRepository.findById(Mockito.any())).thenReturn(Optional.of(store));

        StoreResponse result = storeService.getStoreById("1");

        assertEquals(store.getId(), result.getId());
        assertEquals(store.getStoreName(), result.getStoreName());

        assertNotNull(result.getProductDetails());
        assertEquals(2, result.getProductDetails().size());

        assertEquals(product1.getId(), result.getProductDetails().get(0).getId());
        assertEquals(product1.getProductName(), result.getProductDetails().get(0).getProductName());
        assertEquals(product1.getPrice(), result.getProductDetails().get(0).getPrice());

        assertEquals(product2.getId(), result.getProductDetails().get(1).getId());
        assertEquals(product2.getProductName(), result.getProductDetails().get(1).getProductName());
        assertEquals(product2.getPrice(), result.getProductDetails().get(1).getPrice());

        assertNotNull(result.getImageStore());
        assertEquals(imageStore.getName(), result.getImageStore().getName());
    }

    @Test
    void shouldReturnPageStoreResponseWhenGetAll() {
        PaginationStoreRequest request = new PaginationStoreRequest();
        request.setPage(1);
        request.setSize(10);
        request.setDirection("ASC");
        request.setSortBy("id");

        Product product = new Product();
        product.setId("1");
        product.setProductName("Product");
        product.setPrice(100);

        Store store = new Store();
        store.setId("1");
        store.setStoreName("Store");
        store.setProducts(List.of(product));

        product.setStore(store);

        Page<Store> page = new PageImpl<>(List.of(store));

        Mockito.when(storeRepository.findAll(Mockito.any(Pageable.class))).thenReturn(page);

        Page<StoreResponse> result = storeService.getAll(request);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldReturnStoreResponseWhenUpdateStore() {
        StoreRequest storeRequest = new StoreRequest();
        storeRequest.setStoreName("Store 1");
        storeRequest.setAddress("Address 1");

        ImageStore imageStore = new ImageStore();
        imageStore.setId("image1");
        imageStore.setName("Image 1");

        Product product1 = new Product();
        product1.setId("product1");
        product1.setProductName("Product 1");
        product1.setPrice(100);
        product1.setStock(10);
        product1.setDescription("Description 1");

        Store store = new Store();
        store.setId("1");
        store.setStoreName(storeRequest.getStoreName());
        store.setAddress(storeRequest.getAddress());
        store.setImageStore(imageStore);
        store.setProducts(List.of(product1));
        product1.setStore(store);


        Mockito.when(storeRepository.findById(Mockito.any())).thenReturn(Optional.of(store));
        Mockito.when(storeRepository.saveAndFlush(Mockito.any())).thenReturn(store);

        StoreResponse result = storeService.updateStore(storeRequest);

        assertEquals(store.getId(), result.getId());
        assertEquals(store.getStoreName(), result.getStoreName());
    }

    @Test
    void shouldReturnStoreWhenSetVerifyStore() {
        Store store = new Store();
        store.setId("1");
        store.setStoreName("Store");
        store.setVerified(false);

        Mockito.when(storeRepository.saveAndFlush(Mockito.any())).thenReturn(store);

        storeService.setVerifyStore(store);

        assertFalse(store.isVerified());

    }

    @Test
    void shouldReturnVoidWhenDeleteAccountSeller() {
        Store currentStore = new Store();
        currentStore.setId("1");
        currentStore.setVerified(false);
        currentStore.setActive(false);

        Mockito.when(storeRepository.findById(Mockito.any())).thenReturn(Optional.of(currentStore));

        storeService.deleteAccountSeller("1");

        assertFalse(currentStore.isActive());
        assertFalse(currentStore.isVerified());
    }
}