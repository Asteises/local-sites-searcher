package ru.asteises.local_sites_searcher.service.impl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import ru.asteises.local_sites_searcher.service.PageService;
import ru.asteises.local_sites_searcher.service.WebSiteConnect;

import java.io.IOException;

@Service
public class PageServiceImpl implements PageService, WebSiteConnect {

    // TODO Написать классы сущностей, которые будет хранить данные 1 сайта - 1 страницы
    @Override
    public String searchInTitle(String word) throws IOException {
        String url = "https://bootlegbricks.ru/";
        Document document = getConnection(url);
        int code = document.connection().response().statusCode();
        System.out.println(code);
        return getConnection(url).title();
    }

    @Override
    public Document getConnection(String url) throws IOException {
        return Jsoup.connect(url).get();
    }
}
