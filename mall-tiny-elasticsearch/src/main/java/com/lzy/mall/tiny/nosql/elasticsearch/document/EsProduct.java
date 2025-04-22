package com.lzy.mall.tiny.nosql.elasticsearch.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Document(indexName = "pms"):
 * Spring Data Elasticsearch 注解，标记 EsProduct 类对应 Elasticsearch 中的一个文档。
 * ndexName = "pms": 指定这个文档应该被存储到名为 "pms" 的 Elasticsearch 索引中。
 * 在 Spring Data Elasticsearch 中，@Document 注解是必须的，它告诉框架如何将Java对象映射到ES文档。
 */
@Data
@Document(indexName = "pms")
public class EsProduct implements Serializable {

    /**
     * erializable 接口的一部分，用于版本控制。当序列化对象时，这个ID会被写入。
     * 反序列化时，JVM会检查类和对象的serialVersionUID是否一致，不一致会抛异常。
     * 通常可以由IDE自动生成，或者固定一个值。
     */
    private static final long serialVersionUID = -1L;

    /**
     * Spring Data 注解，标记 id 字段是这个实体的唯一标识符。
     * 在 Spring Data Elasticsearch 中，这个字段会映射到 Elasticsearch 文档的特殊字段 "_id"。
     * Elasticsearch 的文档ID用于唯一标识索引中的每个文档。
     */
    @Id
    private Long id; // 对应数据库中商品的ID，作为Elasticsearch文档的唯一ID。

    /**
     * Spring Data Elasticsearch 注解，标记 productSn 字段在 Elasticsearch 中对应的字段属性。
     * type = FieldType.Keyword: 指定字段类型为 Keyword。Keyword 类型用于索引结构化的字段，
     */
    @Field(type = FieldType.Keyword)
    private String productSn; // 商品货号，通常用于精确查找或过滤。

    /**
     * 品牌ID。没有 @Field 注解时，Spring Data Elasticsearch 会根据Java类型自动推断一个默认的ES类型。
     * Long 类型通常会映射到 Elasticsearch 的 long 类型。
     */
    private Long brandId;

    @Field(type = FieldType.Keyword)
    private String brandName;

    private Long productCategoryId;

    @Field(type = FieldType.Keyword)
    private String productCategoryName;

    private String pic;  //商品图片URL

    /**
     * 标记 name 字段在 Elasticsearch 中的属性。
     * type = FieldType.Text: 指定字段类型为 Text。Text 类型用于索引需要进行全文搜索的文本内容。
     * analyzer = "ik_max_word": 指定用于分析这个 Text 字段的分析器为 "ik_max_word"。
     * "ik_max_word" 是一个中文分词器（如 IK Analyzer 或类似的插件）。
     * 它会将中文文本分解成词语，支持更精确的中文搜索。例如，"小米手机" 可能被分成 "小米" 和 "手机"。
     */
    @Field(analyzer = "ik_max_word",type = FieldType.Text)
    private String name;// 商品名称

    @Field(analyzer = "ik_max_word",type = FieldType.Text)
    private String subTitle;// 商品副标题

    @Field(analyzer = "ik_max_word",type = FieldType.Text)
    private String keywords;// 商品关键词

    private BigDecimal price;// 商品价格

    private Integer sale;// 销量

    private Integer newStatus;// 新品状态

    private Integer recommandStatus;// 推荐状态

    private Integer stock;// 库存

    private Integer promotionType;// 促销类型

    private Integer sort;// 排序字段

    /**
     * 标记 attrValueList 字段。
     * type = FieldType.Nested: 这是非常重要的一个类型。它用于索引包含对象列表的字段。
     * Elasticsearch 默认处理对象列表时会“扁平化”它们，这可能导致无法独立查询列表中每个对象的内部字段组合（例如，无法查询“颜色是红色” AND “尺寸是L”的商品，因为“红色”和“L”可能属于同一个商品的两个不同属性值对象）。
     * FieldType.Nested 会将列表中的每个对象作为一个独立的“内部文档”来索引，从而允许您对这些嵌套文档进行更复杂的查询和聚合，例如查询同时满足某个属性名和属性值的商品。
     */
    @Field(type =FieldType.Nested)
    private List<EsProductAttributeValue> attrValueList;
    // 商品属性值列表。每个 EsProductAttributeValue 对象代表一个具体的商品属性（如颜色、尺寸）及其值。
    // 因为使用了 FieldType.Nested，所以可以针对这个列表中的每个 EsProductAttributeValue 对象进行独立的条件查询。

    //省略了所有getter和setter方法
}