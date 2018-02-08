package com.netty.configcenter.exception;

/**
 * @Author：zeqi
 * @Date: Created in 16:16 8/2/18.
 * @Description:
 */
public class NodeExistsException extends RuntimeException {

    public NodeExistsException(String message) {
        super(message);
    }
}
