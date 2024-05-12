# HAUT_Community

#### 介绍

软件开发综合实践 -- **Gathering Circle** 社交平台--后端代码

实现了一个校园社区交流平台，后端使用Java语言开发

#### 技术栈

后端主要使用Spring Boot + Mybatis Plus + MySQL + Redis + Spring Security + Swagger2 + Lombok + JWT Token等技术实现

#### 开发环境

JDK 17 + MySQL + Redis + Maven

#### 主要功能

- 帖子相关：发帖、浏览帖子、点赞、收藏、分享、评论
- 每日一句：投稿每日一句、管理员审批、邮件通知结果
- 公告：发布公告
- 系统管理：包括用户管理、角色管理、院系管理，使用Spring Security进行权限控制
- 标签管理：每个帖子都可以有至多三个标签

#### 功能结构图

![image](https://github.com/Ephemeral2333/HAUT_Community/assets/88269111/3147df80-4ce9-4672-80e6-db2be0947184)


#### 运行方法

1. 去application-dev.yml中更改MySQL的账号密码，并运行haut_community.sql
2. 更改Redis端口密码，一定记得启动Redis服务
3. 更改application-dev.yml中mail的账户密码改成自己的
4. 在FileServiceImpl中更改七牛云的accesskey和secret_key为自己的
5. 启动启动类
6. 账户名可以查看数据库，初始密码都为111111
