package com.lzy.mall.tiny.service;

import com.lzy.mall.tiny.mbg.model.PmsBrand;

import java.util.List;

/**
 * PmsBrandService 接口
 */
public interface PmsBrandService {

    /**
     * 获取所有品牌列表
     */
    List<PmsBrand> listAllBrand();

    /**
     * 创建品牌
     */
    int createBrand(PmsBrand brand);

    /**
     * 修改指定id的品牌
     */
    int updateBrand(Long id, PmsBrand brand);

    /**
     * 删除指定id的品牌
     */
    int deleteBrand(Long id);

    /**
     * 分页查询品牌列表
     */
    List<PmsBrand> listBrand(int pageNum, int pageSize);

    /**
     * 获取指定id的品牌详情
     */
    PmsBrand getBrand(Long id);
}
