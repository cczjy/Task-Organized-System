package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.service.VerificationService;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.RegistrationResult;
import com.example.demo.dto.LoginResponseDTO;
import com.example.demo.dto.UserTaskSummaryDTO;
import com.example.demo.service.TaskService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.example.demo.dto.UserWorkspaceTaskDTO;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;

@RestController
@RequestMapping("/api/users") // 建议为所有用户相关的API添加统一前缀
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationService verificationService;

    // POST /api/users/send-verification-code
    @PostMapping("/send-verification-code")
    public ResponseEntity<Map<String, String>> sendVerificationCode(@RequestParam String email) {
        verificationService.generateAndSendCode(email);
        Map<String, String> response = new HashMap<>();
        response.put("message", "验证码已发送至 " + email);
        return ResponseEntity.ok(response);
    }

    // POST /api/users/register
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String verificationCode) {

        Map<String, Object> response = new HashMap<>();
        RegistrationResult result = userService.register(username, email, password, verificationCode);

        // 使用 switch 语句来处理不同的注册结果
        switch (result) {
            case SUCCESS:
                // 注册成功后，我们再去数据库把用户信息查出来返回
                // 这是为了遵循CQRS原则，让service的register方法职责更单一
                User user = userRepository.findByEmail(email).orElse(null); // 注入 UserRepository
                response.put("message", "用户注册成功");

                if (user != null) {
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("userId", user.getUserId());
                    userData.put("username", user.getUsername());
                    userData.put("email", user.getEmail());
                    response.put("user", userData);
                }
                return ResponseEntity.status(HttpStatus.CREATED).body(response);

            case EMAIL_ALREADY_EXISTS:
                response.put("error", "注册失败：该邮箱已被使用");
                // 使用 409 Conflict 状态码更符合RESTful风格，表示资源冲突
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);

            case INVALID_VERIFICATION_CODE:
                response.put("error", "注册失败：验证码错误或已失效");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

            case UNKNOWN_ERROR:
            default:
                response.put("error", "注册失败：发生未知错误");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // POST /api/users/login
    @PostMapping("/login")
    // 2. 修改方法的返回类型
    public ResponseEntity<?> loginUser(
            @RequestParam String email,
            @RequestParam String password) {

        Optional<User> userOptional = userService.login(email, password);

        if (userOptional.isPresent()) {
            // 3. 登录成功，创建一个 LoginResponseDTO
            User user = userOptional.get();
            LoginResponseDTO loginResponse = new LoginResponseDTO(user);

            // 4. 返回这个DTO作为响应体
            return ResponseEntity.ok(loginResponse);
        } else {
            // 登录失败的响应保持不变
            Map<String, String> response = new HashMap<>();
            response.put("error", "登录失败：邮箱或密码不正确");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    // POST /api/users/change-password
    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestParam String email,
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String verificationCode) {

        boolean isChanged = userService.changePassword(email, oldPassword, newPassword, verificationCode);
        Map<String, String> response = new HashMap<>();

        if (isChanged) {
            response.put("message", "密码修改成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "密码修改失败：验证码错误、用户不存在或旧密码错误");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // POST /api/users/change-username
    @PostMapping("/change-username")
    public ResponseEntity<Map<String, Object>> changeUsername(
            @RequestParam String email,
            @RequestParam String newUsername) {

        Map<String, Object> response = new HashMap<>();
        Optional<User> updatedUserOptional = userService.changeUsername(email, newUsername);

        if (updatedUserOptional.isPresent()) {
            response.put("message", "用户名修改成功");
            Map<String, Object> userData = new HashMap<>();
            userData.put("userId", updatedUserOptional.get().getUserId());
            userData.put("newUsername", updatedUserOptional.get().getUsername());
            response.put("user", userData);
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "修改失败：用户不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // POST /api/users/change-email
    @PostMapping("/change-email")
    public ResponseEntity<Map<String, String>> changeEmail(
            @RequestParam String oldEmail,
            @RequestParam String newEmail,
            @RequestParam String verificationCode) {

        Map<String, String> response = new HashMap<>();
        boolean isChanged = userService.changeEmail(oldEmail, newEmail, verificationCode);

        if (isChanged) {
            response.put("message", "邮箱修改成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "修改失败：验证码错误、用户不存在或新邮箱已被使用");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * API: 获取指定用户的最新的5个未完成任务
     * @param userId 用户的ID
     */
    @GetMapping("/{userId}/tasks/latest-unfinished")
    public ResponseEntity<List<UserWorkspaceTaskDTO>> getUserTasks(@PathVariable Long userId) { // 2. 修改返回类型
        List<UserWorkspaceTaskDTO> tasks = taskService.getLatestUnfinishedTasksForUser(userId);
        return ResponseEntity.ok(tasks);
    }

    /**
     * API: 获取指定用户在特定群组中的所有任务列表及其个人完成状态
     * @param userId      用户的ID
     * @param workspaceId 群组的ID
     */
    @GetMapping("/{userId}/workspaces/{workspaceId}/tasks")
    public ResponseEntity<List<UserWorkspaceTaskDTO>> getTasksForUserInWorkspace(
            @PathVariable Long userId,
            @PathVariable Long workspaceId) {

        List<UserWorkspaceTaskDTO> tasks = taskService.getTasksForUserInWorkspace(userId, workspaceId);
        return ResponseEntity.ok(tasks);
    }
}