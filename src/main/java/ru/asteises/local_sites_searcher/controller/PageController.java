package ru.asteises.local_sites_searcher.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.asteises.local_sites_searcher.core.model.Page;
import ru.asteises.local_sites_searcher.service.PageService;
import ru.asteises.local_sites_searcher.service.SearchService;

import java.util.List;
import java.util.UUID;

@RestController()
@RequestMapping("/api/page")
@AllArgsConstructor
public class PageController {

    private final SearchService searchService;
    private final PageService pageService;

    // TODO Можно сохранять данные о сайтах в БД, чтобы было проще искать по ним информацию. Правда как потом проверять, не произошли ли изменения на странице.

    /**
     * Метод ищет нужное словосочетание по заголовку (title) страницы сайта.
     * @return возвращаем заголовок.
     */
    @PostMapping("/in_title")
    public ResponseEntity<List<Page>> getDataFromTitle(@RequestBody List<String> urls, @RequestParam String word) {
        return ResponseEntity.ok(searchService.search(urls, word));
    }

    /**
     * Endpoint для парсинга страниц сайта.
     * @param webSitesId - входящий id сайта.
     * @return - пока вернем просто ОК.
     */
    @PostMapping("/add")
    public ResponseEntity<String> renewPagesDataBase(@RequestBody UUID webSitesId) {
        pageService.renewPagesDataBase(webSitesId);
        return ResponseEntity.ok("Everything OK");
    }
}
