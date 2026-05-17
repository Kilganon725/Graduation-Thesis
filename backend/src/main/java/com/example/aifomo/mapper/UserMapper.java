package com.example.aifomo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.aifomo.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
