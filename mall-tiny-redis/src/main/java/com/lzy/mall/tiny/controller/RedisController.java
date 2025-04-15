package com.lzy.mall.tiny.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ArrayUtil;
import com.lzy.mall.tiny.common.api.CommonResult;
import com.lzy.mall.tiny.mbg.model.PmsBrand;
import com.lzy.mall.tiny.service.PmsBrandService;
import com.lzy.mall.tiny.service.RedisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@Tag(name = "RedisController", description = "redis测试")
@RequestMapping("/redis")
public class RedisController {
    @Autowired
    private RedisService redisService;
    @Autowired
    private PmsBrandService brandService;

    @Operation(summary = "测试简单缓存")
    @RequestMapping(value = "/simpleTest", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<PmsBrand> simpleTest() {
        //数据获取
        List<PmsBrand> brandList = brandService.list(1, 5);
        PmsBrand brand = brandList.get(0);
        //缓存写入
        String key = "redis:simple:" + brand.getId();
        redisService.set(key, brand);
        //缓存读取与数据一致性验证
        PmsBrand cacheBrand = (PmsBrand) redisService.get(key);
        return CommonResult.success(cacheBrand);
    }

    @Operation(summary = "测试Hash结构的缓存")
    @RequestMapping(value = "/hashTest", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<PmsBrand> hashTest() {
        //调用 brandService 的分页查询方法，获取第一页的5条品牌数据
        List<PmsBrand> brandList = brandService.list(1, 5);
        //从查询结果中提取第一个品牌对象。
        PmsBrand brand = brandList.get(0);
        //生成唯一的Redis键，格式为 redis:hash:{品牌ID}，例如 redis:hash:123
        String key = "redis:hash:" + brand.getId();
        //使用工具类（如Hutool的BeanUtil）将PmsBrand对象转为Map，【键为属性名，值为属性值】
        Map<String, Object> value = BeanUtil.beanToMap(brand);
        //将Map中的所有字段存入Redis哈希结构
        redisService.hSetAll(key, value);
        //获取Redis哈希中所有字段的值，返回 Map<Object, Object>
        Map<Object, Object> cacheValue = redisService.hGetAll(key);
        //将Redis返回的Map重新转换为 PmsBrand 对象
        PmsBrand cacheBrand = BeanUtil.toBean(cacheValue, PmsBrand.class);
        //将反序列化的品牌对象包装成成功响应返回
        return CommonResult.success(cacheBrand);
    }

    @Operation(summary = "测试Set结构的缓存")//无序且唯一的字符串集合
    @RequestMapping(value = "/setTest", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Set<Object>> setTest() {
        //获取第一页的5条品牌数据
        List<PmsBrand> brandList = brandService.list(1, 5);
        //设置键
        String key = "redis:set:all";
        //将 List<PmsBrand> 转换为 PmsBrand[] 数组,在将 PmsBrand[] 数组向上转型为 Object[] 数组
        //sAdd(key, ...)：Redis的 SAdd 命令，向Set中添加元素,["苹果", "华为", "小米", "三星", "OPPO"]
        redisService.sAdd(key, (Object[]) ArrayUtil.toArray(brandList, PmsBrand.class));
        //从Redis Set中移除 brandList 的第一个元素
        redisService.sRemove(key, brandList.get(0));
        Set<Object> cachedBrandList = redisService.sMembers(key);
        return CommonResult.success(cachedBrandList);
    }

    @Operation(summary = "测试List结构的缓存")//有序且可重复的字符串集合
    @RequestMapping(value = "/listTest", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<Object>> listTest() {
        //获取第1页的5条品牌数据（[A, B, C, D, E]）
        List<PmsBrand> brandList = brandService.list(1, 5);
        //定义 redis:list:all 作为存储键
        String key = "redis:list:all";
        //通过 lPushAll 将数据插入 Redis List，顺序变为 [E, D, C, B, A]
        redisService.lPushAll(key, (Object[]) ArrayUtil.toArray(brandList, PmsBrand.class));
        //删除原列表的第一个元素 A（实际删除的是 Redis List 的最后一个元素）
        redisService.lRemove(key, 1, brandList.get(0));
        //获取索引 0-3 的元素，返回 [E, D, C, B]
        List<Object> cachedBrandList = redisService.lRange(key, 0, 3);
        return CommonResult.success(cachedBrandList);
    }
}
