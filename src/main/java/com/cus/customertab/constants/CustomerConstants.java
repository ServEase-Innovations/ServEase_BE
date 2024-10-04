package com.cus.customertab.constants;

public class CustomerConstants {

    // API Operation Descriptions
    public static final String RETRIEVE_ALL_DESC = "Retrieve all customers";
    public static final String GET_BY_ID_DESC = "Get a customer by ID";
    public static final String ADD_NEW_DESC = "Add a new customer";
    public static final String UPDATE_DESC = "Update an existing customer";
    public static final String DELETE_DESC = "Delete a customer by ID";

    //Query Contants
    public static final String GET_ALL_CUSTOMER_REQUESTS = "FROM CustomerRequest";
    public static final String GET_OPEN_CUSTOMER_REQUESTS = "FROM CustomerRequest WHERE isResolved = 'NO'";
    public static final String GET_POTENTIAL_CUSTOMERS = "FROM CustomerRequest WHERE isPotential = 'YES'";

    // Response Messages
    public static final String CUSTOMER_ADDED = "Customer added successfully!";
    public static final String CUSTOMER_UPDATED = "Customer updated successfully!";
    public static final String CUSTOMER_DELETED = "Customer deleted successfully!";
    public static final String CUSTOMER_NOT_FOUND = "Customer not found";

    public static final String CUSTOMER_REQUEST_ADDED = "Customer request added successfully!";
    public static final String CUSTOMER_REQUEST_UPDATED = "Customer request updated successfully!";

    public static final String CUSTOMER_CONCERN_ADDED = "Customer concern added successfully!";
    public static final String CUSTOMER_CONCERN_UPDATED = "Customer concern updated successfully!";
    public static final String CUSTOMER_CONCERN_DELETED = "Customer concern deleted successfully!";
}
