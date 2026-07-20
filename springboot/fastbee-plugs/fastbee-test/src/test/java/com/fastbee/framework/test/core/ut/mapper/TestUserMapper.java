package com.fastbee.framework.test.core.ut.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import com.fastbee.framework.test.core.ut.entity.TestUserDO;

/**
 * 测试用户 Mapper
 */
@Mapper
public interface TestUserMapper extends BaseMapper<TestUserDO> {
}
