package com.offcn.dycommon.response;

import com.offcn.dycommon.enums.ResponseCodeEnume;

public class AppResponse<T> {

    private Integer code;
    private String message;
    private T data;

    public static<T> AppResponse<T> OK(T data){
        AppResponse response = new AppResponse();
        response.setCode(ResponseCodeEnume.SUCCESS.getCode());
        response.setMessage(ResponseCodeEnume.SUCCESS.getMessage());
        response.setData(data);
        return response;
    }

    public static<T> AppResponse<T> FAIL(T data){
        AppResponse response = new AppResponse();
        response.setCode(ResponseCodeEnume.FAIL.getCode());
        response.setMessage(ResponseCodeEnume.FAIL.getMessage());
        response.setData(data);
        return response;
    }

    public AppResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public AppResponse() {
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
