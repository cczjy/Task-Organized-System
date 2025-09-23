### Git全面操作指南：从入门到高效团队协作

---

### 第零部分：安装与初次配置

在开始使用Git之前，你需要先在你的电脑上安装它。

#### 1. 安装Git

**Windows:**

*   访问Git官网的下载页面 (git-scm.com/download/win)。
*   下载会自动开始。
*   下载完成后，运行安装程序。
*   在安装向导中，直接点击 "Next" 使用默认设置即可，这些选项对大多数用户来说都是合理的。

**macOS:**

*   **方式一 (推荐):** 如果你安装了 [Homebrew](https://brew.sh/)，只需打开终端（Terminal）并运行：
    ```bash
    brew install git
    ```
*   **方式二:** 从Git官网下载安装程序。
    *   访问 [https://git-scm.com/download/mac](https://git-scm.com/download/mac)。
    *   下载并运行安装包，根据提示完成安装。
*   **方式三 (Xcode):** 如果你安装了Xcode或者它的命令行工具，Git可能已经安装好了。 你可以在终端里尝试运行 `git --version` 来检查。如果未安装，系统会自动提示你进行安装。

**Linux (以Debian/Ubuntu为例):**

*   打开终端，首先更新你的包列表：
    ```bash
    sudo apt-get update
    ```
*   然后，安装Git：
    ```bash
    sudo apt-get install git
    ```
*   对于其他Linux发行版（如Fedora, CentOS），可以使用它们各自的包管理器（如 `dnf` 或 `yum`）来安装。

#### 2. 验证安装

安装完成后，打开你的终端（在Windows上可以是Git Bash、CMD或PowerShell）并输入以下命令来确认Git已成功安装：
```bash
git --version
```
如果安装成功，它会显示出安装的Git版本号。

#### 3. 初次配置

安装完Git后，最重要的一步是设置你的**用户名**和**邮箱地址**。每一次你提交代码，这个信息都会被记录下来，以表明是谁进行的提交。

在终端里运行以下两条命令，请将`"Your Name"`和`"you@example.com"`替换成你自己的信息：
```bash
git config --global user.name "Your Name"
git config --global user.email "you@example.com"
```
`--global` 标志表示这个配置对你电脑上所有的Git仓库都有效。这只需要设置一次。

---

### Git核心概念

1.  **仓库 (Repository / Repo)**: 就是你的项目文件夹。Git会追踪这个文件夹里所有文件的修改历史。
2.  **三个区域**:
    *   **工作区 (Working Directory)**: 你在电脑上能看到的、能直接编辑的项目文件夹。
    *   **暂存区 (Staging Area)**: 一个临时的存储区域，像一个“购物车”。你把工作区里修改好的文件（“商品”）通过 `git add` 放进这里，准备一次性提交。
    *   **本地仓库 (Local Repository)**: 最终的“仓库”。当你执行 `git commit` 时，Git会把暂存区里的所有内容生成一个“快照”（版本记录），永久保存在本地仓库中。
3.  **远程仓库 (Remote Repository)**: 托管在云服务器上的代码仓库，比如GitHub、GitLab。它是团队成员共享代码、协同工作的地方。
4.  **分支 (Branch)**: 像一条独立的时间线。默认有一条主分支 `main` (或 `master`)。当你开发新功能或修复Bug时，应该创建一个新分支，在这条新时间线上工作，完成后再合并回主分支。**这是Git团队协作的精髓**。

---

### 第一部分：个人开发工作流

这是最基础的流程，也是所有复杂流程的基石。

#### 流程图
`修改代码` -> `git add` -> `git commit` -> `git push`

#### 步骤详解

1.  **创建与初始化项目**
    *   **全新项目 (本地开始)**:
        1.  在你的电脑上创建一个新文件夹。
        2.  进入该文件夹，运行 `git init`，这会创建一个新的本地Git仓库。
    *   **全新项目 (从GitHub/GitLab开始)**:
        1.  在GitHub或类似平台上，点击“New repository”按钮创建一个新的远程仓库。
        2.  你可以选择使用一个README文件来初始化它。
        3.  创建后，复制仓库地址，然后在你的电脑上运行 `git clone <仓库地址>`，将项目克隆到本地。
    *   **已有项目**: 直接运行 `git clone <仓库地址>`。

2.  **核心工作循环 (每天重复)**
    *   **第1步：查看状态**
        ```bash
        git status
        ```        这个命令是你最好的朋友，它会告诉你当前哪些文件被修改了、哪些文件已暂存。

    *   **第2步：修改代码**
        像平常一样，在你的编辑器里添加、修改、删除文件。

    *   **第3步：添加到暂存区**
        把你完成的修改“放进购物车”。
        ```bash
        # 添加某个文件
        git add <文件名>
        
        # 添加所有已修改和新创建的文件
        git add .
        ```

    *   **第4步：提交到本地仓库**
        给这次修改创建一个“存档点”，并写下清晰的说明。
        ```bash
        git commit -m "一条清晰的提交信息，例如：feat: 完成用户登录功能"
        ```
        > **最佳实践**: 提交信息应该简明扼要地说明你“做了什么”。

3.  **同步到远程仓库**
    *   **关联远程仓库** (对于本地初始化的仓库，只需一次)
        ```bash
        git remote add origin <你的远程仓库地址>
        ```    *   **推送更新**
        把本地仓库的提交记录推送到远程服务器。
        ```bash
        # -u 参数会在第一次推送时建立本地main和远程main的关联，以后只需 git push
        git push -u origin main
        ```

---

### 第二部分：指定不上传的文件 (`.gitignore`)

在项目中，有很多文件是不需要（也不应该）被Git管理的，比如：
*   编译产生的文件（如 `.class`, `.o`）
*   依赖包文件夹（如 `node_modules/`）
*   IDE或编辑器的配置文件（如 `.idea/`, `.vscode/`）
*   包含敏感信息的文件（如 `config.ini`，里面有数据库密码）
*   操作系统自动生成的文件（如 `Thumbs.db`, `.DS_Store`）

为了让Git忽略这些文件，你需要在项目的根目录下创建一个名为 `.gitignore` 的文件。

#### 如何使用 `.gitignore`

1.  **创建文件**: 在你的项目根目录下（与 `.git` 文件夹同级），创建一个名为 `.gitignore` 的文件。
2.  **编辑规则**: 打开这个文件，每一行写入一个你想要忽略的文件或文件夹的匹配规则。
3.  **保存并提交**: 保存 `.gitignore` 文件，然后像其他文件一样，将它 `add` 并 `commit` 到你的仓库中。这样，团队里的每个人都能共享这套忽略规则。

#### `.gitignore` 规则示例

```gitignore
# 这是一个注释，以'#'开头的行会被忽略

# 忽略所有 .log 文件
*.log

# 忽略一个特定的文件
credentials.json

# 忽略整个文件夹
node_modules/
dist/

# 忽略 .idea 文件夹下的所有内容
.idea/

# 但是，不要忽略 .idea 文件夹下的一个特定文件
! .idea/important_setting.xml```

#### 重要：如果文件已经被Git跟踪了怎么办？

`.gitignore` 只能忽略那些**还未被跟踪**的文件。如果你不小心把一个本应忽略的文件（比如 `config.ini`）提交到了仓库，你需要先从Git的跟踪列表中移除它，然后再添加到 `.gitignore`。

执行以下命令：
```bash
# 1. 从Git的暂存区和跟踪列表中移除文件，但保留在你的工作区（本地文件夹里）
git rm --cached config.ini

# 2. 现在，将 'config.ini' 添加到 .gitignore 文件中
#    (手动编辑 .gitignore 文件并添加一行 'config.ini')

# 3. 提交 .gitignore 的更改和文件的移除操作
git add .gitignore
git commit -m "feat: Stop tracking config.ini"
```
这样，这个文件就不会再出现在之后的提交中了。

---

### 第三部分：团队协作工作流

这是实际工作中最常用、最重要的流程，核心是 **分支策略**。

#### 核心原则
1.  **永远不要直接在 `main` 分支上开发！**
2.  **任何新工作（功能、修复）都必须创建新分支。**
3.  **在推送自己代码前，先拉取远程最新代码。**

#### 流程图
`拉取最新代码` -> `创建并切换分支` -> `(个人工作循环)` -> `推送分支` -> `创建Pull Request` -> `合并与清理`

#### 步骤详解

1.  **开始新工作前：同步最新代码**
    确保你的本地 `main` 分支和远程仓库保持同步。
    ```bash
    git switch main
    git pull origin main
    ```

2.  **创建并切换到新分支**
    ```bash
    git switch -c feature/user-profile
    ```
    > **命名规范**: `feature/功能名`、`fix/问题名` 是很好的习惯。

3.  **在新分支上进行开发**
    重复 **个人开发工作流** 中的核心循环：`git add .` -> `git commit -m "..."`。

4.  **推送你的分支到远程仓库**
    ```bash
    git push --set-upstream origin feature/user-profile
    ```

5.  **创建合并请求 (Pull Request / PR)**
    *   推送到远程后，去GitHub或GitLab的页面创建 "Pull Request"。
    *   PR是请求项目维护者将你的分支合并到 `main` 分支，也是进行 **代码审查 (Code Review)** 的地方。

6.  **审查、合并与清理**
    *   **审查**: 同事审查代码并提出修改意见。
    *   **合并**: 审查通过后，项目维护者点击“Merge”按钮。
    *   **清理**: 合并完成后，删除无用的分支。
        ```bash
        # 1. 回到主分支并拉取最新代码
        git switch main
        git pull origin main
        
        # 2. 删除本地分支
        git branch -d feature/user-profile
        
        # 3. (可选) 删除远程分支
        git push origin --delete feature/user-profile
        ```

---

### 第四部分：合并修改与处理冲突

1.  **合并分支 (Git Merge)**
    ```bash
    git switch main
    git merge feature/user-profile
    ```

2.  **变基 (Git Rebase)**
    `git rebase` 可以让提交历史更整洁，但不要对已共享的分支使用。
    ```bash
    git switch feature/user-profile
    git rebase main
    ```

3.  **处理合并冲突**
    *   当Git提示冲突时，打开冲突文件。
    *   手动编辑，删除 `<<<<<<<`, `=======`, `>>>>>>>` 标记，并保留最终想要的代码。
    *   `git add <文件名>`
    *   `git commit`

---

### 第五部分：必备的“后悔药”

1.  **代码改乱了，想撤销工作区的修改**
    ```bash
    git restore <文件名>
    ```

2.  **不小心 `git add` 了不想要的文件**
    ```bash
    git restore --staged <文件名>
    ```

3.  **`git commit` 提交了，但想修改提交信息**
    ```bash
    git commit --amend -m "新的提交信息"
    ```

4.  **`git commit` 提交了，但发现漏了几个文件**
    ```bash
    git add <漏掉的文件>
    git commit --amend --no-edit
    ```

5.  **想临时保存工作，切换到别的分支处理紧急问题**
    ```bash
    git stash       # 保存工作
    git stash pop   # 恢复工作
    ```