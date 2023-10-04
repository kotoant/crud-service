package io.github.kotoant.crud.rest.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@Tag(name = "swagger")
public class RootController {
    @Value("#{servletContext.contextPath}")
    private String servletContextPath;

    @GetMapping(value = {"", "/", "/api"})
    public void redirectToSwagger(HttpServletResponse response) throws IOException {
        response.sendRedirect(servletContextPath + "/swagger-ui/index.html");
    }
}
