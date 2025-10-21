package com.EmpTimeHub.generator;

import com.EmpTimeHub.entity.Employee;
import com.EmpTimeHub.repository.EmployeeRepository;
import com.EmpTimeHub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Optional;

@Component
public class CredentialGenerator {
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final String ALPHANUM = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String PASSWORD_SPECIAL = "!@#$%&*()-_=+[]{}";

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private EmployeeRepository employeeRepository;

    // ------------------ Existing Methods ------------------
    public static String randomAlphaNum(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUM.charAt(secureRandom.nextInt(ALPHANUM.length())));
        }
        return sb.toString();
    }

    public static String generateSecurePassword(int length) {
        if (length < 8) throw new IllegalArgumentException("Use at least 8 chars for passwords");
        String all = ALPHANUM + PASSWORD_SPECIAL;
        StringBuilder sb = new StringBuilder(length);

        // ensure at least one lower, one upper, one digit, one special
        sb.append((char) ('a' + secureRandom.nextInt(26)));
        sb.append((char) ('A' + secureRandom.nextInt(26)));
        sb.append((char) ('0' + secureRandom.nextInt(10)));
        sb.append(PASSWORD_SPECIAL.charAt(secureRandom.nextInt(PASSWORD_SPECIAL.length())));

        for (int i = 4; i < length; i++) {
            sb.append(all.charAt(secureRandom.nextInt(all.length())));
        }

        return shuffleString(sb.toString());
    }

    private static String shuffleString(String input) {
        char[] a = input.toCharArray();
        for (int i = a.length - 1; i > 0; i--) {
            int j = secureRandom.nextInt(i + 1);
            char tmp = a[i];
            a[i] = a[j];
            a[j] = tmp;
        }
        return new String(a);
    }

    public static String generateUniqueUsername(String prefix, UserRepository repo) {
        String username;
        int attempts = 0;
        do {
            username = prefix + "_" + randomAlphaNum(6);
            attempts++;
            if (attempts > 10) {
                username = prefix + "_" + java.util.UUID.randomUUID().toString().substring(0, 8);
                break;
            }
        } while (repo.existsByUserName(username));
        return username;
    }

    public String generateNextCompanyId() {
        Optional<Employee> lastEmployeeOpt = employeeRepository.findFirstByOrderByCreatedAtDesc();
        int nextNumber = 1;
        if (lastEmployeeOpt.isPresent() && lastEmployeeOpt.get().getCompanyId() != null) {
            String lastCompanyId = lastEmployeeOpt.get().getCompanyId();
            try {
                nextNumber = Integer.parseInt(lastCompanyId.substring(2)) + 1;
            } catch (NumberFormatException e) {
                nextNumber = 1;
            }
        }
        return String.format("DQ%03d", nextNumber);
    }

    public static String encryptPassword(String rawPassword, PasswordEncoder encoder) {
        if (rawPassword == null || encoder == null) return null;
        return encoder.encode(rawPassword);
    }

    // ------------------ NEW: Generate + Encrypt + Return Both ------------------
    public PasswordPair generateAndEncryptPassword(int length, PasswordEncoder encoder) {
        String rawPassword = generateSecurePassword(length);
        String encryptedPassword = encryptPassword(rawPassword, encoder);
        return new PasswordPair(rawPassword, encryptedPassword);
    }

    @lombok.AllArgsConstructor
    @lombok.Getter
    public static class PasswordPair {
        private final String rawPassword;
        private final String encryptedPassword;
    }
}
