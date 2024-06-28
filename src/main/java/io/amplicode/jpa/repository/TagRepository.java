package io.amplicode.jpa.repository;

import io.amplicode.jpa.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}