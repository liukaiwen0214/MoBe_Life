/**
 * 核心职责：声明项目中心的数据访问接口，负责组织数据库查询与持久化入口。
 * 所属业务模块：项目中心 / 数据访问层。
 * 重要依赖关系或外部约束：通常由 MyBatis 或 MyBatis-Plus 生成代理实现，方法签名需与 XML 或框架约定对应。
 */
package com.mobe.mobe_life_backend.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mobe.mobe_life_backend.project.entity.MobeProject;
import com.mobe.mobe_life_backend.project.vo.ProjectListItemVO;

import java.util.List;
import com.mobe.mobe_life_backend.project.vo.ProjectDetailVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MobeProjectMapper extends BaseMapper<MobeProject> {
        List<ProjectListItemVO> selectProjectList(
                        @Param("userId") Long userId,
                        @Param("keyword") String keyword,
                        @Param("includeCompleted") Boolean includeCompleted,
                        @Param("offset") long offset,
                        @Param("pageSize") int pageSize);

        Long countProjectList(
                        @Param("userId") Long userId,
                        @Param("keyword") String keyword,
                        @Param("includeCompleted") Boolean includeCompleted);

        ProjectDetailVO selectProjectBaseDetail(@Param("id") Long id, @Param("userId") Long userId);
}
