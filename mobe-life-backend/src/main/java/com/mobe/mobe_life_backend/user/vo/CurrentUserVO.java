/**
 * 核心职责：向前端返回当前登录用户资料与若干派生状态。
 * 所属业务模块：用户中心 / 当前用户视图。
 * 重要依赖关系或外部约束：字段结构需要兼容前端个人中心页面的数据读取方式。
 */
package com.mobe.mobe_life_backend.user.vo;

import lombok.Data;

/**
 * 当前用户资料视图。
 *
 * <p>设计初衷是不把数据库实体原样暴露给前端，而是补充前端真正需要的派生布尔值，
 * 减少客户端自行推导“是否已绑定手机号/邮箱、是否已设置密码”的重复逻辑。</p>
 */
@Data
public class CurrentUserVO {

  private Long id;
  private String openid;
  private String phone;
  private String email;
  private String nickname;
  private String avatar;
  private Integer gender;
  private Integer status;

  /** 出生日期字符串；若未设置则可能为 null。 */
  private String birthday;

  /** 是否已设置本地密码。 */
  private Boolean hasPassword;

  /** 是否已绑定手机号。 */
  private Boolean hasPhone;

  /** 是否已绑定邮箱。 */
  private Boolean hasEmail;
}
