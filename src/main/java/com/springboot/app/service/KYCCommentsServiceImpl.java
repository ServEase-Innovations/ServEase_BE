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
import java.util.stream.Collectors;

@Service
public class KYCCommentsServiceImpl implements KYCCommentsService {

    private static final Logger logger = LoggerFactory.getLogger(KYCCommentsServiceImpl.class);

    @Autowired
    private KYCCommentsRepository kycCommentsRepository;

    @Autowired
    private KYCRepository kycRepository;

    @Autowired
    private KYCCommentsMapper kycCommentsMapper;

    // To get all KYC comments
    @Override
    @Transactional(readOnly = true)
    public List<KYCCommentsDTO> getAllKycComments(int page, int size) {
        logger.info("Fetching all KYC comments with pagination - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        List<KYCComments> comments = kycCommentsRepository.findAll(pageable).getContent();
        logger.debug("Fetched {} KYC comments from the database.", comments.size());

        return comments.stream()
                .map(kycCommentsMapper::kycCommentsToDTO)
                .collect(Collectors.toList());
    }

    // To get a KYC comment by ID
    @Override
    @Transactional(readOnly = true)
    public KYCCommentsDTO getKycCommentById(Long id) {
        logger.info("Fetching KYC comment by ID: {}", id);

        KYCComments comment = kycCommentsRepository.findById(id).orElse(null);

        if (comment != null) {
            logger.debug("Found KYC comment with ID: {}", id);
        } else {
            logger.error("No KYC comment found with ID: {}", id);
        }

        return kycCommentsMapper.kycCommentsToDTO(comment);
    }

    // To add a new KYC comment
    @Override
    @Transactional
    public String addKycComment(KYCCommentsDTO commentDTO) {
        logger.info("Adding new KYC comment for KYC ID: {}", commentDTO.getKyc_id());

        KYC kyc = kycRepository.findById(commentDTO.getKyc_id()).orElse(null);
        if (kyc == null) {
            logger.error("KYC with ID {} does not exist.", commentDTO.getKyc_id());
            throw new IllegalArgumentException("KYC with ID " + commentDTO.getKyc_id() + " does not exist.");
        }

        KYCComments comment = new KYCComments();
        comment.setKyc(kyc);
        comment.setServiceProviderId(commentDTO.getServiceProviderId());
        comment.setComment(commentDTO.getComment());
        comment.setCommentedBy(commentDTO.getCommentedBy());
        kycCommentsRepository.save(comment);

        logger.debug("Persisted new KYC comment with ID: {}", comment.getId());
        return CustomerConstants.ADDED;
    }

    // To update a KYC comment
    @Override
    @Transactional
    public String updateKycComment(Long id, KYCCommentsDTO commentDTO) {
        logger.info("Updating KYC comment with ID: {}", id);

        KYCComments existingComment = kycCommentsRepository.findById(id).orElse(null);

        if (existingComment == null) {
            logger.error("Comment not found with ID: {}", id);
            return "Comment not found";
        }

        KYCComments updatedComment = kycCommentsMapper.dtoToKYCComments(commentDTO);
        updatedComment.setId(existingComment.getId());
        kycCommentsRepository.save(updatedComment);

        logger.debug("Updated KYC comment with ID: {}", id);

        return CustomerConstants.UPDATED;
    }

    // To delete a KYC comment
    @Override
    @Transactional
    public String deleteKycComment(Long id) {
        logger.info("Deleting KYC comment with ID: {}", id);

        KYCComments existingComment = kycCommentsRepository.findById(id).orElse(null);

        if (existingComment != null) {
            kycCommentsRepository.delete(existingComment);
            logger.debug("Deleted KYC comment with ID: {}", id);
            return CustomerConstants.DELETED;
        } else {
            logger.error("Comment not found with ID: {}", id);
            return "Comment not found";
        }
    }
}
