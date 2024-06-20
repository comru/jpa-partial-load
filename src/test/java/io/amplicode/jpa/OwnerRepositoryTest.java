package io.amplicode.jpa;

import jakarta.persistence.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class OwnerRepositoryTest {

	@Autowired
	private OwnerRepository ownerRepository;

	@Test
	void fullLoad() {
		doTest(ownerRepository.findAll());
	}

	@Test
	void interfaceProjection() {
		doTest(ownerRepository.findOwnerProjectionBy());
	}

	@Test
	void classProjection() {
		doTest(ownerRepository.findOwnerDtoBy());
	}

	@Test
	void queryTuple() {
		List<Tuple> tuples = ownerRepository.findAllTuple();
		doTest(tuples);
	}

	@Test
	void queryObjectArray() {
		List<Object[]> owners = ownerRepository.findAllObjectArray();
		doTest(owners);
	}

	@Test
	void queryDto() {
		List<OwnerDto> owners = ownerRepository.findAllQueryDto();
		doTest(owners);
	}

	private static void doTest(List<?> owners) {
		assertEquals(1, owners.size());
		assertNotNull(owners.get(0));
	}
}
