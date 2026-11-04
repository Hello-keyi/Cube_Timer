package com.example.backend.common;

import lombok.Data;

/**
 * 统一返回结构
 * code: 0 = 成功，1 = 失败
 * 2026.10.27
 * yi：这肯定是因为项目还太小，为了简化错误类型而做的简化
 * 行吧，我以后再改，不着急抓细节，应该抓骨干先
 */
@Data
public class Result<T> {

    //用来通知的变量
    private Integer code;
    private String msg;

    //装数据
    private T data;

    /** 成功，带数据 */
    public static <T> Result<T> success(T data) {

        //生成一个装结果的容器
        Result<T> r = new Result<>();

        //把关于状态的信息和数据装进返回的容器
        r.setCode(0);
        r.setData(data);

        return r;
    }

    /** 成功，无数据 */
    public static Result<Void> success() {
        return success(null);
    }

    /** 失败 */
    public static <T> Result<T> error(String msg) {

        //生成容器
        Result<T> r = new Result<>();

        //把关于状态的信息装进去
        r.setCode(1);
        r.setMsg(msg);

        return r;
    }
}