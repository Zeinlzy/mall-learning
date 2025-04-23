package com.lzy.mall.tiny.nosql.mongodb.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

// @Data: Lombok注解，自动为所有字段生成 getter、setter、equals()、hashCode()、toString() 方法。
// 这是为了减少手动编写这些 boilerplate 代码的工作量。
@Data
// @Document: Spring Data MongoDB注解，表明这个Java类是一个MongoDB文档，
// 它将映射到MongoDB中的一个Collection（集合）。
// 如果不指定Collection名称，默认使用类名的小写形式，即 "memberReadHistory"。
@Document
public class MemberReadHistory {

    // @Id: Spring Data注解，用于标识文档的主键。
    // 在MongoDB中，这通常映射到文档的 "_id" 字段。
    // Spring Data MongoDB 会自动处理ObjectId的生成和与String类型的映射。
    @Id
    private String id;

    // @Indexed: Spring Data MongoDB注解，表示在这个字段上创建索引。
    // 索引可以显著提高根据这个字段进行查询的性能。
    // 在实际应用中，我们经常根据会员ID查询浏览记录，所以memberId需要索引。
    @Indexed
    private Long memberId;

    // 会员的昵称，非索引字段
    private String memberNickname;

    // 会员的头像URL，非索引字段
    private String memberIcon;

    // @Indexed: 同样，productId也需要创建索引，以便快速查询某个产品的浏览记录。
    @Indexed
    private Long productId;

    // 商品名称，非索引字段
    private String productName;

    // 商品图片URL，非索引字段
    private String productPic;

    // 商品副标题，非索引字段
    private String productSubTitle;

    // 商品价格（存储为String，可能需要考虑数据类型）
    private String productPrice;

    // 浏览记录的创建时间，非索引字段（尽管 often times are indexed for time-based queries, here it is not annotated as indexed）
    private Date createTime;

    // 省略了所有getter和setter方法 (这正是 @Data 注解的作用)

}