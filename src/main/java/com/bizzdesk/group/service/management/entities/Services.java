package com.bizzdesk.group.service.management.entities;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Accessors(chain = true)
@Entity
public class Services {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long serviceId;
    private BigDecimal amount;
    @OneToOne
    @JoinColumn
    private ServiceCategory serviceCategory;
    private String serviceName;
    private Boolean retaining;
    private BigDecimal percentageOfRetention;
    private Long organisationId;
}
