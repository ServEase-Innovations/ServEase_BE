package com.springboot.app.constant;

public class CustomerConstants {

    // Private constructor to prevent instantiation
    private CustomerConstants() {
        throw new UnsupportedOperationException("This is a constants class and cannot be instantiated");
    }

    // API Operation Descriptions
    public static final String RETRIEVE_ALL_DESC = "Retrieve all customers";
    public static final String GET_BY_ID_DESC = "Get a customer by ID";
    public static final String ADD_NEW_DESC = "Add a new customer";
    public static final String UPDATE_DESC = "Update an existing customer";
    public static final String DELETE_DESC = "Delete a customer by ID";

    // Query Contants
    //public static final String GET_ALL_CUSTOMER = "FROM Customer";
    //public static final String GET_ALL_CUSTOMER_CONCERNS = "FROM CustomerConcern";
    //public static final String GET_ALL_CUSTOMER_FEEDBACK = "FROM CustomerFeedback";
    //public static final String GET_ALL_CUSTOMER_REQUESTS = "FROM CustomerRequest";
    //public static final String GET_OPEN_CUSTOMER_REQUESTS = "FROM CustomerRequest WHERE isResolved = 'NO'";
    //public static final String GET_POTENTIAL_CUSTOMERS = "FROM CustomerRequest WHERE isPotential = 'YES'";

    // Response Messages
    public static final String ADDED = "ADDED SUCCESSFULLY !";
    public static final String UPDATED = "UPDATED SUCCESSFULLY !";
    public static final String DELETED = "DELETED SUCCESSFULLY !";
    public static final String NOT_FOUND = "NOT FOUND";
<<<<<<< HEAD

    // Customer Payment
    public static final String FETCHING_PAYMENTS_FOR_CUSTOMER = "Fetching payments for customer ID: {}";
    public static final String NO_PAYMENTS_FOUND_FOR_CUSTOMER = "No payments found for customer ID: {}";
    public static final String FETCHING_PAYMENT_FOR_CUSTOMER_AND_MONTH = "Fetching payment for customer ID: {} and month: {}";
    public static final String NO_PAYMENT_FOUND_FOR_CUSTOMER_AND_MONTH = "No payment found for customer ID: {} and month: {}";
    public static final String FETCHING_PAYMENTS_BETWEEN_DATES = "Fetching payments between {} and {}";
    public static final String NO_PAYMENTS_FOUND_BETWEEN_DATES = "No payments found between {} and {}";
    public static final String NO_ENGAGEMENTS_FOUND = "No engagements found for the given criteria.";
    public static final String CALCULATING_PAYMENT = "Calculating payment for customerId: {}, baseAmount: {}, startDate: {}, endDate: {}, paymentMode: {}";
    public static final String PAYMENT_CALCULATED_SUCCESSFULLY = "Payment calculated successfully for customerId: {}. Final amount: {}";

=======
    public static final String FAILED = "FAILED";
>>>>>>> main
}
