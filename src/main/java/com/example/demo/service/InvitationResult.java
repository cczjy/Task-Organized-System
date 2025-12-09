package com.example.demo.service;

public enum InvitationResult {
    SUCCESS,
    INVITER_NOT_FOUND,
    WORKSPACE_NOT_FOUND,
    NO_PERMISSION,
    INVITEE_ALREADY_MEMBER,
    INVITEE_NOT_REGISTERED, // 新增：被邀请的用户未注册
    UNKNOWN_ERROR
}