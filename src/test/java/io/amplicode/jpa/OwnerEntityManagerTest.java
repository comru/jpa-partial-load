package io.amplicode.jpa;

import io.amplicode.jpa.projection.OwnerDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SuppressWarnings("unchecked")
@SpringBootTest
class OwnerEntityManagerTest {

	@PersistenceContext
	private EntityManager em;

	@Test
	void constructorExpr() {
		List<OwnerDto> results = em.createQuery(
						"select new io.amplicode.jpa.OwnerDto(id, firstName, lastName) from Owner", OwnerDto.class)
				.getResultList();

		assertEquals(1, results.size());
		for (OwnerDto result : results) {
			assertEquals("George", result.firstName());
			assertEquals("Franklin", result.lastName());
		}
	}

	@Test
	void constructorExprSimple() {
		List<OwnerDto> results = em.createQuery(
						"select id, firstName, lastName from Owner", OwnerDto.class)
				.getResultList();

		assertEquals(1, results.size());
		for (OwnerDto result : results) {
			assertEquals("George", result.firstName());
			assertEquals("Franklin", result.lastName());
		}
	}

	@Test
	void objectArray() {
		List<Object[]> results = em.createQuery("select id, firstName, lastName from Owner", Object[].class)
				.getResultList();

		assertEquals(1, results.size());
		for (Object[] result : results) {
			assertEquals(3, result.length);
			assertEquals("George", result[1]);
			assertEquals("Franklin", result[2]);
		}
	}

	@Test
	void tuple() {
		List<Tuple> results = em.createQuery(
				"select o.id, o.firstName as firstName, o.lastName as lastName from Owner o", Tuple.class)
				.getResultList();

		assertEquals(1, results.size());
		for (Tuple result : results) {
			assertEquals(3, result.getElements().size());
			assertEquals("George", result.get("firstName", String.class));
			assertEquals("Franklin", result.get("lastName", String.class));
		}
	}

	@Test
	void nativeQueryTuple() {
		List<Tuple> results = em.createNativeQuery(
						"select o.id, o.first_name as firstName, o.last_name as lastName from owners o", Tuple.class)
				.getResultList();

		assertEquals(1, results.size());
		for (Tuple result : results) {
			assertEquals(3, result.getElements().size());
			assertEquals("George", result.get("firstName", String.class));
			assertEquals("Franklin", result.get("lastName", String.class));
		}
	}

	@Test
	void resultTransformer() {
		List<OwnerDto> results = em.createQuery("select o.id as id, o.firstName as firstName, o.lastName as lastName from Owner o")
				.unwrap(org.hibernate.query.Query.class)
				.setTupleTransformer((tuple, aliases)
						-> new OwnerDto((Long) tuple[0], (String) tuple[1], (String) tuple[2]))
				.getResultList();

		assertEquals(1, results.size());
		for (OwnerDto result : results) {
			assertEquals("George", result.firstName());
			assertEquals("Franklin", result.lastName());
		}
	}
}
