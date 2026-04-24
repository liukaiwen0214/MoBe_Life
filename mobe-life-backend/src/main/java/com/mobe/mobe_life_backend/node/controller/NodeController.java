/**
 * 核心职责：对外暴露节点中心相关接口，负责接收请求并调用对应业务能力。
 * 所属业务模块：节点中心 / 控制层。
 * 重要依赖关系或外部约束：依赖 Spring MVC 与服务层；通常不承载复杂业务逻辑。
 */
package com.mobe.mobe_life_backend.node.controller;

import com.mobe.mobe_life_backend.common.response.PageResult;
import com.mobe.mobe_life_backend.common.response.Result;
import com.mobe.mobe_life_backend.node.dto.NodeCreateDTO;
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
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mobe.mobe_life_backend.node.dto.NodeUpdateDTO;
import org.springframework.web.bind.annotation.PutMapping;
import com.mobe.mobe_life_backend.node.dto.NodeDeleteDTO;
import org.springframework.web.bind.annotation.DeleteMapping;

@Slf4j
@RestController
@RequestMapping("/api/nodes")
@Tag(name = "节点模块", description = "节点相关接口")
public class NodeController {

  @Resource
  private MobeNodeService mobeNodeService;

  /**
   * 获取NodeList。
   *
   * @return 返回对应结果。
   */
  @GetMapping
  @Operation(summary = "获取节点列表", description = "分页获取当前登录用户的节点列表")
  public Result<PageResult<NodeListItemVO>> getNodeList(@Valid NodeListQueryDTO queryDTO) {
    return Result.success(mobeNodeService.getNodeList(queryDTO));
  }

  /**
   * 获取节点详情。
   *
   * @return 返回节点详情结果。
   */
  @GetMapping("/{id}")
  @Operation(summary = "获取节点详情", description = "获取当前登录用户的节点详情")
  public Result<NodeDetailVO> getNodeDetail(
      @Parameter(description = "节点ID") @PathVariable Long id) {
    return Result.success(mobeNodeService.getNodeDetail(id));
  }

  /**
   * 将节点标记为完成。
   *
   * @return 成功时返回 `true`。
   */
  @Operation(summary = "赋予节点完成态")
  @PostMapping("/{id}/complete")
  public Result<Boolean> completeNode(
      @Parameter(description = "节点ID", required = true) @PathVariable Long id,
      HttpServletRequest request) {
    mobeNodeService.completeNode(id, request);
    return Result.success(true);
  }

  /**
   * 创建Node。
   *
   * @return 返回创建结果。
   */
  @Operation(summary = "新增节点")
  @PostMapping
  public Result<Long> createNode(@RequestBody NodeCreateDTO dto) {
    log.info("NodeController createNode payload = {}", dto);
    return Result.success(mobeNodeService.createNode(dto));
  }

  /**
   * 更新节点信息。
   *
   * @return 成功时返回 `true`。
   */
  @Operation(summary = "编辑节点")
  @PutMapping("/{id}")
  public Result<Boolean> updateNode(
      @Parameter(description = "节点ID", required = true) @PathVariable Long id,
      @RequestBody NodeUpdateDTO dto) {
    log.info("NodeController updateNode id = {}, payload = {}", id, dto);
    mobeNodeService.updateNode(id, dto);
    return Result.success(true);
  }

  /**
   * 删除节点。
   *
   * @return 成功时返回 `true`。
   */
  @Operation(summary = "删除节点")
  @DeleteMapping("/{id}")
  public Result<Boolean> deleteNode(
      @Parameter(description = "节点ID", required = true) @PathVariable Long id,
      @RequestBody(required = false) NodeDeleteDTO dto) {
    log.info("NodeController deleteNode id = {}, payload = {}", id, dto);
    mobeNodeService.deleteNode(id, dto);
    return Result.success(true);
  }
}
