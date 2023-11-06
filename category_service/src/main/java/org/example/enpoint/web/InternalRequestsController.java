package org.example.enpoint.web;

import lombok.RequiredArgsConstructor;
import org.example.service.CategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/internal")
public class InternalRequestsController {

    private final CategoryService categoryService;

    @GetMapping(value = "/exists/{uuid}")
    public Boolean categoryExists(@PathVariable UUID uuid) {
        return categoryService.existsByUuid(uuid);
    }

}
