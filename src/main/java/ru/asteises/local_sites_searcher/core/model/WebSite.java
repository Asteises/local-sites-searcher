package ru.asteises.local_sites_searcher.core.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "web_site")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WebSite {

    @Id
    private UUID id;

    private String name;

    private String theme;

    @OneToMany(mappedBy = "webSiteId", cascade = CascadeType.ALL)
    @JoinColumn(name = "web_site_id")
    private Set<Page> pagesId;
}
