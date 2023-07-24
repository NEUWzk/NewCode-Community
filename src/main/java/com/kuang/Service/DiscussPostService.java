package com.kuang.Service;

import com.kuang.dao.DiscussPostMapper;
import com.kuang.entity.DiscussPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    //分页查询全部帖子
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit, int orderMode)
    {
//        if (userId == 0 && orderMode == 1) {
//            logger.debug("正在从Caffeine缓存中加载热门帖子！");
//            return postListCache.get(offset + ":" + limit);
//        }
        return discussPostMapper.selectDiscussPosts(userId, offset, limit, orderMode);
    }

    public int findDiscussPostRows(int userId) {
        return discussPostMapper.selectDiscussRows(userId);
    }

}
