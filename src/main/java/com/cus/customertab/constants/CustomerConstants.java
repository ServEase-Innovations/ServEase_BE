package com.cus.customertab.constants;

public class CustomerConstants {

    // API Operation Descriptions
    public static final String RETRIEVE_ALL_DESC = "Retrieve all customers";
    public static final String GET_BY_ID_DESC = "Get a customer by ID";
    public static final String ADD_NEW_DESC = "Add a new customer";
    public static final String UPDATE_DESC = "Update an existing customer";
    public static final String DELETE_DESC = "Delete a customer by ID";

    //Query Contants
    public static final String GET_ALL_CUSTOMER = "FROM Customer";
    public static final String GET_ALL_CUSTOMER_CONCERNS = "FROM CustomerConcern";
    public static final String GET_ALL_CUSTOMER_FEEDBACK = "FROM CustomerFeedback";
    public static final String GET_ALL_CUSTOMER_REQUESTS = "FROM CustomerRequest";
    public static final String GET_OPEN_CUSTOMER_REQUESTS = "FROM CustomerRequest WHERE isResolved = 'NO'";
    public static final String GET_POTENTIAL_CUSTOMERS = "FROM CustomerRequest WHERE isPotential = 'YES'";
    public static final String GET_ALL_KYC = "FROM KYC";
    public static final String FROM_CR_COMMENT = "FROM CustomerRequestComment";
    public static final String FROM_KYC_COMMENTS = "FROM KYCComments";
    public static final String LANGUAGE_FILTER = "FROM Customer WHERE languageKnown = :language";
    public static final String RATING_FILTER = "FROM Customer WHERE rating = :rating";
    public static final String GENDER_FILTER = "FROM Customer WHERE gender = :gender";

    // Response Messages
    public static final String ADDED = "ADDED SUCCESSFULLY !";
    public static final String UPDATED = "UPDATED SUCCESSFULLY !";
    public static final String DELETED = "DELETED SUCCESSFULLY !";
    public static final String NOT_FOUND = "NOT FOUND";
}
