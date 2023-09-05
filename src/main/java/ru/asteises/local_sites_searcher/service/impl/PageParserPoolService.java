package ru.asteises.local_sites_searcher.service.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import ru.asteises.local_sites_searcher.core.model.Page;
import ru.asteises.local_sites_searcher.core.model.WebSite;
import ru.asteises.local_sites_searcher.repo.PageStorage;
import ru.asteises.local_sites_searcher.service.PageParseService;
import ru.asteises.local_sites_searcher.service.WebSiteConnect;
import ru.asteises.local_sites_searcher.service.WebSiteService;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

@Slf4j
@Service
@Getter
@AllArgsConstructor
public class PageParserPoolService extends RecursiveTask<List<Page>> implements PageParseService, WebSiteConnect {

    private final Set<String> anchors;
    private final WebSite webSite;

    private final WebSiteService webSiteService;
    private final PageStorage pageStorage;

    @Override
    protected List<Page> compute() {
        if (getAnchors().isEmpty()) {
            return getWebSite().getPages().stream().toList();
        }
        PageParserPoolService poolService = new PageParserPoolService(
                getAnchors(),
                getWebSite(),
                webSiteService,
                pageStorage);
        poolService.fork();
        return poolService.join();
    }

    @Override
    public Set<Page> getPages(Set<String> anchors, WebSite webSite) {
        log.info("income anchors: " + anchors.toString());
        Set<Page> newPages = new HashSet<>();
        for (String anchor : anchors) {
            Page newPage = getPage(anchor, webSite);
            newPages.add(newPage);
        }
        webSite.setPages(newPages);
        savePages(newPages);
        webSiteService.saveWebSite(webSite);
        return new HashSet<>(webSite.getPages());
    }

    @Override
    public Page getPage(String anchor, WebSite webSite) {
        Document document = getConnection(anchor);
        Page newPage = parseData(document, webSite, anchor);
        pageStorage.save(newPage);
        Set<String> newAnchors = new HashSet<>(getAnchors(document, webSite.getName()));
        log.info("new anchors: " + newAnchors);
        webSite.getPages().add(newPage);
        Set<Page> pages = webSite.getPages();
        List<String> onSiteAnchors = pages.stream().map(Page::getPath).toList();
        log.info("total site anchors: " + onSiteAnchors.toString());
        newAnchors.removeAll(new HashSet<>(onSiteAnchors));
        log.info("new anchors after remove on site anchors: " + newAnchors);
        if (!newAnchors.isEmpty()) {
            getPages(newAnchors, webSite);
        }
        return newPage;
    }

    /**
     * Метод создающий соединение со страницей в новом потоке.
     * Создает и запускает новый поток.
     *
     * @param url - входящий адрес страницы.
     * @return - возвращаем Document JSOUP.
     */
    @Override
    public Document getConnection(String url) {
        Document document = null;
        try {
            document = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return document;
    }

    /**
     * Метод принимает Document JSOUP и парсит данные.
     *
     * @param document
     * @return
     */
    @Override
    public Page parseData(Document document, WebSite webSite, String anchor) {
        Page page = new Page();
        page.setId(UUID.randomUUID());
        page.setPath(anchor);
        log.info("Base uri: " + document.baseUri());
        page.setCode(document.connection().response().statusCode());
        page.setContent(document.text());
        page.setWebSite(webSite);
        return page;
    }

    /**
     * Метод парсит ссылки из документа и составляет список тех, которых еще нет в БД.
     * Парсит только ссылки содержащие название сайта.
     *
     * @param document
     * @return
     */
    @Override
    public List<String> getAnchors(Document document, String siteAnchor) {
        List<String> anchors;
        Elements tempAnchors = document.getAllElements();
        anchors = tempAnchors.stream()
                .map(a -> a.absUrl("href"))
                .collect(Collectors.toList());
        anchors.removeIf(newAnchor -> !newAnchor.contains(siteAnchor));
        anchors.removeIf(a -> a.contains("png"));
        return anchors;
    }

    @Override
    public List<String> getAnchors(String url) {
        Document document = getConnection(url);
        Elements anchors = document.select("a");
        return anchors.stream()
                .map(anchor -> anchor.attr("href"))
                .toList();
    }

    /**
     * Метод проверяет наличие страницы в БД.
     *
     * @param url - принимает url страницы.
     * @return возвращает true если страница уже есть в БД.
     */
    @Override
    public boolean checkPageExist(String url) {
        Optional<Page> page = pageStorage.findByPath(url);
        return page.isPresent();
    }

    @Override
    public List<Page> savePages(Set<Page> pages) {
        List<Page> newPages = pages.stream().toList();
        pageStorage.saveAll(newPages);
        return newPages;
    }
}
