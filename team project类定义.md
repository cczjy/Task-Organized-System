## 类定义

### 核心类

#### 1. `User` (用户) 类
这是系统的基本参与者，所有操作都由用户发起。

*   **职责**: 代表一个独立的用户账户，存储用户的身份信息。
*   **属性**:
    *   `userId`: `String` 或 `Integer` - 用户的唯一标识符 (主键)。
    *   `username`: `String` - 用户昵称。
    *   `email`: `String` - 用户的登录邮箱，也用于接收邀请。
    *   `passwordHash`: `String` - 经过加密的用户密码。
    *   `introduction`：（可选）用户本人的职业描述。
    *   `groupList` ：用户的所有群组的列表。
*   **方法**:
    *   登录页：
        *   `register`：用户注册（包含参数：邮箱、用户名、密码、验证码（使用SMTP发送））
        *   `login`：用户登录（用户邮箱和密码）
        *   `chagePassword`：密码修改（用户邮箱，原密码，新密码）
    
    *   首页：
        *   `createWorkspace(name)`: 创建一个新的群组。
        *   `getWorkspaces()`: 获取该用户所属的所有群组列表。
    
    *   设置页：
        *   `updateProfile(newInfo)`: 更新个人信息。
    

#### 2. `Workspace` 类

*   **职责**: 容纳成员和任务的容器，定义了一个协作的边界。
*   **属性**:
    *   `workspaceId`: `String` - 群组的唯一标识符 (主键)。
    *   `name`: `String` - 群组的名称。
    *   `description`: `String` - (可选) 群组的描述。
    *   `ownerId`: `String` - 群主的`userId`，明确指向一个`User`对象。
    *   `taskList<Task>`：群组里面包含的任务列表。
*   **方法**:
    *   `getMembers()`: 获取此群组的所有成员列表。
    *   `createTask(title, description, assigneeId)`: 创建一个新任务。
    *   `inviteMember(email)`: 邀请一个新成员。
    *   `removeMember(userId)`: 移除一个成员。

#### 3. `Task` (任务) 父类（只有`OWNER`, `ADMIN`能够创建，组员只能够查阅回复）

这是群组内的具体工作项。

*   **职责**: 代表一个需要被完成的具体事项。
*   **属性**:
    *   `taskId`: `String` - 任务的唯一标识符 (主键)。
    *   `title`: `String` - 任务的标题。
    *   `description`: `String` - (可选) 任务的详细描述。
    *   `status`: `Enum` - 任务的当前状态 (例如: `TODO`, `IN_PROGRESS`, `DONE`)。
    *   `creatorId`: `String` - 任务创建者的`userId`。
    *   `assigneeId`: `String` - 任务被指派给的用户的`userId`。
    *   `workspaceId`: `String` - 该任务所属的群组ID。
    *   `dueDate`: `DateTime` - (可选) 任务的截止日期。
    *   `createdAt`: `DateTime` - 任务的创建时间。
*   **方法**:
    *   `updateStatus(newStatus)`: 更改任务的状态。
    *   `assignTo(userId)`: 将任务指派或改派给其他用户。
    *   `addComment(userId, text)`: (未来扩展功能) 添加评论。

#### 任务子类

##### (1). 投票任务（子类一）先不具体实现

##### (2). 说明型任务（子类二）先不具体实现

##### (3). 通知型任务（子类三）先不具体实现

---

### 关系类 / 辅助类

#### 4. `Membership` (成员关系) 类

这个类用来处理用户和群组之间的“多对多”关系。一个用户可以加入多个群组，一个群组可以拥有多个用户。

*   **职责**: 定义一个用户在某个特定群组中的角色和状态。
*   **属性**:
    *   `membershipId`: `String`  - 关系ID (主键)。
    *   `userId`: `String`  - 关联的`User`。
    *   `workspaceId`: `String`  - 关联的`Workspace`。
    *   `role`: `Enum` - 成员的角色 (例如: `OWNER`, `ADMIN`, `MEMBER`)。
    *   `joinedAt`: `DateTime` - 加入时间。

#### 5. `Invitation` (邀请) 类
这个类用来管理邀请流程。

*   **职责**: 记录一个从邀请者到被邀请者的、针对特定群组的待处理邀请。
*   **属性**:
    *   `invitationId`: `String`- 邀请的唯一标识符 (主键)。
    *   `workspaceId`: `String` - 邀请加入的群组ID。
    *   `inviterId`: `String` - 发起邀请的用户的ID。
    *   `inviteeEmail`: `String` - 被邀请人的邮箱地址。
    *   `status`: `Enum` - 邀请的状态 (例如: `PENDING`, `ACCEPTED`, `DECLINED`)。
    *   `createdAt`: `DateTime` - 邀请创建时间。
*   **方法**:
    *   `accept()`: 接受邀请（接受后会创建一个`Membership`记录）。
    *   `decline()`: 拒绝邀请。

---

### 类之间的关系

*   一个 **User** 可以是 0 到多个 **Workspace** 的 `owner`。 (一对多)
*   一个 **User** 可以通过 **Membership** 类加入多个 **Workspace**。 (多对多)
*   一个 **Workspace** 可以拥有多个 **User** 作为成员 (通过 **Membership**)。 (多对多)
*   一个 **Workspace** 包含多个 **Task**。 (一对多)
*   一个 **Task** 属于唯一一个 **Workspace**。
*   一个 **Task** 有一个 **User** 作为 `creator` 和一个 **User** 作为 `assignee`。
*   一个 **User** (inviter) 可以为某个 **Workspace** 创建多个 **Invitation**。

