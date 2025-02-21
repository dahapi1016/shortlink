package com.hapi.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hapi.shortlink.project.dao.entity.ShortLinkDO;
import com.hapi.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.hapi.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.hapi.shortlink.project.dto.resp.ShortLinkCountRespDTO;
import com.hapi.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.hapi.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.io.IOException;
import java.util.List;

public interface ShortLinkService extends IService<ShortLinkDO> {

    /**
     * 创建短链接
     * @param requestParam 请求参数
     * @return 创建结果
     */
    ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam);

    /**
     * 分页查询短链接
     * @param requestParam 请求参数
     * @return 分页集合
     */
    IPage<ShortLinkPageRespDTO> getShortLinkByPage(ShortLinkPageReqDTO requestParam);

    /**
     * 获取指定分组短链接数量
     *
     * @return 短链接数量
     */
    ShortLinkCountRespDTO getShortLinkCount(List<String> gidlist, String username);

    /**
     * 跳转短链接
     * @param shortUri 短链接URI
     * @param request Http请求
     * @param response Http响应
     */
    void redirectShortLink(String shortUri, ServletRequest request, ServletResponse response) throws IOException;
}
