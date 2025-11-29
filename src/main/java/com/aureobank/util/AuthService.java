package com.aureobank.util;

import com.aureobank.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    // For demonstration purposes, the MFA code is hardcoded.
    // In a real application, this would be generated and sent via email/SMS.
    private static final String SIMULATED_MFA_CODE = "123456";

    /**
     * Verifies the provided MFA code.
     * For admins, it checks against a hardcoded value.
     * @param user The user attempting to log in.
     * @param code The MFA code entered by the user.
     * @return true if the code is valid, false otherwise.
     */
    public boolean verifyMfaCode(User user, String code) {
        logger.info("Verifying MFA code for user: {}", user.getUsername());
        // Per comments, only admins use this simulated MFA.
        if (user != null && user.getRoles().contains("admin")) {
            return SIMULATED_MFA_CODE.equals(code);
        }
        // For non-admins or null user, MFA verification fails or should be handled differently.
        return false;
    }
}