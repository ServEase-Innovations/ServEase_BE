package com.springboot.app.service;

import com.springboot.app.constant.CustomerConstants;
import com.springboot.app.dto.KYCCommentsDTO;
import com.springboot.app.entity.KYC;
import com.springboot.app.entity.KYCComments;
import com.springboot.app.mapper.KYCCommentsMapper;
import com.springboot.app.repository.KYCCommentsRepository;
import com.springboot.app.repository.KYCRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class KYCCommentsServiceImpl implements KYCCommentsService {

    private static final Logger logger = LoggerFactory.getLogger(KYCCommentsServiceImpl.class);
    private final KYCCommentsRepository kycCommentsRepository;
    private final KYCRepository kycRepository;
    private final KYCCommentsMapper kycCommentsMapper;

    @Autowired
    public KYCCommentsServiceImpl(KYCCommentsRepository kycCommentsRepository,
            KYCRepository kycRepository,
            KYCCommentsMapper kycCommentsMapper) {
        this.kycCommentsRepository = kycCommentsRepository;
        this.kycRepository = kycRepository;
        this.kycCommentsMapper = kycCommentsMapper;
    }

    // To get all KYC comments
    @Override
    @Transactional(readOnly = true)
    public List<KYCCommentsDTO> getAllKycComments(int page, int size) {
        if (logger.isInfoEnabled()) {
            logger.info("Fetching all KYC comments with pagination - page: {}, size: {}", page, size);
        }
        Pageable pageable = PageRequest.of(page, size);
        List<KYCComments> comments = kycCommentsRepository.findAll(pageable).getContent();
        if (logger.isDebugEnabled()) {
            logger.debug("Fetched {} KYC comments from the database.", comments.size());
        }
        return comments.stream()
                .map(kycCommentsMapper::kycCommentsToDTO)
                .toList();
    }

    // To get a KYC comment by ID
    @Override
    @Transactional(readOnly = true)
    public KYCCommentsDTO getKycCommentById(Long id) {
        if (logger.isInfoEnabled()) {
            logger.info("Fetching KYC comment by ID: {}", id);
        }

        KYCComments comment = kycCommentsRepository.findById(id).orElse(null);

        if (comment != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Found KYC comment with ID: {}", id);
            }
        } else {
            if (logger.isErrorEnabled()) {
                logger.error("No KYC comment found with ID: {}", id);
            }
        }

        return kycCommentsMapper.kycCommentsToDTO(comment);
    }

    // To add a new KYC comment
    @Override
    @Transactional
    public String addKycComment(KYCCommentsDTO commentDTO) {
        if (logger.isInfoEnabled()) {
            logger.info("Adding new KYC comment for KYC ID: {}", commentDTO.getKyc_id());
        }
        KYC kyc = kycRepository.findById(commentDTO.getKyc_id()).orElse(null);
        if (kyc == null) {
            if (logger.isErrorEnabled()) {
                logger.error("KYC with ID {} does not exist.", commentDTO.getKyc_id());
            }
            throw new IllegalArgumentException("KYC with ID " + commentDTO.getKyc_id() + " does not exist.");
        }

        KYCComments comment = new KYCComments();
        comment.setKyc(kyc);
        comment.setServiceProviderId(commentDTO.getServiceProviderId());
        comment.setComment(commentDTO.getComment());
        comment.setCommentedBy(commentDTO.getCommentedBy());
        kycCommentsRepository.save(comment);

        if (logger.isDebugEnabled()) {
            logger.debug("Persisted new KYC comment with ID: {}", comment.getId());
        }
        return CustomerConstants.ADDED;
    }

    // To update a KYC comment
    @Override
    @Transactional
    public String updateKycComment(Long id, KYCCommentsDTO commentDTO) {
        if (logger.isInfoEnabled()) {
            logger.info("Updating KYC comment with ID: {}", id);
        }

        KYCComments existingComment = kycCommentsRepository.findById(id).orElse(null);

        if (existingComment == null) {
            if (logger.isErrorEnabled()) {
                logger.error("Comment not found with ID: {}", id);
            }
            return CustomerConstants.NOT_FOUND;
        }

        KYCComments updatedComment = kycCommentsMapper.dtoToKYCComments(commentDTO);
        updatedComment.setId(existingComment.getId());
        kycCommentsRepository.save(updatedComment);

        if (logger.isDebugEnabled()) {
            logger.debug("Updated KYC comment with ID: {}", id);
        }
        return CustomerConstants.UPDATED;
    }

    // To delete a KYC comment
    @Override
    @Transactional
    public String deleteKycComment(Long id) {
        if (logger.isInfoEnabled()) {
            logger.info("Deleting KYC comment with ID: {}", id);
        }

        KYCComments existingComment = kycCommentsRepository.findById(id).orElse(null);

        if (existingComment != null) {
            kycCommentsRepository.delete(existingComment);
            if (logger.isDebugEnabled()) {
                logger.debug("Deleted KYC comment with ID: {}", id);
            }
            return CustomerConstants.DELETED;
        } else {
            if (logger.isErrorEnabled()) {
                logger.error("Comment not found with ID: {}", id);
            }
            return CustomerConstants.NOT_FOUND;
        }
    }
}
