# API配置说明

## 简介
本项目已添加统一的API配置文件 `config.js`，方便快速切换后端服务器地址进行调试。

## 使用方法

### 1. 切换后端服务器地址

打开 `config.js` 文件，找到以下代码：

```javascript
// 当前使用的BASE_URL（修改这里来切换服务器）
get CURRENT_BASE_URL() {
    return this.BASE_URL_1;  // 修改为 BASE_URL_1, BASE_URL_2 或 BASE_URL_LOCAL
}
```

修改 `return` 语句来切换服务器：
- `this.BASE_URL_1` - 主服务器 (http://10.133.22.134:8080)
- `this.BASE_URL_2` - 备用服务器 (http://10.198.137.109:8080)
- `this.BASE_URL_LOCAL` - 本地开发服务器 (http://localhost:8080)

### 2. 添加新的服务器地址

如果需要添加新的服务器地址，可以在配置对象中添加：

```javascript
const API_CONFIG = {
    BASE_URL_1: 'http://10.133.22.134:8080',
    BASE_URL_2: 'http://10.198.137.109:8080',
    BASE_URL_LOCAL: 'http://localhost:8080',
    
    // 添加新的服务器地址
    BASE_URL_3: 'http://your-new-server:8080',
    
    get CURRENT_BASE_URL() {
        return this.BASE_URL_3;  // 使用新添加的地址
    }
};
```

### 3. 添加新的API端点

如果后端添加了新的API接口，可以在 `API_ENDPOINTS` 对象中添加：

```javascript
const API_ENDPOINTS = {
    // 用户相关接口
    USER_LOGIN: '/api/users/login',
    USER_REGISTER: '/api/users/register',
    
    // 添加新的API端点
    USER_PROFILE: '/api/users/profile',
    
    // ...其他端点
};
```

如果端点需要动态参数，可以使用函数形式：

```javascript
const API_ENDPOINTS = {
    // 静态端点
    USER_LOGIN: '/api/users/login',
    
    // 动态端点（带参数）
    USER_DETAIL: (userId) => `/api/users/${userId}`,
};
```

## 配置文件结构

### API_CONFIG
存储所有可用的服务器地址配置：
- `BASE_URL_1`: 主服务器地址
- `BASE_URL_2`: 备用服务器地址
- `BASE_URL_LOCAL`: 本地开发服务器地址
- `CURRENT_BASE_URL`: 当前使用的服务器地址（通过getter方法返回）

### API_ENDPOINTS
存储所有API端点路径：
- 用户相关接口
- 工作空间相关接口
- 邀请相关接口
- 任务相关接口

### 辅助函数
- `getApiUrl(endpoint)`: 获取完整的API URL
- `getApiUrlWithParams(endpointFunc, ...params)`: 获取带参数的API URL

## 已配置的页面

以下页面已经配置为使用统一的配置文件：
- ✅ Login.html
- ✅ Register.html
- ✅ PasswordChanges.html
- ✅ Dashboard.html
- ✅ Settings.html

## 示例

### 静态端点使用示例
```javascript
// 登录API调用
fetch(getApiUrl(API_ENDPOINTS.USER_LOGIN), {
    method: 'POST',
    body: body
})
```

### 动态端点使用示例
```javascript
// 获取工作空间成员
fetch(getApiUrlWithParams(API_ENDPOINTS.WORKSPACE_MEMBERS, workspaceId), {
    method: 'GET'
})
```

## 注意事项

1. 修改配置后，**无需修改任何HTML文件**，只需刷新页面即可生效
2. 确保 `config.js` 文件在所有HTML文件之前加载
3. 所有HTML文件已经正确引入 `config.js`
4. 建议在本地测试时使用 `BASE_URL_LOCAL`，部署时切换为对应的服务器地址

## 快速调试技巧

如果需要临时测试不同的服务器，可以在浏览器控制台直接修改：

```javascript
// 临时修改当前会话的BASE_URL（页面刷新后失效）
API_CONFIG.CURRENT_BASE_URL = 'http://test-server:8080';
```

但这种方式只在当前页面会话有效，刷新后会恢复为config.js中设置的值。

