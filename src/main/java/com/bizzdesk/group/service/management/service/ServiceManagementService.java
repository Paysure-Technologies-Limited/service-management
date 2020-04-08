package com.bizzdesk.group.service.management.service;

import com.bizzdesk.group.service.management.entities.ServiceCategory;
import com.bizzdesk.group.service.management.entities.Services;
import com.bizzdesk.group.service.management.mapper.ModelMapper;
import com.bizzdesk.group.service.management.repository.ServiceCategoryRepository;
import com.bizzdesk.group.service.management.repository.ServiceRepository;
import com.gotax.framework.library.entity.helpers.OrganizationHelperResponse;
import com.gotax.framework.library.entity.helpers.ServiceCategoryHelper;
import com.gotax.framework.library.entity.helpers.ServiceHelperRequest;
import com.gotax.framework.library.entity.helpers.ServiceHelperResponse;
import com.gotax.framework.library.error.handling.GoTaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceManagementService {

    Logger logger = LoggerFactory.getLogger(ServiceManagementService.class.getName());

    private ServiceRepository serviceRepository;
    private ServiceCategoryRepository serviceCategoryRepository;
    private RestTemplate restTemplate;

    @Value("${organization.service.url}")
    private String organizationServiceUrl;

    @Autowired
    public ServiceManagementService(ServiceRepository serviceRepository, ServiceCategoryRepository serviceCategoryRepository, RestTemplate restTemplate) {
        this.serviceRepository = serviceRepository;
        this.serviceCategoryRepository = serviceCategoryRepository;
        this.restTemplate = restTemplate;
    }

    public void createServiceCategory(ServiceCategoryHelper serviceCategoryHelper) throws GoTaxException {
        boolean serviceCategoryExist = serviceCategoryRepository.existsById(serviceCategoryHelper.getServiceCategoryCode());
        if(serviceCategoryExist) {
            throw new GoTaxException(MessageFormat.format("Service Category With Code {0} Already Exist", serviceCategoryHelper.getServiceCategoryCode()));
        } else {
            ServiceCategory serviceCategory = new ServiceCategory().setServiceCategoryCode(serviceCategoryHelper.getServiceCategoryCode())
                    .setCategoryDescription(serviceCategoryHelper.getCategoryDescription())
                    .setCategoryName(serviceCategoryHelper.getCategoryName())
                    .setUrlSlug(serviceCategoryHelper.getUrlSlug());
            serviceCategoryRepository.save(serviceCategory);
        }
    }

    public List<ServiceCategoryHelper> listServiceCategories() throws GoTaxException {
        List<ServiceCategoryHelper> serviceCategoryHelperList = new ArrayList<>();
        List<ServiceCategory> serviceCategoryList = serviceCategoryRepository.findAll();
        if(!serviceCategoryList.isEmpty()) {
            serviceCategoryList.forEach(serviceCategory -> {
                ServiceCategoryHelper serviceCategoryHelper = new ServiceCategoryHelper().setServiceCategoryCode(serviceCategory.getServiceCategoryCode())
                        .setCategoryDescription(serviceCategory.getCategoryDescription())
                        .setCategoryName(serviceCategory.getCategoryName())
                        .setUrlSlug(serviceCategory.getUrlSlug());
                serviceCategoryHelperList.add(serviceCategoryHelper);
            });
        } else {
            throw new GoTaxException(MessageFormat.format("No Service Category Has Been Created", null));
        }
        return serviceCategoryHelperList;
    }

    public void updateServiceCategory(ServiceCategoryHelper serviceCategoryHelper) throws GoTaxException {
        Optional<ServiceCategory> optionalServiceCategory = serviceCategoryRepository.findById(serviceCategoryHelper.getServiceCategoryCode());
        if(optionalServiceCategory.isPresent()) {
            ServiceCategory serviceCategory = optionalServiceCategory.get();
            serviceCategory.setCategoryName(serviceCategoryHelper.getCategoryName())
                    .setUrlSlug(serviceCategoryHelper.getUrlSlug());
            serviceCategoryRepository.save(serviceCategory);
        } else {
            throw new GoTaxException(MessageFormat.format("Service Category With Id {0} Does Not Exist", serviceCategoryHelper.getServiceCategoryCode()));
        }
    }

    public void createService(ServiceHelperRequest serviceHelperRequest) throws GoTaxException {
        ServiceCategory serviceCategory = serviceCategoryRepository.findById(serviceHelperRequest.getServiceCategoryCode()).orElseThrow(
                () -> new GoTaxException(MessageFormat.format("Service Category With Id {0} Does Not Exist", serviceHelperRequest.getServiceCategoryCode()))
        );

        ResponseEntity<OrganizationHelperResponse> responseEntity = this.getOrganizationById(serviceHelperRequest.getOrganisationId());
        if(responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
            Services services = new Services().setAmount(serviceHelperRequest.getAmount())
                    .setOrganisationId(serviceHelperRequest.getOrganisationId())
                    .setPercentageOfRetention(serviceHelperRequest.getPercentageOfRetention())
                    .setRetaining(serviceHelperRequest.getRetaining())
                    .setServiceCategory(serviceCategory)
                    .setServiceName(serviceHelperRequest.getServiceName());
            serviceRepository.save(services);
        } else {
            throw new GoTaxException(MessageFormat.format("Could Not Retrieve Organization Information For Id {0}", serviceHelperRequest.getOrganisationId()));
        }
    }

    public List<ServiceHelperResponse> listServices() throws GoTaxException {
        List<ServiceHelperResponse> serviceHelperResponseList = new ArrayList<>();
        List<Services> servicesList = serviceRepository.findAll();
        if(!servicesList.isEmpty()) {
            returnServicesList(serviceHelperResponseList, servicesList);
        } else {
            throw new GoTaxException(MessageFormat.format("No Service Has Been Created", null));
        }
        return serviceHelperResponseList;
    }

    public List<ServiceHelperResponse> listServicesByOrganizationId(Long organizationId) throws GoTaxException {
        List<ServiceHelperResponse> serviceHelperResponseList = new ArrayList<>();
        ResponseEntity<OrganizationHelperResponse> responseEntity = this.getOrganizationById(organizationId);
        if(responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
            List<Services> servicesList = serviceRepository.findAll();
            if (!servicesList.isEmpty()) {
                servicesList.forEach(services -> {
                    OrganizationHelperResponse organizationHelperResponse = responseEntity.getBody();
                    ServiceHelperResponse serviceHelperResponse = new ServiceHelperResponse().setServiceCategoryHelper(ModelMapper.mapServiceCategoryToHelper(services.getServiceCategory()))
                            .setServiceId(services.getServiceId())
                            .setServiceName(services.getServiceName())
                            .setAmount(services.getAmount())
                            .setOrganisationId(organizationHelperResponse)
                            .setPercentageOfRetention(services.getPercentageOfRetention())
                            .setRetaining(services.getRetaining());
                    serviceHelperResponseList.add(serviceHelperResponse);
                });
            } else {
                throw new GoTaxException(MessageFormat.format("No Service Has Been Created With Organization Id {0}", organizationId));
            }
        } else {
            throw new GoTaxException(MessageFormat.format("Organization With Id {0} Does Not Exist {0}", organizationId));
        }
        return serviceHelperResponseList;
    }

    public List<ServiceHelperResponse> listServicesByServiceCategoryCode(String serviceCategoryCode) throws GoTaxException {
        List<ServiceHelperResponse> serviceHelperResponseList = new ArrayList<>();
        ServiceCategory serviceCategory = serviceCategoryRepository.findById(serviceCategoryCode).orElseThrow(
                () -> new GoTaxException(MessageFormat.format("Service Category Code {0} Does Not Exist", serviceCategoryCode))
        );
        List<Services> servicesList = serviceRepository.findByServiceCategory(serviceCategory);
        if (!servicesList.isEmpty()) {
            returnServicesList(serviceHelperResponseList, servicesList);
        } else {
            throw new GoTaxException(MessageFormat.format("No Service Has Been Created With Service Category Code {0}", serviceCategoryCode));
        }
        return serviceHelperResponseList;
    }

    public List<ServiceHelperResponse> listServicesByRetention(boolean retentionStatus) throws GoTaxException {
        List<ServiceHelperResponse> serviceHelperResponseList = new ArrayList<>();
        List<Services> servicesList = serviceRepository.findByRetaining(retentionStatus);
        if (!servicesList.isEmpty()) {
            returnServicesList(serviceHelperResponseList, servicesList);
        } else {
            throw new GoTaxException(MessageFormat.format("No Service Has Been Created With Retaining Status {0}", retentionStatus));
        }
        return serviceHelperResponseList;
    }

    private void returnServicesList(List<ServiceHelperResponse> serviceHelperResponseList, List<Services> servicesList) {
        servicesList.forEach(services -> {
            ResponseEntity<OrganizationHelperResponse> responseEntity = this.getOrganizationById(services.getOrganisationId());
            if(responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
                OrganizationHelperResponse organizationHelperResponse = responseEntity.getBody();
                ServiceHelperResponse serviceHelperResponse = new ServiceHelperResponse().setServiceCategoryHelper(ModelMapper.mapServiceCategoryToHelper(services.getServiceCategory()))
                        .setServiceId(services.getServiceId())
                        .setServiceName(services.getServiceName())
                        .setAmount(services.getAmount())
                        .setOrganisationId(organizationHelperResponse)
                        .setPercentageOfRetention(services.getPercentageOfRetention())
                        .setRetaining(services.getRetaining());
                serviceHelperResponseList.add(serviceHelperResponse);
            }
        });
    }

    private ResponseEntity<OrganizationHelperResponse> getOrganizationById(Long organizationId) {
        ResponseEntity<OrganizationHelperResponse> responseEntity = restTemplate.exchange(organizationServiceUrl, HttpMethod.GET, null, OrganizationHelperResponse.class, organizationId);
        logger.info(MessageFormat.format("The Response Entity Object is {0}", responseEntity));
        return responseEntity;
    }

    public void updateService(Long serviceId, ServiceHelperRequest serviceHelperRequest) throws GoTaxException {
        Services services = serviceRepository.findById(serviceId).orElseThrow(
                () -> new GoTaxException(MessageFormat.format("Service with Id {0} Does Not Exist", serviceId))
        );
        services.setServiceName(serviceHelperRequest.getServiceName())
                .setRetaining(serviceHelperRequest.getRetaining());
        serviceRepository.save(services);
    }
}
