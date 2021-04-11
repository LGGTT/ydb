package com.lggtt.srb.core.controller.api;


import com.lggtt.srb.base.util.JwtUtils;
import com.lggtt.srb.core.pojo.vo.LoginVO;
import com.lggtt.srb.core.pojo.vo.RegisterVO;
import com.lggtt.srb.core.pojo.vo.UserIndexVO;
import com.lggtt.srb.core.pojo.vo.UserInfoVO;
import com.lggtt.srb.core.service.UserInfoService;
import com.lggtt.srb.exception.Assert;
import com.lggtt.srb.result.R;
import com.lggtt.srb.result.ResponseEnum;
import com.lggtt.srb.util.RegexValidateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 用户基本信息 前端控制器
 * </p>
 *
 * @author lggtt
 * @since 2021-04-03
 */
@Api(tags = "用户接口")
@CrossOrigin
@RestController
@RequestMapping("/api/core/userInfo")
public class UserInfoController {

    @Resource
    UserInfoService userInfoService;

    @Resource
    RedisTemplate redisTemplate;

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public R register(
            @ApiParam(value = "用户基本信息", required = true)
            @RequestBody RegisterVO registerVO) {

        // 校验参数
        Assert.notEmpty(registerVO.getMobile(), ResponseEnum.MOBILE_NULL_ERROR);
        Assert.notEmpty(registerVO.getPassword(), ResponseEnum.PASSWORD_NULL_ERROR);
//        Assert.notEmpty(registerVO.getCode(), ResponseEnum.CODE_NULL_ERROR);
        Assert.isTrue(RegexValidateUtils.checkCellphone(registerVO.getMobile()), ResponseEnum.MOBILE_ERROR);
        // 检验验证码
//        String code = (String) redisTemplate.opsForValue().get("srb:sms:code:" + registerVO.getMobile());
//        Assert.equals(registerVO.getCode(), code, ResponseEnum.CODE_ERROR);

        // 用户注册
        userInfoService.register(registerVO);

        return R.ok().message("注册成功");

    }

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public R login(@ApiParam(value = "用户登录信息", required = true)
                   @RequestBody LoginVO loginVO, HttpServletRequest request) {
        String mobile = loginVO.getMobile();
        String password = loginVO.getPassword();
        // 校验
        Assert.notEmpty(mobile, ResponseEnum.MOBILE_NULL_ERROR);
        Assert.notEmpty(password, ResponseEnum.PASSWORD_NULL_ERROR);

        String ip = request.getRemoteAddr();
        UserInfoVO userInfoVo = userInfoService.login(loginVO, ip);

        return R.ok().data("userInfo", userInfoVo);
    }

    @ApiOperation("token 校验")
    @GetMapping("/checkToken")
    public R checkToken(HttpServletRequest request) {
        String token = request.getHeader("token");

        if (JwtUtils.checkToken(token)) {
            return R.ok();
        }
        return R.setResult(ResponseEnum.LOGIN_AUTH_ERROR);
    }

    @ApiOperation("获取个人空间用户信息")
    @GetMapping("/auth/getIndexUserInfo")
    public R getIndexUserInfo(HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        UserIndexVO userIndexVO = userInfoService.getIndexUserInfo(userId);
        return R.ok().data("userIndexVO", userIndexVO);
    }
}

