package com.hapi.shortlink.admin.remote.dto;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hapi.shortlink.admin.common.convention.result.Result;
import com.hapi.shortlink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import com.hapi.shortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import com.hapi.shortlink.admin.remote.dto.resp.ShortLinkCountRespDTO;
import com.hapi.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.hapi.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public interface ShortLinkRemoteService {

    default Result<ShortLinkCreateRespDTO> createShortLink(ShortLinkCreateReqDTO requestParam) {
        String JSONString = HttpUtil.post("http://127.0.0.1:8003/api/short-link/v1/create", JSON.toJSONString(requestParam));
        return JSON.parseObject(JSONString, new TypeReference<Result<ShortLinkCreateRespDTO>>(){});
    }

    default Result<IPage<ShortLinkPageRespDTO>> getShortLinkByPage(ShortLinkPageReqDTO requestParam) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("current", requestParam.getCurrent());
        requestMap.put("size", requestParam.getSize());
        requestMap.put("gid", requestParam.getGid());
        String jsonString = HttpUtil.get("http://127.0.0.1:8003/api/short-link/v1/page", requestMap);
        return JSON.parseObject(jsonString, new TypeReference<Result<IPage<ShortLinkPageRespDTO>>>() {});
    }

    default Result<ShortLinkCountRespDTO> getGroupShortLinkCount(List<String> gidList, String username) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("gidList", gidList);
        requestMap.put("username", username);
        String jsonString = HttpUtil.get("http://127.0.0.1:8003/api/short-link/v1/count", requestMap);
        return JSON.parseObject(jsonString ,new TypeReference<Result<ShortLinkCountRespDTO>>(){});
    }
}
