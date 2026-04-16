package com.mobe.mobe_life_backend.common.response;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页返回结果
 *
 * @param <T> 列表项类型
 */
@Data
public class PageResult<T> implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * 总记录数
   */
  private Long total;

  /**
   * 当前页码
   */
  private Integer pageNum;

  /**
   * 每页数量
   */
  private Integer pageSize;

  /**
   * 列表数据
   */
  private List<T> list;

  public static <T> PageResult<T> of(Long total, Integer pageNum, Integer pageSize, List<T> list) {
    PageResult<T> pageResult = new PageResult<>();
    pageResult.setTotal(total == null ? 0L : total);
    pageResult.setPageNum(pageNum == null ? 1 : pageNum);
    pageResult.setPageSize(pageSize == null ? 10 : pageSize);
    pageResult.setList(list == null ? Collections.emptyList() : list);
    return pageResult;
  }

  public static <T> PageResult<T> empty(Integer pageNum, Integer pageSize) {
    return of(0L, pageNum, pageSize, Collections.emptyList());
  }
}