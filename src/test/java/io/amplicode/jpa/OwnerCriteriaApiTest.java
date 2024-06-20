package io.amplicode.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class OwnerCriteriaApiTest {

	@PersistenceContext
	private EntityManager em;

	@Test
	void typeSafeTuple() {
		var cb = em.getCriteriaBuilder();
		var query = cb.createTupleQuery();
		var owner = query.from(Owner.class);
		var ownerId = owner.get(Owner_.ID);
		var ownerFirstName = owner.get(Owner_.FIRST_NAME);
		var ownerLastName = owner.get(Owner_.LAST_NAME);
		query.select(cb.tuple(ownerId, ownerFirstName, ownerLastName));
		var resultList = em.createQuery(query).getResultList();
		for (Tuple result : resultList) {
			assertEquals("George", result.get(ownerFirstName));
			assertEquals("Franklin", result.get(ownerLastName));
		}
	}

	@Test
	void dto() {
		var cb = em.getCriteriaBuilder();
		var query = cb.createQuery(OwnerDto.class);

		var owner = query.from(Owner.class);
		var ownerId = owner.get(Owner_.ID);
		var ownerFirstName = owner.get(Owner_.FIRST_NAME);
		var ownerLastName = owner.get(Owner_.LAST_NAME);
		query.multiselect(ownerId, ownerFirstName, ownerLastName);

		var resultList = em.createQuery(query).getResultList();
		for (OwnerDto ownerDto : resultList) {
			assertEquals("George", ownerDto.firstName());
			assertEquals("Franklin", ownerDto.lastName());
		}
	}

	@Test
	void arrayObject() {
		var cb = em.getCriteriaBuilder();
		var query = cb.createQuery(Object[].class);

		var owner = query.from(Owner.class);
		var ownerId = owner.get(Owner_.ID);
		var ownerFirstName = owner.get(Owner_.FIRST_NAME);
		var ownerLastName = owner.get(Owner_.LAST_NAME);
		query.multiselect(ownerId, ownerFirstName, ownerLastName);

		var resultList = em.createQuery(query).getResultList();
		for (Object[] ownerDto : resultList) {
			assertEquals("George", ownerDto[1]);
			assertEquals("Franklin", ownerDto[2]);
		}
	}
}
