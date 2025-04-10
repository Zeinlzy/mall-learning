package com.lzy.mall.tiny.service.impl;

import com.github.pagehelper.PageHelper;
import com.lzy.mall.tiny.mbg.mapper.PmsBrandMapper;
import com.lzy.mall.tiny.mbg.model.PmsBrand;
import com.lzy.mall.tiny.mbg.model.PmsBrandExample;
import com.lzy.mall.tiny.service.PmsBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * PmsBrandService 实现类
 */
@Service // 标记为 Spring Service 组件
public class PmsBrandServiceImpl implements PmsBrandService {

    // 注入 PmsBrandMapper
    @Autowired
    private PmsBrandMapper brandMapper;

    @Override
    public List<PmsBrand> listAllBrand() {
        // 查询所有，创建一个空的 Example 对象即可
        return brandMapper.selectByExample(new PmsBrandExample());
    }

    @Override
    public int createBrand(PmsBrand brand) {
        // 确保 id 为 null，让数据库自动生成
        brand.setId(null);
        // insertSelective 只插入非 null 的字段
        return brandMapper.insertSelective(brand);
        // 或者使用 insert，如果 brand 对象所有字段都已设置
        // return brandMapper.insert(brand);
    }

    @Override
    public int updateBrand(Long id, PmsBrand brand) {
        // 设置要更新的记录的 id
        brand.setId(id);
        // updateByPrimaryKeySelective 只更新非 null 的字段
        return brandMapper.updateByPrimaryKeySelective(brand);
        // 或者使用 updateByPrimaryKey，如果 brand 对象所有字段都已设置（除了主键）
        // return brandMapper.updateByPrimaryKey(brand);
    }

    @Override
    public int deleteBrand(Long id) {
        return brandMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<PmsBrand> listBrand(int pageNum, int pageSize) {
        // 使用 PageHelper 进行分页
        PageHelper.startPage(pageNum, pageSize);
        // 紧跟 PageHelper.startPage 的第一个 MyBatis 查询会自动进行分页
        // 查询所有数据，PageHelper 会拦截并应用分页逻辑
        return brandMapper.selectByExample(new PmsBrandExample());
    }

    @Override
    public PmsBrand getBrand(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }
}
