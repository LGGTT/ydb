package com.lggtt.srb.core.controller.api.sms;

import com.lggtt.srb.core.pojo.entity.UserInfo;
import com.lggtt.srb.core.service.UserInfoService;
import com.lggtt.srb.exception.Assert;
import com.lggtt.srb.result.R;
import com.lggtt.srb.result.ResponseEnum;
import com.lggtt.srb.util.RandomUtils;
import com.lggtt.srb.util.RegexValidateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Api(tags = "模拟发送短信验证码接口")
@RestController
@RequestMapping("/api/sms")
@CrossOrigin
public class SMSController {

    @Resource
    RedisTemplate redisTemplate;
    @Resource
    UserInfoService userInfoService;

    @ApiOperation("发送短信验证码")
    @GetMapping("/send/{phone}")
    public R send(@ApiParam(value = "手机号",required = true)
                  @PathVariable("phone") String mobile) {

        Assert.notNull(mobile, ResponseEnum.MOBILE_NULL_ERROR);
        Assert.isTrue(RegexValidateUtils.checkCellphone(mobile), ResponseEnum.MOBILE_ERROR);

        // 判断手机号是否注册
        UserInfo userInfo = userInfoService.checkMobile(mobile);
        Assert.isNull(userInfo,ResponseEnum.MOBILE_EXIST_ERROR);

//        String code = RandomUtils.getFourBitRandom();
//        Map<String, Object> param = new HashMap<>();
//        param.put("code", code);
//        smsService.send(phone, SMSProperties.TEMPLATE_CODE, param);
//        redisTemplate.opsForValue().set("srb:sms:code:" + mobile, code, 1, TimeUnit.MINUTES);

        return R.ok().message("发送成功");
    }
}
