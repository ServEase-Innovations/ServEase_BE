package com.springboot.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private MessageSource messageSource;

    @GetMapping("/translate")
    public Map<String, String> getAllTranslations(
            @RequestParam(name = "lang", defaultValue = "en") String lang) {
        Locale locale = Locale.forLanguageTag(lang);
        // Locale locale = new Locale(lang);

        // List of all fields to translate
        String[] fields = {
                "firstName", "middleName", "lastName", "mobileNo", "alternateNo", "emailId",
                "gender", "buildingName", "locality", "street", "pincode", "currentLocation",
                "nearbyLocation", "enrolledDate", "profilePic", "isActive", "housekeepingRole",
                "diet", "cookingSpeciality", "kyc", "idNo", "profilePicUrl", "rating",
                "languageKnown", "speciality", "age", "info", "dob", "expectedSalary", "username", "password"
        };

        // Create a map of translations
        Map<String, String> translations = new HashMap<>();
        for (String field : fields) {
            String translation = messageSource.getMessage(field, null, locale);
            translations.put(field, translation);
        }
        return translations;
    }
}

/*
 * import org.springframework.beans.factory.annotation.Autowired;
 * import org.springframework.context.MessageSource;
 * import org.springframework.web.bind.annotation.GetMapping;
 * import org.springframework.web.bind.annotation.RequestParam;
 * import org.springframework.web.bind.annotation.RestController;
 * 
 * import java.util.Locale;
 * 
 * @RestController
 * public class UserController {
 * 
 * @Autowired
 * private MessageSource messageSource;
 * 
 * @GetMapping("/translate")
 * public String getTranslatedField(
 * 
 * @RequestParam String fieldName,
 * 
 * @RequestParam(name = "lang", defaultValue = "en") String lang) {
 * Locale locale = new Locale(lang);
 * return messageSource.getMessage(fieldName, null, locale);
 * }
 * }
 */