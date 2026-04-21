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
