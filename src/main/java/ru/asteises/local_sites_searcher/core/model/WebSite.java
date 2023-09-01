package ru.asteises.local_sites_searcher.core.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
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

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "webSite", cascade = CascadeType.ALL)
    private Set<Page> pages;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebSite webSite = (WebSite) o;
        return id.equals(webSite.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
