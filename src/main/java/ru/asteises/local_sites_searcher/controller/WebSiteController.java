package ru.asteises.local_sites_searcher.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.asteises.local_sites_searcher.core.model.WebSite;
import ru.asteises.local_sites_searcher.service.WebSiteService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/site")
@AllArgsConstructor
public class WebSiteController {

    private final WebSiteService webSiteService;

    @PostMapping("/add")
    public ResponseEntity<Set<WebSite>> renewWebSiteDataBase(@RequestBody List<String> urls) {
        return ResponseEntity.ok(webSiteService.incomeNewWebSites(urls));
    }
}
