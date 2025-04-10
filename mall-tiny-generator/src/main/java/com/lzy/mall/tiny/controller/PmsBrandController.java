package com.lzy.mall.tiny.controller;

import com.lzy.mall.tiny.common.api.CommonPage;
import com.lzy.mall.tiny.common.api.CommonResult;
import com.lzy.mall.tiny.mbg.model.PmsBrand;
import com.lzy.mall.tiny.service.PmsBrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 品牌管理 Controller
 */
@Tag(name = "PmsBrandController", description = "商品品牌管理") // Swagger API 文档标注
@RestController // 标记为 RESTful Controller，并包含 @ResponseBody
@RequestMapping("/brand") // 所有请求的基础路径为 /brand
public class PmsBrandController {

    @Autowired
    private PmsBrandService brandService; // 注入 Service

    // 添加日志记录器
    private static final Logger LOGGER = LoggerFactory.getLogger(PmsBrandController.class);

    @Operation(summary = "获取所有品牌列表")
    @GetMapping(value = "/listAll") // 映射 GET 请求到 /brand/listAll
    public CommonResult<List<PmsBrand>> getBrandList() {
        List<PmsBrand> brandList = brandService.listAllBrand();
        return CommonResult.success(brandList);
    }

    @Operation(summary = "添加品牌")
    @PostMapping(value = "/create") // 映射 POST 请求到 /brand/create
    public CommonResult createBrand(@RequestBody PmsBrand pmsBrand) { // @RequestBody 从请求体中获取 PmsBrand 对象
        CommonResult commonResult;
        int count = brandService.createBrand(pmsBrand);
        if (count == 1) {
            LOGGER.debug("createBrand success:{}", pmsBrand);
            commonResult = CommonResult.success(pmsBrand); // 返回创建成功和数据
        } else {
            LOGGER.debug("createBrand failed:{}", pmsBrand);
            commonResult = CommonResult.failed("操作失败"); // 返回操作失败
        }
        return commonResult;
    }

    @Operation(summary = "更新指定id品牌信息")
    @PostMapping(value = "/update/{id}") // 映射 POST 请求到 /brand/update/{id}
    public CommonResult updateBrand(@PathVariable("id") Long id, @RequestBody PmsBrand pmsBrand) { // @PathVariable 从路径中获取 id
        CommonResult commonResult;
        int count = brandService.updateBrand(id, pmsBrand);
        if (count == 1) {
            LOGGER.debug("updateBrand success:{}", pmsBrand);
            commonResult = CommonResult.success("更新成功"); // 返回更新成功
        } else {
            LOGGER.debug("updateBrand failed:{}", pmsBrand);
            commonResult = CommonResult.failed("操作失败"); // 返回操作失败
        }
        return commonResult;
    }

    @Operation(summary = "删除指定id的品牌")
    @GetMapping(value = "/delete/{id}") // 映射 GET 请求到 /brand/delete/{id} (也可以用 @DeleteMapping)
    public CommonResult deleteBrand(@PathVariable("id") Long id) {
        int count = brandService.deleteBrand(id);
        if (count == 1) {
            LOGGER.debug("deleteBrand success :id={}", id);
            return CommonResult.success("删除成功"); // 返回删除成功
        } else {
            LOGGER.debug("deleteBrand failed :id={}", id);
            return CommonResult.failed("操作失败"); // 返回操作失败
        }
    }

    @Operation(summary = "分页查询品牌列表")
    @GetMapping(value = "/list") // 映射 GET 请求到 /brand/list
    public CommonResult<CommonPage<PmsBrand>> listBrand(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum, // 页码，默认为1
                                                        @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize) { // 每页数量，默认为5
        List<PmsBrand> brandList = brandService.listBrand(pageNum, pageSize);
        // 使用 CommonPage 工具类包装分页结果
        return CommonResult.success(CommonPage.restPage(brandList));
    }

    @Operation(summary = "获取指定id的品牌详情")
    @GetMapping(value = "/{id}") // 映射 GET 请求到 /brand/{id}
    public CommonResult<PmsBrand> brand(@PathVariable("id") Long id) {
        PmsBrand brand = brandService.getBrand(id);
        if (brand != null) {
            return CommonResult.success(brand); // 返回查询到的品牌数据
        } else {
            return CommonResult.failed("未找到指定品牌"); // 如果未找到，返回失败信息
        }
    }
}
