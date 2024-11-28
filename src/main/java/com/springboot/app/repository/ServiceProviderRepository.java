package com.springboot.app.repository;

//import com.springboot.app.enums.Speciality;
//import com.springboot.app.enums.LanguageKnown;
import com.springboot.app.entity.ServiceProvider;
//import com.springboot.app.enums.Gender;
//import com.springboot.app.enums.HousekeepingRole;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceProviderRepository
        extends JpaRepository<ServiceProvider, Long>, JpaSpecificationExecutor<ServiceProvider> {

    // Other methods for filtering based on pincode, street, and locality
    // List<ServiceProvider> findByPincodeAndStreetAndLocality(Integer pincode,
    // String street, String locality);

    List<ServiceProvider> findByPincodeOrStreetOrLocality(Integer pincode, String street, String locality);
}

/*
 * import org.springframework.data.jpa.repository.JpaRepository;
 * import org.springframework.stereotype.Repository;
 * import com.springboot.app.entity.ServiceProvider;
 * import com.springboot.app.enums.Gender;
 * import com.springboot.app.enums.HousekeepingRole;
 * import com.springboot.app.enums.LanguageKnown;
 * import com.springboot.app.enums.Speciality;
 * import java.util.List;
 * 
 * @Repository
 * public interface ServiceProviderRepository extends
 * JpaRepository<ServiceProvider, Long> {
 * List<ServiceProvider> findByFilters(LanguageKnown language, Double rating,
 * Gender gender, Speciality speciality,
 * HousekeepingRole housekeepingRole, Integer minAge, Integer maxAge);
 * 
 * List<ServiceProvider> findByPincodeAndStreetAndLocality(Integer pincode,
 * String street, String locality);
 * 
 * List<ServiceProvider> findByPincodeOrStreetOrLocality(Integer pincode, String
 * street, String locality);
 * }
 */