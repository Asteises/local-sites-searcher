package ru.asteises.local_sites_searcher.controller;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController()
@RequestMapping("/api/search")
public class PageController {

    @GetMapping("/all")
    public ResponseEntity<List<String>> getDataFromPage(@RequestParam List<String> urls, @RequestParam String word) throws IOException {
        // TODO приходит список сайтов и стринга которую нужно найти, для каждого сайта нужно создать свой поток поиска и сохранение в БД
        // TODO Будем возвращать список заголовков в которых нашли совпадение и ссылку на сайт
        String blogUrl = "https://bootlegbricks.ru/";
        Document doc = Jsoup.connect(blogUrl).get();
        String pageTitle = doc.title();
        Elements paragraphs = doc.getElementsByTag("p");
        Elements anchors = doc.select("a");
        return ResponseEntity.ok(anchors.stream()
                .map(anchor -> anchor.attr("href"))
                .toList());
    }

    @GetMapping("/title")
    public ResponseEntity<String> getDataFromTitle() throws IOException {
        // TODO Сделать подключение к сайтам отдельным методомо
        String blogUrl = "https://bootlegbricks.ru/";
        Document doc = Jsoup.connect(blogUrl).get();
        int code = doc.connection().response().statusCode();
        System.out.println(code);
        return ResponseEntity.ok(doc.title());
    }
}
