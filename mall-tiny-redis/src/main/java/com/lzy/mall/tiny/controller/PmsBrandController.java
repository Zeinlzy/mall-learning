package com.lzy.mall.tiny.controller;

import com.lzy.mall.tiny.common.api.CommonPage;
import com.lzy.mall.tiny.common.api.CommonResult;
import com.lzy.mall.tiny.mbg.model.PmsBrand;
import com.lzy.mall.tiny.service.PmsBrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @auther macrozheng
 * @description 品牌管理Controller
 * @date 2019/4/19
 * @github https://github.com/macrozheng
 */
@Controller
@Tag(name = "PmsBrandController")
@Tag(name = "PmsBrandController", description = "商品品牌管理")
@RequestMapping("/brand")
public class PmsBrandController {
    @Autowired
    private PmsBrandService brandService;

    private static final Logger LOGGER = LoggerFactory.getLogger(PmsBrandController.class);

    @Operation(summary = "添加品牌")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult create(@RequestBody PmsBrand pmsBrand) {
        CommonResult commonResult;
        int count = brandService.create(pmsBrand);
        if (count == 1) {
            commonResult = CommonResult.success(pmsBrand);
            LOGGER.debug("create success:{}", pmsBrand);
        } else {
            commonResult = CommonResult.failed("操作失败");
            LOGGER.debug("create failed:{}", pmsBrand);
        }
        return commonResult;
    }

    @Operation(summary = "更新指定id品牌信息")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult update(@PathVariable("id") Long id, @RequestBody PmsBrand pmsBrandDto, BindingResult result) {
        CommonResult commonResult;
        int count = brandService.update(id, pmsBrandDto);
        if (count == 1) {
            commonResult = CommonResult.success(pmsBrandDto);
            LOGGER.debug("update success:{}", pmsBrandDto);
        } else {
            commonResult = CommonResult.failed("操作失败");
            LOGGER.debug("update failed:{}", pmsBrandDto);
        }
        return commonResult;
    }

    @Operation(summary = "删除指定id的品牌")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult delete(@PathVariable("id") Long id) {
        int count = brandService.delete(id);
        if (count == 1) {
            LOGGER.debug("delete success :id={}", id);
            return CommonResult.success(null);
        } else {
            LOGGER.debug("delete failed :id={}", id);
            return CommonResult.failed("操作失败");
        }
    }

    @Operation(summary = "获取指定id的品牌详情")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<PmsBrand> getItem(@PathVariable("id") Long id) {
        return CommonResult.success(brandService.getItem(id));
    }

    @Operation(summary = "分页查询品牌列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<PmsBrand>> list(@RequestParam(value = "pageNum", defaultValue = "1")
                                                        @Param("页码") Integer pageNum,
                                                        @RequestParam(value = "pageSize", defaultValue = "3")
                                                        @Param("每页数量") Integer pageSize) {
        List<PmsBrand> brandList = brandService.list(pageNum, pageSize);
        return CommonResult.success(CommonPage.restPage(brandList));
    }

    @Operation(summary = "获取所有品牌列表")
    @RequestMapping(value = "listAll", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<PmsBrand>> getAll() {
        return CommonResult.success(brandService.ListAll());
    }
}
