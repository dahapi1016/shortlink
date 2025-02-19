package com.hapi.shortlink.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hapi.shortlink.admin.common.convention.result.Result;
import com.hapi.shortlink.admin.remote.dto.ShortLinkRemoteService;
import com.hapi.shortlink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import com.hapi.shortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import com.hapi.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.hapi.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短链接远程调用接口
 */
@RestController
@RequiredArgsConstructor
public class ShortLinkController {

    //TODO 后续重构为Spring feign接口调用
    ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService() {
    };

    /**
     * 远程调用创建短链接
     * @param requestParam 请求参数
     * @return 创建结果
     */
    @PostMapping("api/short-link/admin/v1/create")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam) {
        return shortLinkRemoteService.createShortLink(requestParam);
    }

    /**
     * 远程调用分页查询短链接
     * @param requestParam 请求参数
     * @return 分页信息
     */
    @GetMapping("api/short-link/admin/v1/page")
    public Result<IPage<ShortLinkPageRespDTO>> getShortLinkByPage(ShortLinkPageReqDTO requestParam) {
        return shortLinkRemoteService.getShortLinkByPage(requestParam);
    }
}
