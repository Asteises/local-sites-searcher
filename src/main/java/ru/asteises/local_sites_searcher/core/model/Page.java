package ru.asteises.local_sites_searcher.core.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "page")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Page {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "path")
    private String path;

    @Column(name = "code")
    private int code;

    @Column(name = "content")
    private String content;

    @ManyToOne
    @JoinColumn(name = "web_site_id", nullable = false)
    private WebSite webSite;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Page page = (Page) o;
        return id.equals(page.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
