package ru.asteises.local_sites_searcher.controller;

import lombok.AllArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.asteises.local_sites_searcher.core.model.Page;
import ru.asteises.local_sites_searcher.service.PageService;
import ru.asteises.local_sites_searcher.service.SearchService;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController()
@RequestMapping("/api/page")
@AllArgsConstructor
public class PageController {

    private final SearchService searchService;
    private final PageService pageService;

    // TODO Можно сохранять данные о сайтах в БД, чтобы было проще искать по ним информацию. Правда как потом проверять, не произошли ли изменения на странице.

    @GetMapping("/all")
    public ResponseEntity<List<String>> getDataFromPage(@RequestParam List<String> urls, @RequestParam String word) throws IOException {
        // TODO приходит список сайтов и стринга которую нужно найти, для каждого сайта нужно создать свой поток поиска и сохранение в БД
        // TODO Будем возвращать список заголовков в которых нашли совпадение и ссылку на сайт
        String blogUrl = "https://bootlegbricks.ru/";
        Document doc = Jsoup.connect(blogUrl).get();
        String pageTitle = doc.title(); // Достаем тайтл страницы
        Elements paragraphs = doc.getElementsByTag("p"); // Достаем параграфы
        Elements anchors = doc.select("a"); // Достаем ссылки на странице
        return ResponseEntity.ok(anchors.stream()
                .map(anchor -> anchor.attr("href"))
                .toList());
    }

    /**
     * Метод ищет нужное словосочетание по заголовку (title) страницы сайта.
     *
     * @return возвращаем заголовок.
     */
    @PostMapping("/in_title")
    public ResponseEntity<List<Page>> getDataFromTitle(@RequestBody List<String> urls, @RequestParam String word) {
        return ResponseEntity.ok(searchService.search(urls, word));
    }

    @PostMapping("/add")
    public ResponseEntity<String> renewPagesDataBase(@RequestBody UUID webSitesId) {
        pageService.renewPagesDataBase(webSitesId);
        return ResponseEntity.ok("Everything OK");
    }
}
