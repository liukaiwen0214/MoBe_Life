/**
 * 核心职责：定义后端统一响应结构，约束所有接口以相同格式返回结果。
 * 所属业务模块：公共基础设施 / 响应模型。
 * 重要依赖关系或外部约束：成功码采用项目内部约定 `0`，与 HTTP 状态码语义分离。
 */
package com.mobe.mobe_life_backend.common.response;

import com.mobe.mobe_life_backend.common.constant.ErrorCode;
import lombok.Data;

/**
 * 统一响应体。
 *
 * <p>设计初衷是让前端只处理一套固定的 `code/message/data` 协议，
 * 这样无论接口属于认证、用户还是文件模块，都能复用同一套错误处理逻辑。</p>
 *
 * <p>线程安全性：对象本身可变，不应跨线程共享；通常只在单次请求链路中短暂创建。</p>
 *
 * @param <T> 业务数据类型。
 */
@Data
public class Result<T> {

  /** 业务状态码，`0` 表示成功，其余值表示具体失败原因。 */
  private Integer code;

  /** 响应消息；成功时通常为 `success`，失败时为面向调用方的可读说明。 */
  private String message;

  /** 业务数据；失败场景下通常为 null。 */
  private T data;

  /**
   * 创建空成功响应。
   *
   * @param <T> 数据类型。
   * @return 成功结果，不返回 null。
   */
  public static <T> Result<T> success() {
    Result<T> result = new Result<>();
    result.setCode(ErrorCode.SUCCESS);
    result.setMessage("success");
    return result;
  }

  /**
   * 创建携带数据的成功响应。
   *
   * @param data 业务数据，允许为 null。
   * @param <T> 数据类型。
   * @return 成功结果，不返回 null。
   */
  public static <T> Result<T> success(T data) {
    Result<T> result = new Result<>();
    result.setCode(ErrorCode.SUCCESS);
    result.setMessage("success");
    result.setData(data);
    return result;
  }

  /**
   * 创建失败响应。
   *
   * @param code 业务错误码，不允许为 null。
   * @param message 错误消息，不允许为 null。
   * @param <T> 数据类型。
   * @return 失败结果，不返回 null。
   */
  public static <T> Result<T> error(Integer code, String message) {
    Result<T> result = new Result<>();
    result.setCode(code);
    result.setMessage(message);
    return result;
  }

  /**
   * 链式修改消息。
   *
   * @param message 新消息内容。
   * @return 当前对象自身。
   */
  public Result<T> message(String message) {
    this.setMessage(message);
    return this;
  }

  /**
   * 链式修改数据。
   *
   * @param data 新业务数据。
   * @return 当前对象自身。
   */
  public Result<T> data(T data) {
    this.setData(data);
    return this;
  }
}
