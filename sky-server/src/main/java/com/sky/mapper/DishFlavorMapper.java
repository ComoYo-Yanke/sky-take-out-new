package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    
    void insertBatch(List<DishFlavor> flavors);
    
    @Delete("delete dish_flavor where dish_id = #{dishId}")
    void deleteByDishId(Long dishId);

//    @Delete("delete from dish_flavor where dish_id = #{dishId}")
//    void deleteBatch(Long dishId);
    

}
