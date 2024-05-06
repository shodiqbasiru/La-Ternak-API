package com.enigma.laternak;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication

@OpenAPIDefinition(
        info = @Info(
                title = "LaTernak API",
                version = "1.0",
                description = "LaTernak API Documentation"
        )
)
@SecurityScheme(name = "Authorization", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class LaTernakApplication {

    public static void main(String[] args) {
        SpringApplication.run(LaTernakApplication.class, args);
    }

}
