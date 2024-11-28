package com.springboot.app.constant;

public class ServiceProviderConstants {

    // API Path Constants
    public static final String BASE_API_PATH = "/api/serviceproviders";

    // API value
    public static final String API_VALUE = "Service Provider Request API";
    // Tag for the API
    public static final String TAG_SERVICEPROVIDERS = "Service Provider Requests";

    // Constants for service provider
    public static final String RETRIEVE_ALL_DESC = "Retrieve all customers";
    public static final String GET_BY_ID_DESC = "Get a customer by ID";
    public static final String ADD_NEW_DESC = "Add a new customer";
    public static final String UPDATE_DESC = "Update an existing customer";
    public static final String DELETE_DESC = "Delete a customer by ID";

    // Constants for service provider Request
    public static final String DESC_RETRIEVE_ALL_SERVICE_PROVIDER_REQUESTS = "Retrieve all service provider requests";
    public static final String DESC_ADD_NEW_SERVICE_PROVIDER_REQUEST = "Add a new service provider request";
    public static final String DESC_UPDATE_SERVICE_PROVIDER_REQUEST = "Update an existing service provider request";
    public static final String DESC_DELETE_SERVICE_PROVIDER_REQUEST = "Mark a service provider request as resolved";
    public static final String DESC_GET_SERVICE_PROVIDER_REQUEST_BY_ID = "Retrieve  service provider requests by ID";

    // Constants for service provider feedback
    public static final String DESC_RETRIEVE_ALL_FEEDBACKS = "Retrieve all service provider feedbacks";
    public static final String DESC_GET_FEEDBACK_BY_ID = "Get feedback by ID";
    public static final String DESC_ADD_NEW_FEEDBACK = "Add new service provider feedback";
    public static final String DESC_UPDATE_FEEDBACK = "Update existing service provider feedback";
    public static final String DESC_DELETE_FEEDBACK = "Delete service provider feedback";

    // Constants for service provider comments
    public static final String RETRIEVE_ALL_COMMENTS_DESC = "Fetch all comments related to service provider requests.";
    public static final String GET_COMMENT_BY_ID_DESC = "Retrieve a specific comment by its ID.";
    public static final String ADD_NEW_COMMENT_DESC = "Submit a new comment for a service provider request.";
    public static final String UPDATE_COMMENT_DESC = "Modify an existing comment for a service provider request.";
    public static final String DELETE_COMMENT_DESC = "Remove a comment associated with a service provider request.";

    // Response Messages for Service provider
    public static final String SERVICE_PROVIDER_ADDED = "Service provider added successfully!";
    public static final String SERVICE_PROVIDER_UPDATED = "Service provider updated successfully!";
    public static final String SERVICE_PROVIDER_DELETED = "Service provider deleted successfully!";

    // Response Messages for Service Provider Request
    public static final String SERVICE_PROVIDER_REQUEST_ADDED = "Service provider request added successfully";
    public static final String SERVICE_PROVIDER_REQUEST_UPDATED = "Service provider request updated successfully";
    public static final String SERVICE_PROVIDER_REQUEST_DELETED = "Service provider request deleted successfully";

    // Response Messages for Feedback
    public static final String FEEDBACK_ADDED = "Feedback added successfully";
    public static final String FEEDBACK_UPDATED = "Feedback updated successfully";
    public static final String FEEDBACK_DELETED = "Feedback deleted successfully";

    // Response Messages for comments
    public static final String COMMENT_ADDED_SUCCESS = "Comment added successfully for the service provider request.";
    public static final String COMMENT_UPDATED_SUCCESS = "Comment updated successfully for the service provider request.";
    public static final String COMMENT_DELETED_SUCCESS = "Comment deleted successfully for the service provider request.";

    // Error messages
    public static final String SERVICE_PROVIDER_NOT_FOUND = "ServiceProvider not found with id: ";
    public static final String SERVICE_PROVIDER_REQUEST_NOT_FOUND = "ServiceProviderRequest not found with id: ";
    public static final String FEEDBACK_NOT_FOUND = "ServiceProviderFeedback not found with id: ";

    // Error messages for customer not found
    public static final String CUSTOMER_NOT_FOUND = "Customer not found with ID: ";

    // Status messages
    public static final String REQUEST_RESOLVED = "YES";

    // Prevent instantiation
    private ServiceProviderConstants() {
        throw new AssertionError("Cannot instantiate " + getClass().getName());
    }

}
