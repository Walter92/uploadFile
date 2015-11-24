实现了一个文件上传器。

功能：用户可以注册和登录，登录成功之后可以上传文件。
用户数据和文件数据都在mysql数据库中，数据库中用Blob存储文件数据。
基本功能已经实现，用户可以注册和登录以及上传文件。

目前找出的bug：

1.新用户注册后不能立即登录，需要重启客户端才能成功登录，但是数据库中却已经有了新用户信息。
2.上传文件是，客户端发送完毕之后，服务端仍然在阻塞等待读入客户端上传文件数据。如果继续操作客户端，服务端才会显示上传成功。
3.发送操作信息时，第一次发送服务端接收会乱码，而后几次发送不会乱码。

建表sql语句：
用户表：
CREATE TABLE `tb_user` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `passwd` varchar(50) NOT NULL,
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8；


文件表：
CREATE TABLE `user_files` (
  `fid` int(11) NOT NULL AUTO_INCREMENT,
  `file` mediumblob,
  `owner` int(11) DEFAULT NULL,
  PRIMARY KEY (`fid`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8；
