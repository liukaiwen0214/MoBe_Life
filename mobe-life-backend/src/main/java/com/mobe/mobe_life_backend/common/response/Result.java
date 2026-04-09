/**
 * 文件级注释：
 * 核心职责：定义后端 API 的统一响应结构，封装业务数据、状态码和提示信息，实现前后端数据交换标准化。
 * 所属业务模块：系统基础设施层 - 通用响应模型。
 * 重要依赖：
 * - ErrorCode 常量类：定义业务状态码规范
 * - Lombok @Data：自动生成 getter/setter/toString
 * 设计约束：
 * - 所有 Controller 方法必须返回 Result<T> 包装类型，禁止直接返回裸数据
 * - HTTP 状态码固定 200，业务错误通过 Result.code 区分，简化前端错误处理
 */
package com.mobe.mobe_life_backend.common.response;

import com.mobe.mobe_life_backend.common.constant.ErrorCode;

import lombok.Data;

/**
 * 统一 API 响应封装类。
 *
 * <p>设计初衷：解决前后端交互中响应格式不一致的问题，提供标准化的成功/失败响应结构，
 * 使前端能够以统一方式处理所有接口返回，降低对接成本。</p>
 *
 * <p>在架构中的角色：表现层数据传输对象（DTO），作为 HTTP Response Body 的唯一载体，
 * 所有 Controller 方法返回值均为此类型或其泛型实例。</p>
 *
 * <p>响应结构规范：
 * <pre>
 *   // 成功响应示例
 *   {
 *     "code": 200,
 *     "message": "success",
 *     "data": { ... }
 *   }
 *
 *   // 失败响应示例
 *   {
 *     "code": 400,
 *     "message": "参数错误：邮箱格式不正确",
 *     "data": null
 *   }
 * </pre></p>
 *
 * <p>核心业务概念：
 * - code：业务状态码，与 HTTP 状态码解耦，200 表示业务成功，其他表示具体业务错误
 * - message：人类可读的错误描述，用于前端提示或日志记录
 * - data：实际业务数据，成功时非空，失败时通常为 null</p>
 *
 * <p>线程安全性：该类为纯数据载体（DTO），无状态，线程安全。
 * 但实例本身非线程安全（可变对象），不应在多线程间共享同一实例。</p>
 *
 * <p>使用示例：
 * <pre>
 *   // Controller 中返回成功结果
 *   @GetMapping("/user/{id}")
 *   public Result<UserVO> getUser(@PathVariable Long id) {
 *     UserVO user = userService.getById(id);
 *     return Result.success(user);
 *   }
 *
 *   // 返回自定义错误
 *   return Result.error(ErrorCode.PARAM_ERROR, "用户名不能为空");
 * </pre></p>
 *
 * @param <T> 业务数据类型，由具体接口决定，如 UserVO、List<OrderVO> 等
 */
@Data
public class Result<T> {

  /**
   * 业务状态码。
   *
   * <p>设计说明：
   * - 200：业务成功（注意：HTTP 状态码也是 200，但两者含义独立）
   * - 400：请求参数错误（如格式校验失败）
   * - 401：未认证或 Token 无效
   * - 403：无权限（认证通过但无权访问）
   * - 404：资源不存在
   * - 500：服务器内部错误
   * - 完整定义见 {@link ErrorCode}</p>
   *
   * <p>与 HTTP 状态码的区别：
   * - HTTP 200 + Result.code 400：请求到达服务器，但业务参数有误
   * - HTTP 404：接口路径不存在（不会进入 Controller）</p>
   */
  private Integer code;

  /**
   * 响应消息，人类可读的状态描述。
   *
   * <p>使用规范：
   * - 成功时通常为 "success" 或操作成功描述（如 "删除成功"）
   * - 失败时应包含具体错误原因，便于前端展示给用户
   * - 避免暴露敏感信息（如数据库结构、堆栈跟踪）</p>
   */
  private String message;

  /**
   * 业务数据载荷。
   *
   * <p>取值规则：
   * - 成功（code=200）：包含请求的业务数据，类型由泛型 T 决定
   * - 失败（code≠200）：通常为 null，或包含错误详情对象
   * - 列表查询：data 为 List<T>，空列表时返回 [] 而非 null</p>
   */
  private T data;

  /**
   * 创建成功响应（无数据）。
   *
   * <p>使用场景：
   * - 操作成功但无需返回数据（如删除、提交成功）
   * - 配合 {@link #data(T)} 链式调用设置数据</p>
   *
   * <p>示例：
   * <pre>
   *   return Result.success();  // {code: 200, message: "success", data: null}
   *   return Result.success().data(userVO);  // 链式设置数据
   * </pre></p>
   *
   * @param <T> 数据类型占位符
   * @return 状态码 200、message "success"、data null 的 Result 实例
   */
  public static <T> Result<T> success() {
    Result<T> result = new Result<>();
    result.setCode(ErrorCode.SUCCESS);
    result.setMessage("success");
    return result;
  }

  /**
   * 创建成功响应（携带数据）。
   *
   * <p>使用场景：
   * - 查询操作返回业务对象
   * - 创建操作返回新对象（含生成的 ID）</p>
   *
   * @param data 业务数据，可为任意类型（对象、列表、Map 等）
   *             - 允许值：任意非 null 值（null 时建议使用 success()）
   *             - 约束：应为可序列化为 JSON 的类型
   * @param <T>  数据类型
   * @return 状态码 200、包含传入数据的 Result 实例
   */
  public static <T> Result<T> success(T data) {
    Result<T> result = new Result<>();
    result.setCode(ErrorCode.SUCCESS);
    result.setMessage("success");
    result.setData(data);
    return result;
  }

  /**
   * 创建错误响应。
   *
   * <p>使用场景：
   * - 业务校验失败（如用户不存在、库存不足）
   * - 调用第三方服务失败</p>
   *
   * @param code    业务错误码，应使用 ErrorCode 中定义的常量
   *                - 允许值：400、401、403、404、500 等
   *                - 约束：禁止使用 200 表示错误
   * @param message 错误描述，应清晰说明失败原因
   *                - 示例："用户不存在"、"订单已过期"
   * @param <T>     数据类型占位符（错误时通常为 null）
   * @return 包含指定错误码和消息的 Result 实例，data 为 null
   */
  public static <T> Result<T> error(Integer code, String message) {
    Result<T> result = new Result<>();
    result.setCode(code);
    result.setMessage(message);
    return result;
  }

  /**
   * 链式设置响应消息。
   *
   * <p>设计意图：支持流畅接口（Fluent Interface），便于在返回前动态修改消息。</p>
   *
   * <p>使用示例：
   * <pre>
   *   return Result.success(user).message("欢迎回来，" + user.getNickname());
   * </pre></p>
   *
   * @param message 自定义消息内容
   * @return 当前 Result 实例（支持链式调用）
   */
  public Result<T> message(String message) {
    this.setMessage(message);
    return this;
  }

  /**
   * 链式设置响应数据。
   *
   * <p>设计意图：与 {@link #success()} 配合，支持先创建空成功响应再填充数据。</p>
   *
   * <p>使用示例：
   * <pre>
   *   Result<List<UserVO>> result = Result.success();
   *   if (condition) {
   *     result.data(filteredList);
   *   }
   *   return result;
   * </pre></p>
   *
   * @param data 业务数据
   * @return 当前 Result 实例（支持链式调用）
   */
  public Result<T> data(T data) {
    this.setData(data);
    return this;
  }
}
