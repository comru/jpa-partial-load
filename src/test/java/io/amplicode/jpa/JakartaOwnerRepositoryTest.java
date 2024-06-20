package io.amplicode.jpa;

import jakarta.persistence.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class JakartaOwnerRepositoryTest {

    @Autowired
    private JakartaOwnerRepository ownerRepository;

    @Test
    void fullLoad() {
        doTest(ownerRepository.findAll().toList());
    }

    @Test
    void constructorDto() {
        doTest(ownerRepository.findOwnerDtoAll());
    }

    @Test
    void objectArray() {
        List<Object[]> results = ownerRepository.findObjectArrayAll();

        assertEquals(1, results.size());
        for (Object[] result : results) {
            assertEquals(3, result.length);
            assertEquals("George", result[1]);
            assertEquals("Franklin", result[2]);
        }
    }

    @Test
    void tuple() {
        List<Tuple> results = ownerRepository.findTupleAll();

        assertEquals(1, results.size());
        for (Tuple result : results) {
            assertEquals(3, result.getElements().size());
            assertEquals("George", result.get(Owner_.FIRST_NAME));
            assertEquals("Franklin", result.get(Owner_.LAST_NAME));
        }
    }

    private static void doTest(List<?> owners) {
        assertEquals(1, owners.size());
        assertNotNull(owners.get(0));
    }
}