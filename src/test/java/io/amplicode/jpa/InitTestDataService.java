package io.amplicode.jpa;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class InitTestDataService {

    @Autowired
    private OwnerRepository ownerRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setLastName("Franklin");
        owner.setAddress("110 W. Liberty St.");
        owner.setCity("Madison");
        owner.setTelephone("6085551023");
        ownerRepository.save(owner);
    }

    @PreDestroy
    public void destroy() {
        ownerRepository.deleteAll();
    }
}
