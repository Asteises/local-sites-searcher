package ru.asteises.local_sites_searcher.service.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import ru.asteises.local_sites_searcher.core.model.Page;
import ru.asteises.local_sites_searcher.core.model.WebSite;
import ru.asteises.local_sites_searcher.repo.PageStorage;
import ru.asteises.local_sites_searcher.service.PageService;
import ru.asteises.local_sites_searcher.service.WebSiteService;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Getter
@Slf4j
public class

PageServiceImpl implements PageService {

    private final WebSiteService webSiteService;
    private final PageStorage pageStorage;

    @Override
    public void renewPagesDataBase(UUID webSitesId) {
        WebSite webSite = getWebSiteById(webSitesId);
        List<Page> pages = getPages(List.of(webSite.getName()), webSite).stream().toList();
    }

    @Override
    public Set<Page> getPages(List<String> anchors, WebSite webSite) {
        log.info("income anchors: " + anchors.toString());
        Set<Page> pages = new HashSet<>();
        if (!anchors.isEmpty()) {
            for (String anchor: anchors) {
                if (!checkPageExist(anchor)) {
                    Page page = getPage(anchor, webSite);
                    if (page == null) {
                        continue;
                    }
                    pages.add(page);
                }
            }
        }
        return pages;
    }

    @Override
    public Page getPage(String anchor, WebSite webSite) {
        Document document = getConnection(anchor); // Берем страницу сайта
        if (document == null) {
            return null;
        }
        Page newPage = parseData(document, webSite, anchor); // Создаем новый Pager
        pageStorage.save(newPage); // Сохраняем Page в БД
        List<String> newAnchors = new ArrayList<>(getAnchors(document, webSite.getName())); // Собираем новые анкоры на странице
        log.info("new anchors: " + newAnchors);
        webSite.getPages().add(newPage); // Добавляем Page к страницам сайта
        webSiteService.saveWebSite(webSite); // Обновляем данные сайта в БД
        if (!newAnchors.isEmpty()) {
            getPages(newAnchors, webSite); // Если на странице есть еще анкоры, то отправляем в рекурсию
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
        Connection.Response response = null;
        try {
            response = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("https://www.google.com")
                    .execute();
            if (response.statusCode() == 200) {
                document = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("https://www.google.com")
                        .get();
            } else {
                return null;
            }
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
        anchors.removeIf(a -> a.contains(".png"));
        anchors.removeIf(a -> a.contains(".ico"));
        anchors.removeIf(a -> a.contains(".json"));
        anchors.removeIf(a -> a.contains(".?"));
        return anchors;
    }

    @Override
    public void savePages(Set<Page> pages) {
        List<Page> newPages = pages.stream().toList();
        pageStorage.saveAll(newPages);
    }

    private WebSite getWebSiteById(UUID webSiteId) {
        return webSiteService.getWebSiteById(webSiteId);
    }

    private boolean checkPageExist(String pagePath) {
        return pageStorage.findByPath(pagePath).isPresent();
    }
}
