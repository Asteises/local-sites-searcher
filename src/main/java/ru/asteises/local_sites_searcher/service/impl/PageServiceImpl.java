package ru.asteises.local_sites_searcher.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import ru.asteises.local_sites_searcher.core.PageConnectThread;
import ru.asteises.local_sites_searcher.core.model.Page;
import ru.asteises.local_sites_searcher.core.model.WebSite;
import ru.asteises.local_sites_searcher.repo.PageStorage;
import ru.asteises.local_sites_searcher.service.PageService;
import ru.asteises.local_sites_searcher.service.WebSiteConnect;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class PageServiceImpl implements PageService, WebSiteConnect {

    private final PageStorage pageStorage;

    /**
     * Метод принимает список новых URL сайтов, которых точно нет в БД. Разбивает на отдельные записи,
     * и передает их по одному в getConnection().
     *
     * @param newWebSites - список входящих URL.
     * @return
     */
    @Override
    public Set<Page> incomeNewPages(Set<WebSite> newWebSites) {
        Set<String> newStartPages = newWebSites.stream()
                .map(WebSite::getName)
                .collect(Collectors.toSet());
        // TODO Для каждого нового сайта нужно запустить свой поток!
        return getData(newStartPages);
    }

    /**
     * @param urls
     * @return
     */
    public Set<Page> getData(Set<String> urls) {
        Set<Page> pages = new HashSet<>();
        for (String url : urls) {

            if (checkPageExist(url)) {
                log.debug("URL already exist");
                continue;
            }
            Document document = getConnection(url);
            Page page = parseData(document);
            savePage(page);
            pages.add(page);
            Set<String> anchors = getAnchors(document);
            if (anchors != null) {
                getData(anchors);
            }
        }
        return pages;
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
        PageConnectThread pageConnectThread = PageConnectThread.createAndStart(url);
        try {
            pageConnectThread.getThrd().join();
            // pageConnectThread.getThrd().interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return pageConnectThread.getDocument();
    }

    /**
     * Метод принимает Document JSOUP и парсит данные.
     *
     * @param document
     * @return
     */
    @Override
    public Page parseData(Document document) {
        Page page = new Page();
        page.setId(UUID.randomUUID());
        page.setPath(document.baseUri());
        page.setCode(document.connection().response().statusCode());
        page.setContent(document.text());
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
    public Set<String> getAnchors(Document document) {
        Elements tempAnchors = document.select("a");
        return tempAnchors.stream()
                .filter(element -> element.baseUri().contains(document.baseUri()))
                .map(anchor -> tempAnchors.attr("href"))
                .collect(Collectors.toSet());
    }

    /**
     * Метод проверяет наличие страницы в БД.
     *
     * @param url - принимает url страницы.
     * @return возвращает true если страница уже есть в БД.
     */
    public boolean checkPageExist(String url) {
        Optional<Page> page = pageStorage.findByPath(url);
        return page.isPresent();
    }


    /**
     * Метод принимает Document JSOUP и сохраняет его в БД.
     *
     * @param page - принимаем Document JSOUP.
     * @return - возвращаем Page.
     */
    @Override
    public Page savePage(Page page) {
        pageStorage.save(page);
        return page;
    }
}
