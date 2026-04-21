package com.mobe.mobe_life_backend.node.controller;

import com.mobe.mobe_life_backend.common.response.PageResult;
import com.mobe.mobe_life_backend.common.response.Result;
import com.mobe.mobe_life_backend.node.dto.NodeListQueryDTO;
import com.mobe.mobe_life_backend.node.service.MobeNodeService;
import com.mobe.mobe_life_backend.node.vo.NodeDetailVO;
import com.mobe.mobe_life_backend.node.vo.NodeListItemVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/nodes")
@Tag(name = "节点模块", description = "节点相关接口")
public class NodeController {

  @Resource
  private MobeNodeService mobeNodeService;

  @GetMapping
  @Operation(summary = "获取节点列表", description = "分页获取当前登录用户的节点列表")
  public Result<PageResult<NodeListItemVO>> getNodeList(@Valid NodeListQueryDTO queryDTO) {
    return Result.success(mobeNodeService.getNodeList(queryDTO));
  }

  @GetMapping("/{id}")
  @Operation(summary = "获取节点详情", description = "获取当前登录用户的节点详情")
  public Result<NodeDetailVO> getNodeDetail(
      @Parameter(description = "节点ID") @PathVariable Long id) {
    return Result.success(mobeNodeService.getNodeDetail(id));
  }

  @Operation(summary = "赋予节点完成态")
  @PostMapping("/{id}/complete")
  public Result<Boolean> completeNode(
      @Parameter(description = "节点ID", required = true) @PathVariable Long id,
      HttpServletRequest request) {
    mobeNodeService.completeNode(id, request);
    return Result.success(true);
  }
}