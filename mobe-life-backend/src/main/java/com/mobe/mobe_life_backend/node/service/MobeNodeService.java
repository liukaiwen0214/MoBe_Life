package com.mobe.mobe_life_backend.node.service;

import com.mobe.mobe_life_backend.common.response.PageResult;
import com.mobe.mobe_life_backend.node.dto.NodeListQueryDTO;
import com.mobe.mobe_life_backend.node.vo.NodeDetailVO;
import com.mobe.mobe_life_backend.node.vo.NodeListItemVO;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 节点服务接口
 */
public interface MobeNodeService {
  PageResult<NodeListItemVO> getNodeList(NodeListQueryDTO queryDTO);

  NodeDetailVO getNodeDetail(Long id);

  void completeNode(Long id, HttpServletRequest request);
}
