package com.dmv.service;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ExternalDataHelper {
    
    private final ExternalDataService externalDataService;
    
    public ExternalDataHelper(ExternalDataService externalDataService) {
        this.externalDataService = externalDataService;
    }
    
    public Optional<Map<String, Object>> getStateByCode(String stateCode) {
        List<Map<String, Object>> states = externalDataService.findByCollectionAndKey("states", "code", stateCode);
        return states.isEmpty() ? Optional.empty() : Optional.of(states.get(0));
    }
    
    public Optional<Map<String, Object>> getLicenseClassByCode(String classCode) {
        List<Map<String, Object>> classes = externalDataService.findByCollectionAndKey("license-classes", "class", classCode);
        return classes.isEmpty() ? Optional.empty() : Optional.of(classes.get(0));
    }
    
    public boolean isOnlineRenewalAvailable(String stateCode) {
        Optional<Map<String, Object>> state = getStateByCode(stateCode);
        if (state.isPresent()) {
            Map<String, Object> dmvInfo = (Map<String, Object>) state.get().get("dmv");
            if (dmvInfo != null) {
                return Boolean.TRUE.equals(dmvInfo.get("onlineRenewalAvailable"));
            }
        }
        return false;
    }
    
    public double getLateFee(String stateCode) {
        Optional<Map<String, Object>> state = getStateByCode(stateCode);
        if (state.isPresent()) {
            Map<String, Object> dmvInfo = (Map<String, Object>) state.get().get("dmv");
            if (dmvInfo != null) {
                Object fee = dmvInfo.get("lateFee");
                if (fee instanceof Number) {
                    return ((Number) fee).doubleValue();
                }
            }
        }
        return 0.0;
    }
    
    public boolean requiresDrivingTest(String classCode) {
        Optional<Map<String, Object>> licenseClass = getLicenseClassByCode(classCode);
        if (licenseClass.isPresent()) {
            Map<String, Object> testRequirements = (Map<String, Object>) licenseClass.get().get("testRequirements");
            if (testRequirements != null) {
                return Boolean.TRUE.equals(testRequirements.get("driving"));
            }
        }
        return false;
    }
    
    public double getRenewalFee(String classCode) {
        Optional<Map<String, Object>> licenseClass = getLicenseClassByCode(classCode);
        if (licenseClass.isPresent()) {
            Object fee = licenseClass.get().get("renewalFee");
            if (fee instanceof Number) {
                return ((Number) fee).doubleValue();
            }
        }
        return 0.0;
    }
    
    public int getMinimumAge(String classCode) {
        Optional<Map<String, Object>> licenseClass = getLicenseClassByCode(classCode);
        if (licenseClass.isPresent()) {
            Object age = licenseClass.get().get("minAge");
            if (age instanceof Number) {
                return ((Number) age).intValue();
            }
        }
        return 0;
    }
}