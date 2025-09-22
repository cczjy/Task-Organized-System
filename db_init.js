const client = new Client({
  user: "user_name",
  host: "localhost",
  database: "db_name",
  password: "123",
  port: 5432,
});


async function init_system_DB() {
  try {
    await client.connect();

        // users 表
    await client.query(`
      CREATE TABLE IF NOT EXISTS users (
        id SERIAL PRIMARY KEY,
        username VARCHAR(50) NOT NULL,
        password VARCHAR(100) NOT NULL
      );
    `);
        //groups表
    await client.query(`
      CREATE TABLE IF NOT EXISTS groups (
        id SERIAL PRIMARY KEY,
        groupname VARCHAR(50) NOT NULL
      );
    `);

    // GroupMembers表
    await client.query(`
      CREATE TABLE IF NOT EXISTS GroupMembers (
        group_id INT,
        user_id INT,
        role VARCHAR(20),
        PRIMARY KEY (group_id, user_id),
        FOREIGN KEY (group_id) REFERENCES groups(group_id),
        FOREIGN KEY (user_id) REFERENCES users(user_id)
      );
    `);

    console.log("✅ 数据库表初始化完成");
  } catch (err) {
    console.error("❌ 数据库初始化失败:", err);
  } finally {
    await client.end();
  }
}

init_system_DB();

async function init_group_DB() {
  try {
    await client.connect();

    // message表
    await client.query(`
      CREATE TABLE IF NOT EXISTS message (
        id SERIAL PRIMARY KEY,
        name VARCHAR(50) NOT NULL,
        content TEXT NOT NULL,
        type VARCHAR(128) NOT NULL,
        group_id INT NOT NULL,
        sender_id INT NOT NULL,
        create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        FOREIGN KEY (group_id) REFERENCES groups(group_id),
        FOREIGN KEY (sender_id) REFERENCES users(user_id)
      );
    `);
        // tasks 表
    await client.query(`
      CREATE TABLE IF NOT EXISTS tasks (
        id INT PRIMARY KEY,
        ddl TIMESTAMP NOT NULL
        filename VARCHAR(255),
        file_path VARCHAR(500),
        FOREIGN KEY (id) REFERENCES users(id)
      );
    `);
        //votes表
    await client.query(`
      CREATE TABLE IF NOT EXISTS votes (
        id INT PRIMARY KEY,
        ddl TIMESTAMP NOT NULL,
        FOREIGN KEY (id) REFERENCES users(id)
      );
    `);
    //vote需要好几把多表好几把复杂我后面再写
    
    //message_users表
    await client.query(`
      CREATE TABLE MessageUser (
        message_id INT,
        user_id INT,
        status VARCHAR(20), -- 已读、未读
        PRIMARY KEY (message_id, user_id),
        FOREIGN KEY (message_id) REFERENCES message(id),
        FOREIGN KEY (user_id) REFERENCES users(id)
      );
    `);
    //task_users表
    await client.query(`
      CREATE TABLE TaskUser (
        message_id INT,
        user_id INT,
        status VARCHAR(20), -- 已提交、未提交
        responded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        content TEXT NOT NULL
        filename VARCHAR(255),
        file_path VARCHAR(500),
        PRIMARY KEY (message_id, user_id),
        FOREIGN KEY (message_id) REFERENCES Message(id),
        FOREIGN KEY (user_id) REFERENCES users(id)
      );
    `);




    console.log("✅ 数据库表初始化完成");
  } catch (err) {
    console.error("❌ 数据库初始化失败:", err);
  } finally {
    await client.end();
  }
}

init_system_DB();
init_group_DB();
