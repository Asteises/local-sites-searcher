package ru.asteises.local_sites_searcher.service;

import ru.asteises.local_sites_searcher.core.model.Page;

import java.util.List;

public interface SearchService {

    List<Page> search(List<String> urls, String word);
}
