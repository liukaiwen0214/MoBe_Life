package com.mobe.mobe_life_backend.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mobe.mobe_life_backend.user.entity.MobeUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MobeUserMapper extends BaseMapper<MobeUser> {
}