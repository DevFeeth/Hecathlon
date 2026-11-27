package com.spring.henallux.hecathlon.dataAccess.repository;

import com.spring.henallux.hecathlon.dataAccess.entity.ProductEntity;
import com.spring.henallux.hecathlon.dataAccess.entity.ProductTypeEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, String>, JpaSpecificationExecutor<ProductEntity> {
    List<ProductEntity> findByFeaturedTrueOrderByPosition();

    List<ProductEntity> findByHighlightedTrueOrderByPosition();

    List<ProductEntity> findBySportIdAndIdNotIn(String sportId, Collection<String> excludedIds, Pageable pageable);

    @Query("select p.sport.id, count(p) from ProductEntity p group by p.sport.id")
    List<Object[]> countBySport();

    @Query("select distinct t from ProductEntity p join p.type t where p.sport.id = :sportId order by t.position")
    List<ProductTypeEntity> findTypesOfSport(@Param("sportId") String sportId);
}
