package ru.asteises.local_sites_searcher.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.asteises.local_sites_searcher.core.model.Page;

import java.util.UUID;

@Repository
public interface PageStorage extends JpaRepository<Page, UUID> {
}
