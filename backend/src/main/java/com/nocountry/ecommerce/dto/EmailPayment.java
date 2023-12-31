package com.nocountry.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailPayment {
    private String mailFrom;
    private String mailTo;
    private String subject;
    private String fullName;
    private String currencyCode;
    private double total;
}
