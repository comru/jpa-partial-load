package io.amplicode.jpa;

import jakarta.data.repository.*;
import jakarta.persistence.Tuple;

import java.util.List;

@Repository
public interface JakartaOwnerRepository extends CrudRepository<Owner, Long> {

    @Query("select id, firstName, lastName from Owner")
    List<OwnerDto> findOwnerDtoAll();

    @Query("select id, firstName, lastName from Owner")
    List<Object[]> findObjectArrayAll();

    @Query("select o.id, o.firstName as firstName, o.lastName as lastName from Owner o")
    List<Tuple> findTupleAll();
}
