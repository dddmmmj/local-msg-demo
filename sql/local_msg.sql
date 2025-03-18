/*
 Navicat Premium Dump SQL

 Source Server         : localmysql
 Source Server Type    : MySQL
 Source Server Version : 80040 (8.0.40)
 Source Host           : localhost:3306
 Source Schema         : local_msg

 Target Server Type    : MySQL
 Target Server Version : 80040 (8.0.40)
 File Encoding         : 65001

 Date: 18/03/2025 09:55:16
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for local_message
-- ----------------------------
DROP TABLE IF EXISTS `local_message`;
CREATE TABLE `local_message`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `deleted` tinyint NOT NULL DEFAULT 0,
  `req_snapshot` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请求快照参数json',
  `status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '状态 INIT, FAIL, SUCCESS',
  `next_retry_time` bigint NOT NULL COMMENT '下一次重试的时间',
  `retry_times` int NOT NULL DEFAULT 0 COMMENT '已经重试的次数',
  `max_retry_times` int NOT NULL COMMENT '最大重试次数',
  `fail_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '执行失败的信息',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '本地消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of local_message
-- ----------------------------
INSERT INTO `local_message` VALUES (1, '2025-03-18 09:48:23', '2025-03-18 09:51:55', 0, '{\"args\":\"[{\\\"kfc\\\":\\\"3570759c-809c-4f75-b248-580a5e4ee09c\\\"},{\\\"age\\\":18,\\\"name\\\":\\\"yoyocraft\\\"}]\",\"className\":\"org.ddmj.controller.LocalMessageTestController\",\"methodName\":\"testRetryMethod\",\"paramTypes\":\"[\\\"java.util.Map\\\",\\\"org.ddmj.controller.LocalMessageTestController$ModelData\\\"]\"}', 'SUCCESS', 1742262623056, 1, 3, NULL);
INSERT INTO `local_message` VALUES (2, '2025-03-18 09:50:25', '2025-03-18 09:50:25', 0, '{\"args\":\"[{\\\"kfc\\\":\\\"91820361-86b8-4643-a0bc-20d85d6c7249\\\"},{\\\"age\\\":18,\\\"name\\\":\\\"yoyocraft\\\"}]\",\"className\":\"org.ddmj.controller.LocalMessageTestController\",\"methodName\":\"testRetryMethod\",\"paramTypes\":\"[\\\"java.util.Map\\\",\\\"org.ddmj.controller.LocalMessageTestController$ModelData\\\"]\"}', 'SUCCESS', 1742262745882, 0, 3, NULL);
INSERT INTO `local_message` VALUES (3, '2025-03-18 09:50:27', '2025-03-18 09:50:27', 0, '{\"args\":\"[{\\\"kfc\\\":\\\"b0050c32-0de0-4890-a662-64f28e32db1e\\\"},{\\\"age\\\":18,\\\"name\\\":\\\"yoyocraft\\\"}]\",\"className\":\"org.ddmj.controller.LocalMessageTestController\",\"methodName\":\"testRetryMethod\",\"paramTypes\":\"[\\\"java.util.Map\\\",\\\"org.ddmj.controller.LocalMessageTestController$ModelData\\\"]\"}', 'SUCCESS', 1742262747899, 0, 3, NULL);
INSERT INTO `local_message` VALUES (4, '2025-03-18 09:50:28', '2025-03-18 09:50:28', 0, '{\"args\":\"[{\\\"kfc\\\":\\\"6ba289e0-22fb-4440-aac8-742226c974ee\\\"},{\\\"age\\\":18,\\\"name\\\":\\\"yoyocraft\\\"}]\",\"className\":\"org.ddmj.controller.LocalMessageTestController\",\"methodName\":\"testRetryMethod\",\"paramTypes\":\"[\\\"java.util.Map\\\",\\\"org.ddmj.controller.LocalMessageTestController$ModelData\\\"]\"}', 'SUCCESS', 1742262748896, 0, 3, NULL);
INSERT INTO `local_message` VALUES (5, '2025-03-18 09:50:29', '2025-03-18 09:53:55', 0, '{\"args\":\"[{\\\"kfc\\\":\\\"87c6743b-6390-46df-b0c5-ff72440683e2\\\"},{\\\"age\\\":18,\\\"name\\\":\\\"yoyocraft\\\"}]\",\"className\":\"org.ddmj.controller.LocalMessageTestController\",\"methodName\":\"testRetryMethod\",\"paramTypes\":\"[\\\"java.util.Map\\\",\\\"org.ddmj.controller.LocalMessageTestController$ModelData\\\"]\"}', 'RETRY', 1742263075143, 2, 3, NULL);

SET FOREIGN_KEY_CHECKS = 1;
