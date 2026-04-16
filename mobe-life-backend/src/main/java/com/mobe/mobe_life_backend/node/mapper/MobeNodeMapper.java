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
