package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.User;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public interface UserMapper {
    
    @Select("select * from user where openid = #{openid}")
    User getByOpenid(String openid);
    
    Long insert(User user);
}
