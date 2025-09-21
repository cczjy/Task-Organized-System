# Task-Organized-System
Man!
运行    mkdir -p .ssh
chmod 700 .ssh

ssh-keygen -t ed25519 -C "你邮箱" -f ~/.ssh/github_task_organized_system


nano ~/.ssh/config

Host github-task-system
  HostName github.com
  User git
  IdentityFile ~/.ssh/github_task_organized_system
  IdentitiesOnly yes

# 查看公钥
cat ~/.ssh/github_task_organized_system.pub
 ### ctrl x保存，公钥发给我
