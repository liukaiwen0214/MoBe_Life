/**
 * 核心职责：定义系统统一业务错误码，保证前后端和异常处理层共享同一套语义。
 * 所属业务模块：公共基础设施 / 错误码规范。
 * 重要依赖关系或外部约束：所有 `Result` 和 `BusinessException` 都应复用这里的常量，避免出现同义不同码。
 */
package com.mobe.mobe_life_backend.common.constant;

/**
 * 业务错误码常量。
 *
 * <p>设计初衷是把“错误语义”与“业务实现”解耦，让前端可以只根据稳定错误码决定提示和跳转逻辑，
 * 而不用依赖易变的错误消息文本。</p>
 *
 * <p>线程安全性：纯常量类，天然线程安全。</p>
 */
public final class ErrorCode {

  private ErrorCode() {
  }

  /** 业务成功。 */
  public static final int SUCCESS = 0;

  /** 参数校验或业务前置条件不满足。 */
  public static final int PARAMS_ERROR = 40000;

  /** 当前请求缺少有效登录态。 */
  public static final int NOT_LOGIN_ERROR = 40100;

  /** 已登录但不具备访问目标资源的权限。 */
  public static final int NO_AUTH_ERROR = 40300;

  /** 请求的业务资源不存在。 */
  public static final int NOT_FOUND_ERROR = 40400;

  /** 系统内部未分类异常。 */
  public static final int SYSTEM_ERROR = 50000;
}
