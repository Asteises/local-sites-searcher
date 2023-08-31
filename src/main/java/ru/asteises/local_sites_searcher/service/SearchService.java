package ru.asteises.local_sites_searcher.service;

import ru.asteises.local_sites_searcher.core.model.Page;

import java.util.List;
import java.util.Set;

public interface SearchService {

    Set<Page> search(List<String> urls, String word);
}
