package com.example.plug_n_play.repository;

import com.example.plug_n_play.model.Site;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SiteRepository extends JpaRepository<Site, Long> {
    Optional<Site> getSiteByUrlContaining(String url);
}
