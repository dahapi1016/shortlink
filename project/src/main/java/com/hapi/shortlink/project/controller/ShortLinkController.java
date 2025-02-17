package com.hapi.shortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hapi.shortlink.project.common.convention.result.Result;
import com.hapi.shortlink.project.common.convention.result.Results;
import com.hapi.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.hapi.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.hapi.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.hapi.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import com.hapi.shortlink.project.service.ShortLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 短链接管理接口
 */
@RestController
@RequiredArgsConstructor
public class ShortLinkController {

    private final ShortLinkService shortLinkService;

    /**
     * 创建短链接
     * @param requestParam 请求参数
     * @return 返回短链接信息
     */
    @PostMapping("/api/short-link/v1/create")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam) {
        return Results.success(shortLinkService.createShortLink(requestParam));
    }

    @GetMapping("api/short-link/v1/page")
    public Result<IPage<ShortLinkPageRespDTO>> getShortLinkByPage(ShortLinkPageReqDTO requestParam) {
        return Results.success(shortLinkService.getShortLinkByPage(requestParam));
    }
}
