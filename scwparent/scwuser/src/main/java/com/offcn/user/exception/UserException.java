package com.offcn.user.exception;

import com.offcn.user.enums.UserExceptionEnums;

public class UserException extends RuntimeException {

    public UserException(UserExceptionEnums exceptionEnum) {
        super(exceptionEnum.getMessage());
    }
}
