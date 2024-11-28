package com.springboot.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShortListedServiceProviderDTO {

    private Long id;
    private Long customerId;
    private String serviceProviderIdList;

}
