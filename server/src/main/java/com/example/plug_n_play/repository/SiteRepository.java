package com.example.plug_n_play.repository;

import com.example.plug_n_play.model.Site;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SiteRepository extends JpaRepository<Site, Long> {
}
