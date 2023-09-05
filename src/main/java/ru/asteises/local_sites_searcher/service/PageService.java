package ru.asteises.local_sites_searcher.service;

import ru.asteises.local_sites_searcher.core.model.Page;

import java.util.List;
import java.util.UUID;

public interface PageService {

    List<Page> renewPagesDataBase(UUID webSitesId);
}
