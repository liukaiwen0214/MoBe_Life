/**
 * 界定节点中心的业务能力边界，供控制层稳定调用。
 * 模块：节点中心 / 服务接口。
 * 约束：实现通常位于 `impl` 包中，接口方法语义应尽量稳定。
 */
package com.mobe.mobe_life_backend.node.service;

import com.mobe.mobe_life_backend.common.response.PageResult;
import com.mobe.mobe_life_backend.node.dto.NodeCreateDTO;
import com.mobe.mobe_life_backend.node.dto.NodeDeleteDTO;
import com.mobe.mobe_life_backend.node.dto.NodeListQueryDTO;
import com.mobe.mobe_life_backend.node.dto.NodeUpdateDTO;
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

  Long createNode(NodeCreateDTO dto);

  void updateNode(Long id, NodeUpdateDTO dto);

  void deleteNode(Long id, NodeDeleteDTO dto);
}
