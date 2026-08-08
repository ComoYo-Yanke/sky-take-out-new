package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {
    
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    
    @Override
    @Transactional
    public void add(ShoppingCartDTO shoppingCartDTO){
        
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        // 判断当前商品是否已经添加在购物车 select
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        
        // 如果已经存在则执行update操作，修改数量
        if(list != null && list.size() > 0){
            ShoppingCart cart = list.get(0);
            cart.setNumber(cart.getNumber() + 1);
            shoppingCartMapper.updateCartNumber(cart);
        }else{
            // 不存在则执行insert操作
            Long dishId = shoppingCart.getDishId();
            if(dishId != null){
                // 查询菜品数据
                Dish dish = dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            } else {
                // 查询套餐数据
                Setmeal setmeal = setmealMapper.getById(shoppingCart.getSetmealId());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            
            shoppingCartMapper.insert(shoppingCart);
        }
        
    }
    
    @Override
    public List<ShoppingCart> showShoppingCart(){
        
        ShoppingCart cart = ShoppingCart.builder()
                .userId(BaseContext.getCurrentId())
                .build();
        List<ShoppingCart> list = shoppingCartMapper.list(cart);
        return list;
        
    }
    @Override
    public void clean(Long currentId){
        shoppingCartMapper.deleteAllCartByUserId(currentId);
    }
    
    @Override
    @Transactional
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO){
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        // 查询对应用户的购物车对应菜单(菜品或套餐)
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if(list == null || list.size() == 0) return;
        shoppingCart = list.get(0);
//        log.info("查看1:{}", shoppingCart);
        
        // 如果数量大于1，直接修改数量即可
        if(shoppingCart.getNumber() > 1){
            shoppingCart.setNumber(shoppingCart.getNumber() - 1);
            shoppingCartMapper.updateCartNumber(shoppingCart);
        }else{
            // 如果数量为1，直接删去即可
            shoppingCartMapper.deleteById(shoppingCart.getId());
        }
//        log.info("查看:{}", shoppingCart);
    }
    
    
}
