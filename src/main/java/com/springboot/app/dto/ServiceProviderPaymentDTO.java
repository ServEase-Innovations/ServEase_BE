package com.springboot.app.dto;

import java.sql.Date;
import java.sql.Timestamp;

import com.springboot.app.enums.Currency;
import com.springboot.app.enums.PaymentMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProviderPaymentDTO {

    private Long id;
    private Long serviceProviderId;
    private Long customerId;
    private Date startDate;
    private Date endDate;
    private Timestamp settledOn;
    private PaymentMode paymentMode;
    private Date paymentOn;
    private String transactionId;
    private double noOfDays;
    private int amount;
    private Currency currency;
    private String upiId;
    private int month;
    private int year;
    private int monthlyAmount;

}
