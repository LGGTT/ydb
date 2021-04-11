package com.lggtt.srb.sms.controller;

import com.lggtt.srb.exception.Assert;
import com.lggtt.srb.result.R;
import com.lggtt.srb.result.ResponseEnum;
import com.lggtt.srb.sms.service.SMSService;
import com.lggtt.srb.sms.util.SMSProperties;
import com.lggtt.srb.util.RandomUtils;
import com.lggtt.srb.util.RegexValidateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/sms")
@Api(tags = "短信接口")
@Slf4j
public class smsController {

    @Resource
    SMSService smsService;

    @Resource
    RedisTemplate redisTemplate;

    @ApiOperation("获取短信验证码")
    @GetMapping("/send/{phone}")
    public R send(@ApiParam("手机号")
                  @PathVariable("phone") String phone) {
        Assert.notNull(phone, ResponseEnum.MOBILE_NULL_ERROR);
        Assert.isTrue(RegexValidateUtils.checkCellphone(phone), ResponseEnum.MOBILE_ERROR);
//        String code = RandomUtils.getSixBitRandom();
//        Map<String, Object> param = new HashMap<>();
//        param.put("code", code);
//        smsService.send(phone, SMSProperties.TEMPLATE_CODE, param);
//        redisTemplate.opsForValue().set("srb:sms:code:" + phone, code, 5, TimeUnit.MINUTES);
        return R.ok().message("短信发送成功");
    }
}
