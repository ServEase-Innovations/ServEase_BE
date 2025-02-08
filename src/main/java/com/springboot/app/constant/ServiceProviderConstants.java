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
    public static final String SERVICE_PROVIDER_ALREADY_EXISTS = "Service provider with this email or mobile number already exists.";
    public static final String SERVICE_PROVIDER_ERROR = "An error occurred while processing the service provider request.";
    public static final String NO_SERVICE_PROVIDERS_FOUND_FOR_VENDOR = "No service providers found for vendor ID: ";
    public static final String GET_BY_VENDOR_ID_DESC = "Retrieve all service providers by Vendor ID";

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

    // Attendance Constants
    public static final String ATTENDANCE_ADDED = "Attendance record added successfully";
    public static final String ATTENDANCE_UPDATED = "Attendance record updated successfully";
    public static final String ATTENDANCE_DELETED = "Attendance record marked as deleted successfully";

    // Error messages
    public static final String SERVICE_PROVIDER_NOT_FOUND = "ServiceProvider not found with id: ";
    public static final String SERVICE_PROVIDER_REQUEST_NOT_FOUND = "ServiceProviderRequest not found with id: ";
    public static final String FEEDBACK_NOT_FOUND = "ServiceProviderFeedback not found with id: ";

    // Error messages for customer not found
    public static final String CUSTOMER_NOT_FOUND = "Customer not found with ID: ";

    // Status messages
    public static final String REQUEST_RESOLVED = "YES";

    // Eng
    public static final String RETRIEVE_ALL_ENGAGEMENT_DESC = "Retrieve all service provider engagements ";
    public static final String GET_BY_ID_ENGAGEMENT_DESC = "Retrieve service provider engagement by ID";
    public static final String ADD_NEW_ENGAGEMENT_DESC = "Add a new service provider engagement";
    public static final String UPDATE_ENGAGEMENT_DESC = "Update an existing service provider engagement";
    public static final String DEACTIVATE_ENGAGEMENT_DESC = "Deactivate a service provider engagement";
    public static final String ENGAGEMENT_ALREADY_EXISTS = "Service provider engagement already exists.";
    public static final String ENGAGEMENT_NOT_FOUND = "Service provider engagement not found.";
    public static final String ENGAGEMENT_ERROR = "Error occurred while processing the service provider engagement.";
    public static final String GET_BY_SERVICE_PROVIDER_ID_DESC = "Get all engagements for a given ServiceProvider ID.";

    public static final String ENGAGEMENT_ADDED = "Service Provider Engagement added successfully.";
    public static final String ENGAGEMENT_UPDATED = "Service Provider Engagement updated successfully.";
    public static final String ENGAGEMENT_DELETED = "Service Provider Engagement deactivated successfully.";

    public static final String PASSWORD_UPDATED = "Password updated successfully.";
    public static final String ACCOUNT_DEACTIVATED = "Account deactivated successfully.";
    public static final String USER_NOT_FOUND = "User not found.";

    public static final String VENDOR_SAVED = "Vendor saved successfully.";
    public static final String VENDOR_UPDATED = "Vendor updated successfully.";
    public static final String VENDOR_DELETED = "Vendor deactivated successfully.";
    public static final String VENDOR_NOT_FOUND = "Vendor not found.";
    public static final String RETRIEVE_ALL_VENDOR_DESC = "Retrieve all vendors";
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final String GET_VENDOR_BY_ID_DESC = "Get a vendor by ID";
    public static final String ADD_NEW_VENDOR_DESC = "Add a new vendor";
    public static final String VENDOR_ADDED = "Vendor has been added successfully.";
    public static final String VENDOR_ALREADY_EXISTS = "Vendor already exists.";
    public static final String VENDOR_ERROR = "An error occurred while adding the vendor.";
    public static final String UPDATE_VENDOR_DESC = "Update vendor details";

    public static final String DELETE_VENDOR_DESC = "Delete a vendor (deactivate)";

    // Prevent instantiation
    private ServiceProviderConstants() {
        throw new AssertionError("Cannot instantiate " + getClass().getName());
    }

}
