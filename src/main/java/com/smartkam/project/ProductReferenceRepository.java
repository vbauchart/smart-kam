package com.smartkam.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

interface ProductReferenceRepository extends JpaRepository<ProductReference, Long> {

    @Query("SELECT pr FROM ProductReference pr JOIN FETCH pr.family WHERE pr.id = :id")
    Optional<ProductReference> findByIdWithFamily(@Param("id") Long id);
}
