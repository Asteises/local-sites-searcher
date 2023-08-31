package ru.asteises.local_sites_searcher.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.asteises.local_sites_searcher.core.model.WebSite;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WebSiteStorage extends JpaRepository<WebSite, UUID> {

    Optional<WebSite> findByName(String name);
}
