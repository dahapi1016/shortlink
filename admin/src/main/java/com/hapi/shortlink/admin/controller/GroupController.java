package com.hapi.shortlink.admin.controller;

import com.hapi.shortlink.admin.common.convention.result.Result;
import com.hapi.shortlink.admin.common.convention.result.Results;
import com.hapi.shortlink.admin.dto.req.CreateGroupReqDTO;
import com.hapi.shortlink.admin.dto.req.ShortLinkGroupSortReqDTO;
import com.hapi.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import com.hapi.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;
import com.hapi.shortlink.admin.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;



/**
 * 短链接分组接口
 */
@RestController
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    /**
     * 创建短链接分组
     * @param requestParam 请求参数
     */
    @PostMapping("/api/short-link/admin/v1/group")
    public Result<Void> createGroup(@RequestBody CreateGroupReqDTO requestParam) {
        groupService.createGroup(requestParam);
        return Results.success();
    }

    /**
     * 获取当前用户的分组列表（有序）
     * @return 分组列表
     */
    @GetMapping("/api/short-link/admin/v1/group")
    public Result<List<ShortLinkGroupRespDTO>> getGroupList() {
        return Results.success(groupService.getGroupList());
    }

    /**
     * 更新分组信息
     * @param requestParam  请求参数
     */
    @PutMapping("/api/short-link/admin/v1/group")
    public Result<Void> updateGroup(@RequestBody ShortLinkGroupUpdateReqDTO requestParam) {
        groupService.updateGroup(requestParam);
        return Results.success();
    }

    /**
     * 删除分组
     * @param gid 分组标识
     */
    @DeleteMapping("/api/short-link/admin/v1/group")
    public Result<Void> deleteGroup(@RequestParam String gid) {
        groupService.deleteGroup(gid);
        return Results.success();
    }

    /**
     * 根据前端操作排序分组
     * @param requestParam 请求参数
     */
    @PostMapping("/api/short-link/admin/v1/group/sort")
    public Result<Void> sortGroup(@RequestBody List<ShortLinkGroupSortReqDTO> requestParam) {
        groupService.sortGroup(requestParam);
        return Results.success();
    }
}
