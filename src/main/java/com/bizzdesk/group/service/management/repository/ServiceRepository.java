package com.bizzdesk.group.service.management.repository;

import com.bizzdesk.group.service.management.entities.ServiceCategory;
import com.bizzdesk.group.service.management.entities.Services;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Services, Long> {

    List<Services> findByServiceCategory(ServiceCategory serviceCategory);
    List<Services> findByRetaining(boolean retaining);
}
