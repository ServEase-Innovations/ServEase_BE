package com.cus.customertab.mapper;

import com.cus.customertab.dto.KYCDTO;
import com.cus.customertab.entity.KYC;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = KYCCommentsMapper.class)
public interface KYCMapper {
    KYCMapper INSTANCE = Mappers.getMapper(KYCMapper.class);

    @Mapping(source = "kyc_id", target = "kyc_id") 
    KYCDTO kycToDTO(KYC kyc);

    @Mapping(source = "kyc_id", target = "kyc_id") 
    KYC dtoToKYC(KYCDTO kycDTO);
}
