package com.lzy.mall.tiny.common.api;

/**
 * 通用API响应封装类（支持泛型）
 * <T> 表示响应数据的数据类型
 * 标准化接口返回格式，包含状态码、消息提示、业务数据三要素
 * 设计目标：统一微服务架构下的接口响应规范，便于前后端联调
 */
public class CommonResult<T> {
    /**
     * 状态码（建议遵循HTTP状态码规范或自定义业务码体系）
     * 示例：200-成功，400-参数错误，500-系统异常
     */
    private long code;

    /**
     * 业务提示信息（面向开发者或终端用户的友好提示）
     * 建议格式：中文说明 + 错误定位码（如："参数校验失败[E1001]"）
     */
    private String message;

    /**
     * 业务数据载体（泛型支持复杂数据结构）
     * 特殊场景可为空（如错误响应时）
     */
    private T data;

    /**
     * 受保护构造器（推荐使用静态工厂方法创建实例）
     */
    protected CommonResult() {
    }

    /**
     * 全参数构造器（适用于需要完全自定义的场景）
     * @param code    自定义状态码
     * @param message 自定义消息内容
     * @param data    自定义数据对象
     */
    protected CommonResult(long code, String message, T data) {
        this.code  = code;
        this.message  = message;
        this.data  = data;
    }

    /**
     * 标准成功响应（携带数据，使用默认成功消息）
     * @param data 业务数据（需符合泛型类型约束）
     * @param <T>  响应数据类型
     * @return 封装后的成功响应对象
     *
     * @示例 CommonResult.success(userDTO)
     * => {code:200, message:"操作成功", data:{...}}
     */
    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<T>(
                ResultCode.SUCCESS.getCode(),
                ResultCode.SUCCESS.getMessage(),
                data
        );
    }

    /**
     * 自定义消息的成功响应（需确保code仍为成功状态）
     * @param data    业务数据
     * @param message 自定义成功提示（如："用户创建成功"）
     * @return 携带自定义消息的成功响应
     *
     * @注意 需谨慎使用，避免成功状态下的message不一致性
     */
    public static <T> CommonResult<T> success(T data, String message) {
        return new CommonResult<T>(
                ResultCode.SUCCESS.getCode(),
                message,
                data
        );
    }

    /**
     * 基于错误码的失败响应（推荐标准错误场景使用）
     * @param errorCode 错误码枚举（需实现IErrorCode接口）
     * @return 预定义错误响应
     *
     * @示例 CommonResult.failed(ResultCode.USER_NOT_FOUND)
     * => {code:404, message:"用户不存在", data:null}
     */
    public static <T> CommonResult<T> failed(IErrorCode errorCode) {
        return new CommonResult<T>(
                errorCode.getCode(),
                errorCode.getMessage(),
                null
        );
    }

    /**
     * 自定义消息的失败响应（适用于动态错误信息）
     * @param message 错误详情描述
     * @return 携带自定义错误消息的响应
     *
     * @示例 CommonResult.failed(" 库存不足，剩余量：5")
     * => {code:500, message:"库存不足，剩余量：5", data:null}
     */
    public static <T> CommonResult<T> failed(String message) {
        return new CommonResult<T>(
                ResultCode.FAILED.getCode(),
                message,
                null
        );
    }

    /**
     * 默认失败响应（使用预定义的通用失败错误码）
     * @return 标准失败响应
     */
    public static <T> CommonResult<T> failed() {
        return failed(ResultCode.FAILED);
    }

    /**
     * 参数校验失败响应（默认消息）
     * @return 预定义校验错误响应
     *
     * @关联 通常与Spring Validation结合使用
     */
    public static <T> CommonResult<T> validateFailed() {
        return failed(ResultCode.VALIDATE_FAILED);
    }

    /**
     * 携带校验详情的参数错误响应
     * @param message 校验失败详情（如："密码长度需6-20位"）
     * @return 包含具体校验错误的响应
     */
    public static <T> CommonResult<T> validateFailed(String message) {
        return new CommonResult<T>(
                ResultCode.VALIDATE_FAILED.getCode(),
                message,
                null
        );
    }

    /**
     * 未认证响应（通常伴随401状态码）
     * @param data 可选附加数据（如跳转URL）
     * @return 身份验证失败响应
     */
    public static <T> CommonResult<T> unauthorized(T data) {
        return new CommonResult<T>(
                ResultCode.UNAUTHORIZED.getCode(),
                ResultCode.UNAUTHORIZED.getMessage(),
                data
        );
    }

    /**
     * 权限不足响应（通常伴随403状态码）
     * @param data 可选附加数据（如所需权限列表）
     * @return 授权失败响应
     */
    public static <T> CommonResult<T> forbidden(T data) {
        return new CommonResult<T>(
                ResultCode.FORBIDDEN.getCode(),
                ResultCode.FORBIDDEN.getMessage(),
                data
        );
    }

    //------------------- Getter/Setter 方法（需确保JSON序列化兼容） -------------------//

    /** 获取响应状态码 */
    public long getCode() { return code; }

    /**
     * 设置状态码（谨慎使用，建议通过工厂方法统一管理）
     * @warning 直接修改可能导致状态码体系混乱
     */
    public void setCode(long code) { this.code  = code; }

    /** 获取业务提示信息 */
    public String getMessage() { return message; }

    /** 设置消息内容（适用于需要动态修改的场景） */
    public void setMessage(String message) { this.message  = message; }

    /** 获取业务数据对象 */
    public T getData() { return data; }

    /** 设置数据内容（适用于后续数据处理） */
    public void setData(T data) { this.data  = data; }
}