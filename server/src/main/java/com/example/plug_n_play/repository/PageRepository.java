package com.example.plug_n_play.repository;


import com.example.plug_n_play.model.Page;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PageRepository extends JpaRepository<Page, Long> {
}
