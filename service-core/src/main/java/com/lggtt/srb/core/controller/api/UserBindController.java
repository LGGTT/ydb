package com.lggtt.srb.core.controller.api;

import com.alibaba.fastjson.JSON;
import com.lggtt.srb.base.util.JwtUtils;
import com.lggtt.srb.core.hfb.RequestHelper;
import com.lggtt.srb.core.pojo.vo.UserBindVO;
import com.lggtt.srb.core.service.UserBindService;
import com.lggtt.srb.result.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 * 用户绑定表 前端控制器
 * </p>
 *
 * @author lggtt
 * @since 2021-04-03
 */
@Api(tags = "用户账号绑定")
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/core/userBind")
public class UserBindController {

    @Resource
    UserBindService userBindService;

    @ApiOperation("账号绑定提交数据")
    @PostMapping("/auth/bind")
    public R bind(@ApiParam("绑定信息")
                  @RequestBody UserBindVO userBindVO,
                  HttpServletRequest request) {

        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);

        String formStr = userBindService.commitBindUser(userBindVO, userId);

        return R.ok().data("formStr",formStr);

    }

    @ApiOperation("第三方账号绑定通知回调")
    @PostMapping("/notify")
    public String callback(HttpServletRequest request){

        // 第三方回调携带的参数
        Map<String, Object> params = RequestHelper.switchMap(request.getParameterMap());

        // 验签
        if (!RequestHelper.isSignEquals(params)){
            log.error("第三方账号绑定通知回调验签失败：{}", JSON.toJSONString(params));
            return "false";
        }

        userBindService.callback(params);

        return "success";
    }
}

