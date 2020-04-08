package com.bizzdesk.group.service.management.repository;

import com.bizzdesk.group.service.management.entities.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory, String> {
}
