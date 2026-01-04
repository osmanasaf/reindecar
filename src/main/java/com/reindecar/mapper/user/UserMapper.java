package com.reindecar.mapper.user;

import com.reindecar.entity.user.User;
import com.reindecar.dto.user.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "fullName", expression = "java(user.getFullName())")
    @Mapping(target = "branchName", ignore = true)
    UserResponse toResponse(User user);
}
