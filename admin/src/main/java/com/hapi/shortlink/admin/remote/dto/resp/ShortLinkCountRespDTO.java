package com.hapi.shortlink.admin.remote.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class ShortLinkCountRespDTO {

    /**
     * 结果存储在Map中便于Admin模块读取
     */
    private Map<String, Long> resultMap;
}
