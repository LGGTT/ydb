package com.lggtt.srb.core.mapper;

import com.lggtt.srb.core.pojo.entity.UserAccount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * <p>
 * 用户账户 Mapper 接口
 * </p>
 *
 * @author lggtt
 * @since 2021-04-03
 */
public interface UserAccountMapper extends BaseMapper<UserAccount> {

    void updateAccount(@Param("bindCode")String bindCode,
                       @Param("amount")BigDecimal amount,
                       @Param("freezeAmount")BigDecimal freezeAmount);
}
