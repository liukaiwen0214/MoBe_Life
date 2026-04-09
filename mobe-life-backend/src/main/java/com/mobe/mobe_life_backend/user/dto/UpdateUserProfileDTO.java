/**
 * 核心职责：承接当前用户资料更新请求。
 * 所属业务模块：用户中心 / 资料维护。
 * 重要依赖关系或外部约束：当前实现采用“只更新非 null 字段”的部分更新策略，因此 `null` 表示“不修改”而不是“清空”。
 */
package com.mobe.mobe_life_backend.user.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 用户资料更新请求体。
 *
 * <p>设计初衷是让前端可以按字段增量提交，而不是每次都回传完整用户档案，降低覆盖式更新误伤未编辑字段的风险。</p>
 */
@Data
public class UpdateUserProfileDTO {

  /** 用户昵称；允许为 null，表示本次不修改。 */
  private String nickname;

  /** 用户头像地址；允许为 null，表示本次不修改。 */
  private String avatar;

  /** 性别编码；允许为 null，表示本次不修改。 */
  private Integer gender;

  /** 出生日期；允许为 null，表示本次不修改。 */
  private LocalDate birthday;
}
