package com.lzy.mall.tiny.service.impl;

import com.lzy.mall.tiny.nosql.mongodb.document.MemberReadHistory;
import com.lzy.mall.tiny.nosql.mongodb.repository.MemberReadHistoryRepository;
import com.lzy.mall.tiny.service.MemberReadHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class MemberReadHistoryServiceImpl implements MemberReadHistoryService {
    @Autowired
    private MemberReadHistoryRepository memberReadHistoryRepository;

    //创建一个新的会员商品浏览历史记录
    @Override
    public int create(MemberReadHistory memberReadHistory) {
        /**
         * 1. 将输入对象的ID设置为null。
         * @Id注解在MemberReadHistory类中标记了id字段作为MongoDB文档的主键（_id）。
         * 当使用Spring Data MongoDB的save方法保存一个新文档时，
         * 如果对象的@Id字段是null，Spring Data会告诉MongoDB自动生成一个唯一的ObjectId作为_id。
         * 这样做是为了确保每次调用create方法时，都会创建一个新的浏览记录，而不是更新现有记录（如果传入的对象偶然有了ID）
         */
        memberReadHistory.setId(null);

        /**
         * 2. 将输入对象的创建时间设置为当前系统时间。
         * 这是为了记录这条浏览历史记录发生的准确时间。
         */
        memberReadHistory.setCreateTime(new Date());

        /**
         * 3. 调用memberReadHistoryRepository的save方法将对象持久化到MongoDB。
         * memberReadHistoryRepository是MongoRepository的实例，它提供了基本的CRUD操作。
         * 由于我们在前面将id设置为了null，save方法在这里会执行一个插入（Insert）操作，
         * 在MongoDB中创建一个新的文档，并自动生成_id。
         */
        memberReadHistoryRepository.save(memberReadHistory);
        return 1;
    }

    //批量删除指定的会员商品浏览历史记录
    @Override
    public int delete(List<String> ids) {

        // 1. 创建一个空的 ArrayList，用于存放待删除的 MemberReadHistory 对象列表。
        List<MemberReadHistory> deleteList = new ArrayList<>();

        // 2. 遍历输入的 ids 列表（要删除的记录的ID列表）
        for(String id:ids){
            // 3. 对于每一个要删除的 ID，创建一个新的 MemberReadHistory 对象。
            MemberReadHistory memberReadHistory = new MemberReadHistory();
            // 4. 将当前 ID 设置到新创建的对象的 id 属性上。
            // 注意：这里只设置了 id，其他属性（如 memberId, productName 等）保持默认值（null 或 0）。
            // 这是因为 Spring Data MongoDB 的 delete/deleteAll 方法在删除时只需要文档的 ID 来定位要删除的记录。
            memberReadHistory.setId(id);
            // 5. 将这个仅包含 ID 的 MemberReadHistory 对象添加到 deleteList 中。
            deleteList.add(memberReadHistory);
        }

        // 6. 调用 memberReadHistoryRepository 的 deleteAll 方法，传入构建好的 deleteList。
        // Spring Data MongoDB 会根据 deleteList 中的每个对象的 @Id 字段（即我们设置的 id）
        // 到 MongoDB 中对应的集合（memberReadHistory 集合）中找到并删除匹配的文档。
        memberReadHistoryRepository.deleteAll(deleteList);

        // 7. 方法返回输入 ids 列表的大小。
        // 这个返回值表示的是尝试删除的记录数量（即根据传入的ID数量），
        // 但并不直接表示实际成功删除的记录数量（如果传入的ID在数据库中不存在，deleteAll 也不会报错，但也不会删除任何文档）。
        return ids.size();
    }

    //根据会员 ID 获取该会员的商品浏览历史记录列表
    @Override
    public List<MemberReadHistory> list(Long memberId) {

        // - "findBy": 表示根据某个条件进行查询。
        // - "MemberId": 表示查询的条件字段是 MemberReadHistory 类中的 memberId 字段。
        // - "OrderByCreateTimeDesc": 表示查询结果需要按照 createTime 字段进行排序，Desc 表示降序（Descending）。降序排列意味着最新的记录会排在前面。
        return memberReadHistoryRepository.findByMemberIdOrderByCreateTimeDesc(memberId);
    }
}