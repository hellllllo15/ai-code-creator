package com.example.code.common;

import lombok.Data;

import java.io.Serializable;

@Data
/// 删除请求包装类
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}
