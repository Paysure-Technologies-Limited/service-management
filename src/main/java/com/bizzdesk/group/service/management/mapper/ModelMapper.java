package com.bizzdesk.group.service.management.mapper;

import com.bizzdesk.group.service.management.entities.ServiceCategory;
import com.bizzdesk.group.service.management.entities.Services;
import com.gotax.framework.library.entity.helpers.OrganizationHelperResponse;
import com.gotax.framework.library.entity.helpers.ServiceCategoryHelper;
import com.gotax.framework.library.entity.helpers.ServiceHelperResponse;

public class ModelMapper {

    public static ServiceHelperResponse mapServiceToResponse(Services services, OrganizationHelperResponse organizationHelperResponse) {
        return new ServiceHelperResponse().setAmount(services.getAmount())
                .setOrganisationId(organizationHelperResponse)
                .setPercentageOfRetention(services.getPercentageOfRetention())
                .setRetaining(services.getRetaining())
                .setServiceCategoryHelper(mapServiceCategoryToHelper(services.getServiceCategory()))
                .setServiceId(services.getServiceId())
                .setServiceName(services.getServiceName());
    }

    public static ServiceCategoryHelper mapServiceCategoryToHelper(ServiceCategory serviceCategory) {
        return new ServiceCategoryHelper().setCategoryDescription(serviceCategory.getCategoryDescription())
                .setCategoryName(serviceCategory.getCategoryName())
                .setServiceCategoryCode(serviceCategory.getServiceCategoryCode())
                .setUrlSlug(serviceCategory.getUrlSlug());
    }
}
