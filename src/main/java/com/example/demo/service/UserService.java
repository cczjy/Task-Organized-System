package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import com.example.demo.service.VerificationService;
import com.example.demo.service.RegistrationResult;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VerificationService verificationService;

    /**
     * 用户注册
     * @return 返回一个 RegistrationResult 枚举，表明注册的结果
     */
    public RegistrationResult register(String username, String email, String password, String verificationCode) {
        // 1. 优先校验验证码
        if (!verificationService.validateCode(email, verificationCode)) {
            return RegistrationResult.INVALID_VERIFICATION_CODE;
        }

        // 2. 其次检查邮箱是否已存在
        if (userRepository.findByEmail(email).isPresent()) {
            return RegistrationResult.EMAIL_ALREADY_EXISTS;
        }

        // 3. 如果都通过，则创建用户
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPasswordHash(passwordEncoder.encode(password));

        try {
            userRepository.save(newUser);
            return RegistrationResult.SUCCESS;
        } catch (Exception e) {
            // 捕获可能的数据库异常等
            return RegistrationResult.UNKNOWN_ERROR;
        }
    }

    /**
     * 用户登录
     * 根据邮箱查找用户，并验证密码。
     * @return 如果邮箱和密码匹配，返回用户对象；否则返回 null。
     */
    public Optional<User> login(String email, String password) {
        // 首先根据邮箱查找用户
        Optional<User> userOptional = userRepository.findByEmail(email);

        // 如果用户存在，并且输入的密码与数据库中加密的密码匹配
        if (userOptional.isPresent() && passwordEncoder.matches(password, userOptional.get().getPasswordHash())) {
            return userOptional; // 登录成功，返回包含用户信息的 Optional
        }

        // 登录失败
        return Optional.empty();
    }

    /**
     * 修改密码
     * 验证用户旧密码是否正确，如果正确则更新为新密码。
     * @return 如果修改成功，返回 true；否则返回 false。
     */
    public boolean changePassword(String email, String oldPassword, String newPassword, String verificationCode) {
        // 5. 增加验证码校验逻辑
        if (!verificationService.validateCode(email, verificationCode)) {
            return false; // 验证码错误
        }

        Optional<User> userOptional = userRepository.findByEmail(email);

        // 检查用户是否存在，以及旧密码是否正确
        if (userOptional.isPresent() && passwordEncoder.matches(oldPassword, userOptional.get().getPasswordHash())) {
            User userToUpdate = userOptional.get();
            // 设置新的、加密后的密码
            userToUpdate.setPasswordHash(passwordEncoder.encode(newPassword));
            // 保存更新
            userRepository.save(userToUpdate);
            return true; // 密码修改成功
        }

        // 如果用户不存在或旧密码错误，则修改失败
        return false;
    }

    /**
     * 修改用户名
     * @param email 用户的当前邮箱，用于定位用户
     * @param newUsername 新的用户名
     * @return 如果成功，返回更新后的 User 对象；如果用户不存在，返回 Optional.empty()
     */
    public Optional<User> changeUsername(String email, String newUsername) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setUsername(newUsername);
            userRepository.save(user);
            return Optional.of(user);
        }
        return Optional.empty();
    }

    /**
     * 修改用户邮箱
     * @param oldEmail 用户的旧邮箱
     * @param newEmail 用户的新邮箱
     * @param verificationCode 发送到旧邮箱的验证码
     * @return 如果成功，返回 true；如果失败（用户不存在、新邮箱已被使用、验证码错误），返回 false
     */
    public boolean changeEmail(String oldEmail, String newEmail, String verificationCode) {
        // 1. 校验验证码是否正确
        if (!verificationService.validateCode(oldEmail, verificationCode)) {
            return false; // 验证码不正确
        }

        // 2. 检查新邮箱是否已经被其他用户使用
        if (userRepository.findByEmail(newEmail).isPresent()) {
            return false; // 新邮箱已被占用
        }

        // 3. 查找用户并更新邮箱
        Optional<User> userOptional = userRepository.findByEmail(oldEmail);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setEmail(newEmail);
            userRepository.save(user);
            return true; // 修改成功
        }

        return false; // 用户不存在
    }
}