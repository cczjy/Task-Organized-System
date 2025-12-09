package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VerificationService {

    @Autowired
    private JavaMailSender mailSender;

    // 使用ConcurrentHashMap作为内存缓存来存储验证码
    private final ConcurrentHashMap<String, VerificationCode> verificationCodes = new ConcurrentHashMap<>();
    private static final int CODE_EXPIRATION_MINUTES = 15;

    /**
     * 生成并发送验证码到指定邮箱
     * @param email 目标邮箱
     */
    public void generateAndSendCode(String email) {
        String code = String.format("%06d", new Random().nextInt(999999)); // 生成6位随机数
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(CODE_EXPIRATION_MINUTES);

        // 存储验证码和过期时间
        verificationCodes.put(email, new VerificationCode(code, expirationTime));

        // 发送邮件
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom("task_organized_sys@163.com"); // 确保这里和配置文件中的用户名一致
        message.setSubject("Your verification code");
        message.setText("Dear user, hello:\n\nYour verification code is: " + code + "\n\nThe verification code will expire after 15 minutes. Please use it in time. \n\nIf you haven't requested this verification code, please ignore this email.");

        mailSender.send(message);
    }

    /**
     * 校验验证码是否有效
     * @param email 邮箱
     * @param code  用户提供的验证码
     * @return true 如果有效, false 如果无效
     */
    public boolean validateCode(String email, String code) {
        VerificationCode storedCode = verificationCodes.get(email);

        if (storedCode != null && storedCode.getCode().equals(code) && LocalDateTime.now().isBefore(storedCode.getExpirationTime())) {
            // 验证成功后立即移除，防止重复使用
            verificationCodes.remove(email);
            return true;
        }
        return false;
    }

    // 内部类，用于存储验证码和其过期时间
    private static class VerificationCode {
        private final String code;
        private final LocalDateTime expirationTime;

        public VerificationCode(String code, LocalDateTime expirationTime) {
            this.code = code;
            this.expirationTime = expirationTime;
        }

        public String getCode() { return code; }
        public LocalDateTime getExpirationTime() { return expirationTime; }
    }
}