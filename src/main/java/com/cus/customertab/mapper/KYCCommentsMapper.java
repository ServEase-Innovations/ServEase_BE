package com.cus.customertab.mapper;

import com.cus.customertab.dto.KYCCommentsDTO;
import com.cus.customertab.entity.KYC;
import com.cus.customertab.entity.KYCComments;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface KYCCommentsMapper {
    KYCCommentsMapper INSTANCE = Mappers.getMapper(KYCCommentsMapper.class);

    @Mapping(source = "kyc.kyc_id", target = "kyc_id")
    KYCCommentsDTO kycCommentsToDTO(KYCComments kycComments);

    @Mapping(target = "kyc", ignore = true) // Ignore KYC for now; we'll set it manually
    KYCComments dtoToKYCComments(KYCCommentsDTO kycCommentsDTO);

    default KYCComments dtoToKYCCommentsWithKYC(KYCCommentsDTO kycCommentsDTO, KYC kyc) {
        KYCComments comment = dtoToKYCComments(kycCommentsDTO);
        comment.setKyc(kyc);
        return comment;
    }
}
