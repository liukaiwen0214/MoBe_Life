/**
 * 核心职责：统一承载分页查询结果，避免各业务模块重复定义分页响应结构。
 * 所属业务模块：公共基础设施 / 响应模型。
 * 重要依赖关系或外部约束：该对象通常会被序列化为接口响应体，字段语义应在各模块保持一致。
 */
package com.mobe.mobe_life_backend.common.response;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页返回结果。
 *
 * <p>设计初衷是把“总数 + 当前页 + 每页大小 + 当前页数据”抽象成稳定模型，
 * 让控制层和前端在不同业务模块里都能复用同一套分页语义。</p>
 *
 * <p>该模型本身不负责分页计算，只负责承载分页查询后的结果。</p>
 *
 * @param <T> 列表项类型
 */
@Data
public class PageResult<T> implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  /** 满足查询条件的总记录数。 */
  private Long total;

  /** 当前页码，从 1 开始计数。 */
  private Integer pageNum;

  /** 每页数量。 */
  private Integer pageSize;

  /** 当前页的数据列表。 */
  private List<T> list;

  /**
   * 构造一个完整分页结果。
   *
   * @param total 总记录数，允许为 null；会被兜底为 0。
   * @param pageNum 当前页码，允许为 null；会被兜底为 1。
   * @param pageSize 每页数量，允许为 null；会被兜底为 10。
   * @param list 当前页数据，允许为 null；会被兜底为空列表。
   * @return 规范化后的分页结果对象。
   */
  public static <T> PageResult<T> of(Long total, Integer pageNum, Integer pageSize, List<T> list) {
    PageResult<T> pageResult = new PageResult<>();
    pageResult.setTotal(total == null ? 0L : total);
    pageResult.setPageNum(pageNum == null ? 1 : pageNum);
    pageResult.setPageSize(pageSize == null ? 10 : pageSize);
    pageResult.setList(list == null ? Collections.emptyList() : list);
    return pageResult;
  }

  /**
   * 构造空分页结果。
   *
   * @param pageNum 当前页码。
   * @param pageSize 每页数量。
   * @return 总数为 0、列表为空的分页结果。
   */
  public static <T> PageResult<T> empty(Integer pageNum, Integer pageSize) {
    return of(0L, pageNum, pageSize, Collections.emptyList());
  }
}
