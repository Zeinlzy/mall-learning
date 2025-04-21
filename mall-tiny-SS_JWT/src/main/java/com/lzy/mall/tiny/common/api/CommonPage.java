package com.lzy.mall.tiny.common.api;

import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 通用分页数据封装类（支持泛型）
 * <T> 表示分页数据中的数据类型
 * 用于封装分页查询结果，便于统一前端分页数据格式
 */
public class CommonPage<T> {
    /** 当前页码（从1开始计数） */
    private Integer pageNum;

    /** 每页显示记录数 */
    private Integer pageSize;

    /** 总页数（根据总记录数和pageSize计算得出） */
    private Integer totalPage;

    /** 总记录数（符合条件的数据总量） */
    private Long total;

    /** 当前页的数据列表 */
    private List<T> list;

    /**
     * 将PageHelper分页结果转换为通用分页对象
     * @param list 分页后的数据列表（需配合PageHelper.startPage 使用）
     * @param <T> 数据类型泛型
     * @return 封装好的分页对象，包含分页信息和数据列表
     *
     * @apiNote 典型使用场景：
     * 1. 在Service层使用PageHelper.startPage 后执行查询
     * 2. 将查询结果直接传入本方法
     * 3. 返回对象包含完整的分页元数据
     */
    public static <T> CommonPage<T> restPage(List<T> list) {
        CommonPage<T> result = new CommonPage<T>();
        PageInfo<T> pageInfo = new PageInfo<T>(list);
        result.setTotalPage(pageInfo.getPages());     // 计算总页数
        result.setPageNum(pageInfo.getPageNum());     // 当前页码
        result.setPageSize(pageInfo.getPageSize());   // 每页条数
        result.setTotal(pageInfo.getTotal());         // 总记录数
        result.setList(pageInfo.getList());           // 当前页数据
        return result;
    }

    //------------------- Getter/Setter 方法 -------------------//
    /** 获取当前页码 */
    public Integer getPageNum() { return pageNum; }

    /** 设置当前页码（通常不需要手动设置） */
    public void setPageNum(Integer pageNum) { this.pageNum  = pageNum; }

    /** 获取每页显示条数 */
    public Integer getPageSize() { return pageSize; }

    /** 设置每页显示条数 */
    public void setPageSize(Integer pageSize) { this.pageSize  = pageSize; }

    /** 获取总页数 */
    public Integer getTotalPage() { return totalPage; }

    /** 设置总页数（由PageInfo自动计算） */
    public void setTotalPage(Integer totalPage) { this.totalPage  = totalPage; }

    /** 获取当前页数据列表 */
    public List<T> getList() { return list; }

    /** 设置当前页数据列表 */
    public void setList(List<T> list) { this.list  = list; }

    /** 获取总记录数 */
    public Long getTotal() { return total; }

    /** 设置总记录数 */
    public void setTotal(Long total) { this.total  = total; }
}