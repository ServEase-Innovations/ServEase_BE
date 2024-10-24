package com.cus.customertab.service;

import com.cus.customertab.constants.CustomerConstants;
import com.cus.customertab.dto.KYCCommentsDTO;
import com.cus.customertab.entity.KYC;
import com.cus.customertab.entity.KYCComments;
import com.cus.customertab.mapper.KYCCommentsMapper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class KYCCommentsServiceImpl implements KYCCommentsService {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private KYCCommentsMapper kycCommentsMapper;

    // To get all KYC comments
    @Override
    @Transactional(readOnly = true)
    public List<KYCCommentsDTO> getAllKycComments() {
        Session session = sessionFactory.getCurrentSession();
        List<KYCComments> comments = session.createQuery(CustomerConstants.FROM_KYC_COMMENTS, KYCComments.class)
                .getResultList();
        return comments.stream()
                .map(kycCommentsMapper::kycCommentsToDTO)
                .toList();
    }

    // To get a KYC comment by ID
    @Override
    @Transactional(readOnly = true)
    public KYCCommentsDTO getKycCommentById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        KYCComments comment = session.get(KYCComments.class, id);
        return kycCommentsMapper.kycCommentsToDTO(comment);
    }

    // To add a new KYC comment
    @Override
    @Transactional
    public String addKycComment(KYCCommentsDTO commentDTO) {
        Session session = sessionFactory.getCurrentSession();

        // Fetch the KYC entity using the provided ID from the DTO
        KYC kyc = session.get(KYC.class, commentDTO.getKyc_id());
        if (kyc == null) {
            throw new IllegalArgumentException("KYC with ID " + commentDTO.getKyc_id() + " does not exist.");
        }

        // Create a new KYCComments instance and associate it with the managed KYC
        // entity
        KYCComments comment = new KYCComments();
        comment.setKyc(kyc); // Set the managed KYC entity
        comment.setServiceProviderId(commentDTO.getServiceProviderId());
        comment.setComment(commentDTO.getComment());
        comment.setCommentedBy(commentDTO.getCommentedBy());

        // Persist the comment
        session.persist(comment); // This should work now as the KYC entity is managed

        return CustomerConstants.ADDED;
    }

    // @Override
    // @Transactional
    // public String addKycComment(KYCCommentsDTO commentDTO) {
    //     Session session = sessionFactory.getCurrentSession();
    //     KYC kyc = session.get(KYC.class, commentDTO.getId());

    //     KYCComments comment = kycCommentsMapper.dtoToKYCComments(commentDTO);
    //     kyc.getComments().add(comment); // Add the comment to the KYC
    //     comment.setKyc(kyc); // Set the relationship
    //     session.persist(comment);
    //     return CustomerConstants.ADDED;
    // }

    // To update a KYC comment
    @Override
    @Transactional
    public String updateKycComment(Long id, KYCCommentsDTO commentDTO) {
        Session session = sessionFactory.getCurrentSession();
        KYCComments existingComment = session.get(KYCComments.class, id);

        if (existingComment == null) {
            return "Comment not found"; // Handle the case when the comment is not found
        }

        KYCComments updatedComment = kycCommentsMapper.dtoToKYCComments(commentDTO);
        updatedComment.setId(existingComment.getId());

        session.merge(updatedComment);
        return CustomerConstants.UPDATED;
    }

    // To delete a KYC comment
    @Override
    @Transactional
    public String deleteKycComment(Long id) {
        Session session = sessionFactory.getCurrentSession();
        KYCComments existingComment = session.get(KYCComments.class, id);
        session.remove(existingComment);
        return CustomerConstants.DELETED;
    }
}
