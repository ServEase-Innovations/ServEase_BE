package com.springboot.app.listener;

import com.springboot.app.entity.ServiceProvider;
import com.springboot.app.entity.ServiceProviderPayment;
import jakarta.persistence.*;

public class ServiceProviderPaymentListener {

    @PostUpdate
    public void onUpdate(ServiceProviderPayment serviceProviderPayment) {
        ServiceProvider serviceProvider = serviceProviderPayment.getServiceProvider();
        if (serviceProvider != null) {
            serviceProvider.setExpectedSalary(serviceProviderPayment.getAmount());
        }

    }

    @PostPersist
    public void onPersist(ServiceProviderPayment serviceProviderPayment) {
        ServiceProvider serviceProvider = serviceProviderPayment.getServiceProvider();
        if (serviceProvider != null) {
            serviceProvider.setExpectedSalary(serviceProviderPayment.getAmount());
        }
    }
}
