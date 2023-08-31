package ru.asteises.local_sites_searcher.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.asteises.local_sites_searcher.core.model.Page;
import ru.asteises.local_sites_searcher.service.PageService;
import ru.asteises.local_sites_searcher.service.SearchService;
import ru.asteises.local_sites_searcher.service.WebSiteService;

import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final WebSiteService webSiteService;
    private final PageService pageService;

    @Override
    public Set<Page> search(List<String> urls, String word) {
        return null;
    }
}
