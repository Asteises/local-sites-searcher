package ru.asteises.local_sites_searcher.service.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.asteises.local_sites_searcher.core.model.Page;
import ru.asteises.local_sites_searcher.core.model.WebSite;
import ru.asteises.local_sites_searcher.repo.PageStorage;
import ru.asteises.local_sites_searcher.service.PageParseService;
import ru.asteises.local_sites_searcher.service.PageService;
import ru.asteises.local_sites_searcher.service.WebSiteService;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;

@Service
@AllArgsConstructor
@Getter
@Slf4j
public class

PageServiceImpl implements PageService {

    private final WebSiteService webSiteService;
    private final PageStorage pageStorage;

    @Override
    public List<Page> renewPagesDataBase(UUID webSitesId) {
        WebSite webSite = getWebSiteById(webSitesId);
        ForkJoinPool pool = ForkJoinPool.commonPool();
        PageParserPoolService parserPool = new PageParserPoolService(
                Set.of(webSite.getName()),
                webSite,
                getWebSiteService(),
                getPageStorage());
        return pool.invoke(parserPool);
    }

    public WebSite getWebSiteById(UUID webSiteId) {
        return webSiteService.getWebSiteById(webSiteId);
    }
}
