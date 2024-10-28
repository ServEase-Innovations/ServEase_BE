package com.cus.customertab.service;

import com.cus.customertab.config.PaginationHelper;
import com.cus.customertab.constants.CustomerConstants;
import com.cus.customertab.dto.KYCCommentsDTO;
import com.cus.customertab.entity.KYC;
import com.cus.customertab.entity.KYCComments;
import com.cus.customertab.mapper.KYCCommentsMapper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KYCCommentsServiceImpl implements KYCCommentsService {

    private static final Logger logger = LoggerFactory.getLogger(KYCCommentsServiceImpl.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private KYCCommentsMapper kycCommentsMapper;

    // To get all KYC comments
    @Override
    @Transactional(readOnly = true)
    public List<KYCCommentsDTO> getAllKycComments(int page, int size) {
        logger.info("Fetching all KYC comments with pagination - page: {}, size: {}", page, size);
        Session session = sessionFactory.getCurrentSession();
        List<KYCComments> comments = PaginationHelper.getPaginatedResults(
                session,
                CustomerConstants.FROM_KYC_COMMENTS,
                page,
                size,
                KYCComments.class);
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
        Session session = sessionFactory.getCurrentSession();
        KYCComments comment = session.get(KYCComments.class, id);

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
        Session session = sessionFactory.getCurrentSession();

        KYC kyc = session.get(KYC.class, commentDTO.getKyc_id());
        if (kyc == null) {
            logger.error("KYC with ID {} does not exist.", commentDTO.getKyc_id());
            throw new IllegalArgumentException("KYC with ID " + commentDTO.getKyc_id() + " does not exist.");
        }
        KYCComments comment = new KYCComments();
        comment.setKyc(kyc);
        comment.setServiceProviderId(commentDTO.getServiceProviderId());
        comment.setComment(commentDTO.getComment());
        comment.setCommentedBy(commentDTO.getCommentedBy());
        session.persist(comment);
        logger.debug("Persisted new KYC comment with ID: {}", comment.getId());
        return CustomerConstants.ADDED;
    }

    // To update a KYC comment
    @Override
    @Transactional
    public String updateKycComment(Long id, KYCCommentsDTO commentDTO) {
        logger.info("Updating KYC comment with ID: {}", id);
        Session session = sessionFactory.getCurrentSession();
        KYCComments existingComment = session.get(KYCComments.class, id);

        if (existingComment == null) {
            logger.error("Comment not found with ID: {}", id);
            return "Comment not found";
        }
        KYCComments updatedComment = kycCommentsMapper.dtoToKYCComments(commentDTO);
        updatedComment.setId(existingComment.getId());
        session.merge(updatedComment);
        logger.debug("Updated KYC comment with ID: {}", id);

        return CustomerConstants.UPDATED;
    }

    // To delete a KYC comment
    @Override
    @Transactional
    public String deleteKycComment(Long id) {
        logger.info("Deleting KYC comment with ID: {}", id);
        Session session = sessionFactory.getCurrentSession();
        KYCComments existingComment = session.get(KYCComments.class, id);

        if (existingComment != null) {
            session.remove(existingComment);
            logger.debug("Deleted KYC comment with ID: {}", id);
            return CustomerConstants.DELETED;
        } else {
            logger.error("Comment not found with ID: {}", id);
            return "Comment not found";
        }
    }
}
