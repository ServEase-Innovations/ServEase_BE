package com.springboot.app.service;

import com.springboot.app.dto.CustomerHolidaysDTO;
import java.util.List;

public interface CustomerHolidaysService {
    List<CustomerHolidaysDTO> getAllHolidays(int page, int size);

    CustomerHolidaysDTO getHolidayById(Long id);

    String addNewHoliday(CustomerHolidaysDTO customerHolidaysDTO);

    String modifyHoliday(CustomerHolidaysDTO customerHolidaysDTO);

    String deactivateHoliday(Long id);

}
