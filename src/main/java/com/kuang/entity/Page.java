package com.kuang.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Page {
    //当前页面
    private int current=1;
    //显示上限
    private int limit = 6;
    //数据总数(用于计算总页数)
    private int rows;
    //查询路径(用于复用分页链接)
    private String path;

    /**
     * 获取当前页的起始行
     **/
    public int getOffset(){
        //current*limit-limit
        return (current-1)*limit;
    }

    /**
     * 获取总页数
     **/
    public int getTotal(){
        //rows/limit[+1]
        if (rows%limit==0){
            return rows/limit;
        }else{
            return rows/limit+1;
        }
    }

    /**
     * 获取起始页码
     **/
    public int getFrom(){
        int from=current-2;
        return from < 1 ? 1 : from;
    }

    /**
     * 获取结束页码
     **/
    public int getTo(){
        int to=current+2;
        int total=getTotal();
        return to > total ? total : to;
    }
}
