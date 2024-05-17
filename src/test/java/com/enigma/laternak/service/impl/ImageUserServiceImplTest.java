package com.enigma.laternak.service.impl;

import com.enigma.laternak.entity.ImageUser;
import com.enigma.laternak.repository.ImageUserRepository;
import com.enigma.laternak.service.ImageUserService;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ImageUserServiceImplTest {
    @Mock
    private ImageUserRepository imageUserRepository;
    @Mock
    private Path directoryPath;
    private ImageUserService imageUserService;

    @BeforeEach
    void setUp() {
        imageUserService = new ImageUserServiceImpl(directoryPath.toString(), imageUserRepository);
    }

    @Test
    public void shouldInitDirectory() throws Exception {
        try (MockedStatic<Paths> pathsMockedStatic = Mockito.mockStatic(Paths.class)) {
            pathsMockedStatic.when(() -> Paths.get(anyString())).thenReturn(directoryPath);

            try (MockedStatic<Files> filesMockedStatic = Mockito.mockStatic(Files.class)) {
                filesMockedStatic.when(() -> Files.exists(directoryPath)).thenReturn(false);
                filesMockedStatic.when(() -> Files.createDirectory(directoryPath)).thenReturn(directoryPath);

                ImageUserServiceImpl imageUserService = new ImageUserServiceImpl("mockDirectory", imageUserRepository);
                imageUserService.iniDirectory();

                filesMockedStatic.verify(() -> Files.exists(directoryPath));
                filesMockedStatic.verify(() -> Files.createDirectory(directoryPath));
            }
        }
    }

    @Test
    void shouldThrowExceptionWhenInitDirectoryFailed() throws Exception {
        try (MockedStatic<Paths> pathsMockedStatic = Mockito.mockStatic(Paths.class)) {
            pathsMockedStatic.when(() -> Paths.get(anyString())).thenReturn(directoryPath);

            try (MockedStatic<Files> filesMockedStatic = Mockito.mockStatic(Files.class)) {
                filesMockedStatic.when(() -> Files.exists(directoryPath)).thenReturn(false);
                filesMockedStatic.when(() -> Files.createDirectory(directoryPath)).thenThrow(IOException.class);

                assertThrows(ResponseStatusException.class, () -> {
                    ImageUserServiceImpl imageUserService = new ImageUserServiceImpl("mockDirectory", imageUserRepository);
                    imageUserService.iniDirectory();
                });

                filesMockedStatic.verify(() -> Files.exists(directoryPath));
                filesMockedStatic.verify(() -> Files.createDirectory(directoryPath));
            }
        }
    }

    @Test
    void shouldCreateImageUser() throws Exception {
        // Given
        MultipartFile multipartFile = new MockMultipartFile("imagefile", "image.jpg", "image/jpeg", "image".getBytes());
        ImageUser imageUser = ImageUser.builder()
                .name(multipartFile.getOriginalFilename())
                .contentType(multipartFile.getContentType())
                .size(multipartFile.getSize())
                .path(directoryPath.toString())
                .build();
        when(imageUserRepository.saveAndFlush(any(ImageUser.class))).thenReturn(imageUser);

        // Mock the static Files.copy method
        try (MockedStatic<Files> filesMockedStatic = Mockito.mockStatic(Files.class)) {
            filesMockedStatic.when(() -> Files.copy(any(InputStream.class), any(Path.class))).thenReturn(1L);

            imageUserService.create(multipartFile);

            verify(imageUserRepository, times(1)).saveAndFlush(any(ImageUser.class));
        }
    }

    @Test
    void shouldThrowExceptionWhenCreateImageUserWithInvalidContentType() {
        MultipartFile multipartFile = new MockMultipartFile("imagefile", "image.txt", "text/plain", "image".getBytes());
        assertThrows(ConstraintViolationException.class, () -> imageUserService.create(multipartFile));
    }

    @Test
    void shouldGetImageUserById() throws Exception {
        String id = "1";
        ImageUser imageUser = ImageUser.builder()
                .id(id)
                .name("image.jpg")
                .contentType("image/jpeg")
                .size(123L)
                .path(directoryPath.resolve("image.jpg").toString())
                .build();
        when(imageUserRepository.findById(id)).thenReturn(Optional.of(imageUser));

        try (MockedStatic<Files> filesMockedStatic = Mockito.mockStatic(Files.class)) {
            filesMockedStatic.when(() -> Files.exists(any(Path.class))).thenReturn(true);

            Resource resource = imageUserService.getById(id);

            assertNotNull(resource);
        }
    }

    @Test
    void shouldThrowExceptionWhenGetImageUserByInvalidId() {
        assertThrows(ResponseStatusException.class, () -> imageUserService.getById("invalidId"));
    }

    @Test
    void shouldThrowExceptionWhenDeleteImageUserByInvalidId() {
        assertThrows(ResponseStatusException.class, () -> imageUserService.deleteById("invalidId"));
    }


}