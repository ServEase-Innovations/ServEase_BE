// package com.springboot.app.service;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

// import com.springboot.app.constant.ServiceProviderConstants;
// import com.springboot.app.dto.ServiceProviderFeedbackDTO;
// //import com.springboot.app.entity.Customer;
// import com.springboot.app.entity.ServiceProviderFeedback;
// import com.springboot.app.mapper.ServiceProviderFeedbackMapper;
// //import com.springboot.app.repository.CustomerRepository;
// import com.springboot.app.repository.ServiceProviderFeedbackRepository;
// import com.springboot.app.repository.ServiceProviderRepository;

// import java.util.List;
// import java.util.stream.Collectors;

// @Service
// public class ServiceProviderFeedbackServiceImpl implements
// ServiceProviderFeedbackService {

// private static final Logger logger =
// LoggerFactory.getLogger(ServiceProviderFeedbackServiceImpl.class);

// @Autowired
// private ServiceProviderFeedbackRepository feedbackRepository;

// @Autowired
// private ServiceProviderRepository serviceProviderRepository;

// @Autowired
// private ServiceProviderFeedbackMapper serviceProviderFeedbackMapper;

// @Override
// @Transactional(readOnly = true)
// public List<ServiceProviderFeedbackDTO> getAllServiceProviderFeedbackDTOs(int
// page, int size) {
// logger.info(ServiceProviderConstants.DESC_RETRIEVE_ALL_FEEDBACKS + " - page:
// {}, size: {}", page, size);

// return feedbackRepository.findAll(PageRequest.of(page, size))
// .stream()
// .map(serviceProviderFeedbackMapper::feedbackToDTO)
// .collect(Collectors.toList());
// }

// @Override
// @Transactional(readOnly = true)
// public ServiceProviderFeedbackDTO getServiceProviderFeedbackDTOById(Long id)
// {
// logger.info(ServiceProviderConstants.DESC_GET_FEEDBACK_BY_ID + " with ID:
// {}", id);

// ServiceProviderFeedback feedback = feedbackRepository.findById(id)
// .orElseThrow(() -> {
// logger.warn("Feedback with ID {} not found", id);
// return new RuntimeException(ServiceProviderConstants.FEEDBACK_NOT_FOUND +
// id);
// });

// return serviceProviderFeedbackMapper.feedbackToDTO(feedback);
// }

// @Override
// @Transactional
// public void saveServiceProviderFeedbackDTO(ServiceProviderFeedbackDTO
// feedbackDTO) {
// logger.info(ServiceProviderConstants.DESC_ADD_NEW_FEEDBACK + " for customer
// ID: {}, service provider ID: {}",
// feedbackDTO.getCustomerId(), feedbackDTO.getServiceproviderId());

// // Validate ServiceProvider existence
// if
// (!serviceProviderRepository.existsById(feedbackDTO.getServiceproviderId())) {
// logger.error("Service Provider with ID {} not found",
// feedbackDTO.getServiceproviderId());
// throw new RuntimeException(
// ServiceProviderConstants.SERVICE_PROVIDER_NOT_FOUND +
// feedbackDTO.getServiceproviderId());
// }

// // Save feedback
// ServiceProviderFeedback feedback =
// serviceProviderFeedbackMapper.dtoToFeedback(feedbackDTO);
// feedbackRepository.save(feedback);
// logger.debug("Feedback saved: {}", feedback);
// }

// @Override
// @Transactional
// public void updateServiceProviderFeedbackDTO(ServiceProviderFeedbackDTO
// feedbackDTO) {
// logger.info(ServiceProviderConstants.DESC_UPDATE_FEEDBACK + " with ID: {},
// service provider ID: {}",
// feedbackDTO.getId(), feedbackDTO.getServiceproviderId());

// // Validate ServiceProvider existence
// if
// (!serviceProviderRepository.existsById(feedbackDTO.getServiceproviderId())) {
// logger.error("Service Provider with ID {} not found",
// feedbackDTO.getServiceproviderId());
// throw new RuntimeException(
// ServiceProviderConstants.SERVICE_PROVIDER_NOT_FOUND +
// feedbackDTO.getServiceproviderId());
// }

// // Fetch existing feedback
// ServiceProviderFeedback existingFeedback =
// feedbackRepository.findById(feedbackDTO.getId())
// .orElseThrow(() -> {
// logger.warn("Feedback with ID {} not found for update", feedbackDTO.getId());
// return new RuntimeException(ServiceProviderConstants.FEEDBACK_NOT_FOUND +
// feedbackDTO.getId());
// });

// // Update entity fields using mapper
// serviceProviderFeedbackMapper.updateEntityFromDTO(feedbackDTO,
// existingFeedback);
// feedbackRepository.save(existingFeedback);

// logger.debug("Feedback updated: {}", existingFeedback);
// }

// @Override
// @Transactional
// public void deleteServiceProviderFeedbackDTO(Long id) {
// logger.info(ServiceProviderConstants.DESC_DELETE_FEEDBACK + " with ID: {}",
// id);

// ServiceProviderFeedback feedback = feedbackRepository.findById(id)
// .orElseThrow(() -> {
// logger.warn("Feedback with ID {} not found for deletion", id);
// return new RuntimeException(ServiceProviderConstants.FEEDBACK_NOT_FOUND +
// id);
// });

// feedbackRepository.delete(feedback);
// logger.debug("Feedback with ID {} deleted", id);
// }
// }
