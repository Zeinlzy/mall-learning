package com.lzy.mall.tiny.service.impl;

import com.lzy.mall.tiny.dao.EsProductDao;
import com.lzy.mall.tiny.nosql.elasticsearch.document.EsProduct;
import com.lzy.mall.tiny.nosql.elasticsearch.repository.EsProductRepository;
import com.lzy.mall.tiny.service.EsProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@Service
public class EsProductServiceImpl implements EsProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EsProductServiceImpl.class);
    @Autowired
    private EsProductDao productDao;
    @Autowired
    private EsProductRepository productRepository;

    //将数据库中的所有商品数据迁移（或同步）到 Elasticsearch 中
    @Override
    public int importAll() {
        // 1. 从数据库查询所有需要导入到Elasticsearch的商品数据
        List<EsProduct> esProductList = productDao.getAllEsProductList(null);

        // 2. 将查询到的商品列表批量保存到Elasticsearch
        // productRepository 是一个 Spring Data Elasticsearch Repository，
        // 它提供了与Elasticsearch交互的标准方法，saveAll用于批量保存,saveAll方法返回的是一个Iterable迭代器，表示成功保存的文档集合
        Iterable<EsProduct> esProductIterable = productRepository.saveAll(esProductList);

        // 3. 统计成功导入到Elasticsearch的商品数量
        //esProductIterable.iterator(): 获取 esProductIterable 集合的迭代器
        Iterator<EsProduct> iterator = esProductIterable.iterator();
        int result = 0;
        while (iterator.hasNext()) {
            result++;
            iterator.next(); // 移动到下一个元素，next()方法的调用是必须的，否则hasNext()永远是true
        }

        // 4. 返回成功导入的商品数量
        return result;
    }

    @Override
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    /**
     * 根据一个指定的数据库商品 ID，将该商品的数据从数据库中读取出来，然后将其同步（索引或更新）到 Elasticsearch 中。
     * 这通常用于处理单个商品的创建或更新事件，以确保 Elasticsearch 中的数据与数据库保持同步。
     * 如果数据库中不存在该商品，则不会向 Elasticsearch 中添加任何内容。
     */
    @Override
    public EsProduct create(Long id) {
        // 1. 初始化一个变量来存储最终返回的结果，默认为null
        EsProduct result = null;

        // 2. 根据传入的商品ID从数据库查询对应的商品数据
        // getAllEsProductList方法在这里被重用了，传入特定的id意味着查询指定的单个商品
        List<EsProduct> esProductList = productDao.getAllEsProductList(id);

        // 3. 检查是否从数据库成功查询到了商品数据
        if (esProductList.size() > 0) {
            // 4. 如果查询到了（预期只有一个，因为id是唯一的），获取列表中的第一个EsProduct对象
            EsProduct esProduct = esProductList.get(0);

            // 5. 将从数据库获取到的EsProduct对象保存（索引）到Elasticsearch中
            // productRepository.save()方法用于将单个文档保存到Elasticsearch
            result = productRepository.save(esProduct);
        }

        // 6. 返回保存到Elasticsearch中的EsProduct对象（如果成功的话），
        // 如果数据库中没有找到对应id的商品，则返回null
        return result;
    }

    /**
     * 根据传入的商品 ID 列表，批量删除 Elasticsearch 中对应 ID 的文档
     */
    @Override
    public void delete(List<Long> ids) {
        // 1. 检查传入的商品ID列表是否为空或null
        if (!CollectionUtils.isEmpty(ids)) {
            // 2. 如果列表不为空，创建一个用于存储EsProduct对象的列表
            List<EsProduct> esProductList = new ArrayList<>();

            // 3. 遍历传入的每一个商品ID
            for (Long id : ids) {
                // 4. 为当前的商品ID创建一个临时的EsProduct对象
                EsProduct esProduct = new EsProduct();
                // 5. 设置这个临时的EsProduct对象的ID为当前的商品ID
                esProduct.setId(id);
                // 6. 将这个设置了ID的临时EsProduct对象添加到列表中
                esProductList.add(esProduct);
            }

            // 7. 调用productRepository的deleteAll方法，批量删除Elasticsearch中对应ID的文档
            // Spring Data Elasticsearch Repository 只需要实体的ID就能执行删除操作
            productRepository.deleteAll(esProductList);
        }
        // 8. 方法没有返回值 (void)
    }

    @Override
    public Page<EsProduct> search(String keyword, Integer pageNum, Integer pageSize) {
        // 1. 构建分页参数对象 Pageable
        // 包含当前页码 (pageNum) 和每页记录数 (pageSize)。
        Pageable pageable = PageRequest.of(pageNum, pageSize);

        // 2. 调用 EsProductRepository 接口中定义的搜索方法执行查询
        // Spring Data Elasticsearch 会根据方法名自动构建 Elasticsearch 查询
        // 这个方法名 findByNameOrSubTitleOrKeywords 表示在 name, subTitle, keywords，三个字段中进行 OR 逻辑的搜索
        // 将同一个 keyword 传入这三个参数，意味着在这三个字段中搜索同一个关键词
        // 最后一个参数 pageable 指示 Spring Data Elasticsearch 进行分页查询
        return productRepository.findByNameOrSubTitleOrKeywords(keyword, keyword, keyword, pageable);
    }

}