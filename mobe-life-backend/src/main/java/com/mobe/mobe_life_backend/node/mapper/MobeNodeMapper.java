/**
 * 核心职责：声明节点中心的数据访问接口，负责组织数据库查询与持久化入口。
 * 所属业务模块：节点中心 / 数据访问层。
 * 重要依赖关系或外部约束：通常由 MyBatis 或 MyBatis-Plus 生成代理实现，方法签名需与 XML 或框架约定对应。
 */
package com.mobe.mobe_life_backend.node.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mobe.mobe_life_backend.node.entity.MobeNode;
import org.apache.ibatis.annotations.Mapper;
import com.mobe.mobe_life_backend.node.vo.NodeDetailVO;
import com.mobe.mobe_life_backend.node.vo.NodeListItemVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MobeNodeMapper extends BaseMapper<MobeNode> {
  List<NodeListItemVO> selectNodeList(@Param("userId") Long userId,
      @Param("keyword") String keyword,
      @Param("ownerType") String ownerType,
      @Param("offset") Long offset,
      @Param("pageSize") Integer pageSize);

  Long countNodeList(@Param("userId") Long userId,
      @Param("keyword") String keyword,
      @Param("ownerType") String ownerType);

  NodeDetailVO selectNodeBaseDetail(@Param("id") Long id, @Param("userId") Long userId);
}
