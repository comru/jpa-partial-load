package io.amplicode.jpa;

import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OwnerRepository extends JpaRepository<Owner, Long> {

    List<OwnerProjection> findOwnerProjectionBy();

    List<OwnerDto> findOwnerDtoBy();

    @Query("select id, firstName, lastName from Owner")
    List<Tuple> findAllTuple();

    @Query("select id, firstName, lastName from Owner")
    List<Object[]> findAllObjectArray();

    @Query("select new io.amplicode.jpa.OwnerDto(id, firstName, lastName) from Owner")
    List<OwnerDto> findAllQueryDto();
}