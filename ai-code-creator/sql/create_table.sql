-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin',
    editTime     datetime     default CURRENT_TIMESTAMP not null comment '编辑时间',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    UNIQUE KEY uk_userAccount (userAccount),
    INDEX idx_userName (userName)
) comment '用户' collate = utf8mb4_unicode_ci;


ALTER TABLE user
ADD COLUMN vipExpireTime DATETIME NULL COMMENT '会员过期时间',
ADD COLUMN vipCode VARCHAR(128) NULL COMMENT '会员兑换码',
ADD COLUMN vipNumber BIGINT NULL COMMENT '会员编号',
ADD COLUMN shareCode VARCHAR(20) DEFAULT NULL COMMENT '分享码',
ADD COLUMN password varchar(512)   DEFAULT NULL COMMENT '原密码',
ADD COLUMN inviteUser BIGINT DEFAULT NULL COMMENT '邀请用户 id';

