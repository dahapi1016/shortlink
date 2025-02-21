package com.hapi.shortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hapi.shortlink.project.common.convention.result.Result;
import com.hapi.shortlink.project.common.convention.result.Results;
import com.hapi.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.hapi.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.hapi.shortlink.project.dto.resp.ShortLinkCountRespDTO;
import com.hapi.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.hapi.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import com.hapi.shortlink.project.service.ShortLinkService;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

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

    /**
     * 分页查询短链接
     * @param requestParam 查询参数
     * @return 返回短链接页面
     */
    @GetMapping("/api/short-link/v1/page")
    public Result<IPage<ShortLinkPageRespDTO>> getShortLinkByPage(ShortLinkPageReqDTO requestParam) {
        return Results.success(shortLinkService.getShortLinkByPage(requestParam));
    }

    /**
     * 获取指定分组短链接数量
     * @param gidList 分组标识
     * @return 短链接数量
     */
    @GetMapping("/api/short-link/v1/count")
    public Result<ShortLinkCountRespDTO> getGroupShortLinkCount(@RequestParam List<String> gidList, @RequestParam String username) {
        return Results.success(shortLinkService.getShortLinkCount(gidList, username));
    }

    /**
     * 跳转短链接
     * @param shortUri 短链接URI
     * @param request Http请求
     * @param response Http响应
     */
    @GetMapping("/{short-uri}")
    public void redirectShortLink(@PathVariable("short-uri") String shortUri, ServletRequest request, ServletResponse response) throws IOException {
        shortLinkService.redirectShortLink(shortUri, request, response);
    }
}
