package com.bizzdesk.group.service.management.controller;

import com.bizzdesk.group.service.management.service.ServiceManagementService;
import com.gotax.framework.library.entity.helpers.ServiceCategoryHelper;
import com.gotax.framework.library.entity.helpers.ServiceHelperRequest;
import com.gotax.framework.library.entity.helpers.ServiceHelperResponse;
import com.gotax.framework.library.error.handling.GoTaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ServiceManagementController {

    private ServiceManagementService serviceManagementService;

    @Autowired
    public ServiceManagementController(ServiceManagementService serviceManagementService) {
        this.serviceManagementService = serviceManagementService;
    }

    @PostMapping("/v1/create/service/category")
    public void createServiceCategory(@RequestBody ServiceCategoryHelper serviceCategoryHelper) throws GoTaxException {
        serviceManagementService.createServiceCategory(serviceCategoryHelper);
    }

    @GetMapping("/v1/list/service/category")
    public List<ServiceCategoryHelper> listServiceCategories() throws GoTaxException {
        return serviceManagementService.listServiceCategories();
    }

    @PutMapping("/v1/update/service/category")
    public void updateServiceCategory(@RequestBody ServiceCategoryHelper serviceCategoryHelper) throws GoTaxException {
        serviceManagementService.updateServiceCategory(serviceCategoryHelper);
    }

    @PostMapping("/v1/create/service")
    public void createService(@RequestBody ServiceHelperRequest serviceHelperRequest) throws GoTaxException {
        serviceManagementService.createService(serviceHelperRequest);
    }

    @GetMapping("/v1/list/services")
    public List<ServiceHelperResponse> listServices() throws GoTaxException {
        return serviceManagementService.listServices();
    }

    @PostMapping("/v1/list/services/organisation/id")
    public List<ServiceHelperResponse> listServicesByOrganizationId(@RequestParam("organizationId") Long organizationId) throws GoTaxException {
        return serviceManagementService.listServicesByOrganizationId(organizationId);
    }

    @GetMapping("/v1/list/services/category/code")
    public List<ServiceHelperResponse> listServicesByServiceCategoryCode(@RequestParam("serviceCategoryCode") String serviceCategoryCode) throws GoTaxException {
        return serviceManagementService.listServicesByServiceCategoryCode(serviceCategoryCode);
    }

    @GetMapping("/v1/list/services/retention")
    public List<ServiceHelperResponse> listServicesByRetention(boolean retentionStatus) throws GoTaxException {
        return serviceManagementService.listServicesByRetention(retentionStatus);
    }

    @PutMapping("/v1/update/service")
    public void updateService(@RequestParam("serviceId") Long serviceId, @RequestBody ServiceHelperRequest serviceHelperRequest) throws GoTaxException {
        serviceManagementService.updateService(serviceId, serviceHelperRequest);
    }
}
