// API配置文件
// 通过修改这个文件可以快速切换后端服务器地址，方便调试

const API_CONFIG = {
    // 基础URL配置
    // 可以根据需要切换不同的服务器地址
    
    // 选项1: 主服务器
    BASE_URL_1: '',
    
    // 选项2: 备用服务器
    BASE_URL_2: 'http://10.198.137.109:8080',
    
    // 本地开发服务器
    BASE_URL_LOCAL: 'http://localhost:8080',
    
    // 部署
    BASE_URL_final: '',

    // 当前使用的BASE_URL（修改这里来切换服务器）
    get CURRENT_BASE_URL() {
        return this.BASE_URL_final;  // 修改为 BASE_URL_1, BASE_URL_2 或 BASE_URL_LOCAL
    }
};

// API端点配置
const API_ENDPOINTS = {
    // 用户相关接口
    USER_LOGIN: '/api/users/login',
    USER_REGISTER: '/api/users/register',
    USER_CHANGE_PASSWORD: '/api/users/change-password',
    USER_SEND_VERIFICATION_CODE: '/api/users/send-verification-code',
    USER_CHANGE_USERNAME: '/api/users/change-username',
    USER_CHANGE_EMAIL: '/api/users/change-email',
    
    // 工作空间相关接口
    WORKSPACE_CREATE: '/api/workspaces/create',
    WORKSPACE_DELETE: (workspaceId) => `/api/workspaces/${workspaceId}`,
    WORKSPACE_MEMBERS: (workspaceId) => `/api/workspaces/${workspaceId}/members`,
    WORKSPACE_INVITATIONS: (workspaceId) => `/api/workspaces/${workspaceId}/invitations`,
    WORKSPACE_TASKS: (userId, workspaceId) => `/api/users/${userId}/workspaces/${workspaceId}/tasks`,
    WORKSPACE_MEMBER_PROMOTE: (workspaceId, userId) => `/api/workspaces/${workspaceId}/members/${userId}/promote`,
    WORKSPACE_MEMBER_DEMOTE: (workspaceId, userId) => `/api/workspaces/${workspaceId}/members/${userId}/demote`,
    WORKSPACE_MEMBER_DELETE: (workspaceId, userId) => `/api/workspaces/${workspaceId}/members/${userId}`,
    
    // 邀请相关接口
    INVITATIONS_PENDING: '/api/invitations/pending',
    INVITATIONS_ACCEPT: (invitationId) => `/api/invitations/${invitationId}/accept`,
    INVITATIONS_DECLINE: (invitationId) => `/api/invitations/${invitationId}/decline`,
    
    // 任务相关接口
    TASK_DOCUMENT: '/api/tasks/document',
    TASK_VOTE: '/api/tasks/vote',
    TASK_DISCUSSION: '/api/tasks/discussion',
    TASK_DETAIL: (taskId) => `/api/tasks/${taskId}`,
    TASK_VOTE_CAST: (taskId) => `/api/tasks/${taskId}/votes/cast`,
    TASK_COMMENT_ADD: (taskId) => `/api/tasks/${taskId}/comments/add`,
    TASK_COMMENT_REPLY: (parentCommentId) => `/api/tasks/comments/${parentCommentId}/reply`,
    TASK_DOCUMENT_SUBMIT: (taskId) => `/api/tasks/${taskId}/documents/submit`,
    TASK_COMPLETION_STATUS: (taskId) => `/api/tasks/${taskId}/completion-status`,
    
    // 文件相关接口
    FILE_DOWNLOAD: (fileId) => `/api/files/download/${fileId}`,
    
    // 用户任务相关接口
    USER_LATEST_UNFINISHED_TASKS: (userId) => `/api/users/${userId}/tasks/latest-unfinished`
};

// 辅助函数：获取完整的API URL
function getApiUrl(endpoint) {
    return API_CONFIG.CURRENT_BASE_URL + endpoint;
}

// 辅助函数：获取带参数的API URL
function getApiUrlWithParams(endpointFunc, ...params) {
    return API_CONFIG.CURRENT_BASE_URL + endpointFunc(...params);
}

