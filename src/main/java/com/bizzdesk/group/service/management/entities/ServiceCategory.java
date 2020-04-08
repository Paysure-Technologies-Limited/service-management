package com.bizzdesk.group.service.management.entities;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Accessors(chain = true)
@Entity
public class ServiceCategory {

    @Id
    private String serviceCategoryCode;
    private String categoryName;
    private String categoryDescription;
    private String urlSlug;
}
