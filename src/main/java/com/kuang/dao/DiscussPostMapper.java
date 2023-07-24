package com.kuang.dao;

import com.kuang.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface DiscussPostMapper {

    /**
     * 分页查询
     * userId=0为所有帖子，1为我的帖子
     * 注：@Param("")用来给函数中的参数起别名
     * orderMode=0：最新  orderMode=1：最热
     *
     * @return
     */
    List<DiscussPost> selectDiscussPosts(@Param("userId") int userId, @Param("offset") int offset,
                                         @Param("limit") int limit, @Param("orderMode") int orderMode);

    /**
     * 为分页查询服务的查询总条数
     * 给参数起别名，如果只有一个参数并且要在<if>里使用，则必须加别名
     *
     * @return
     */
    int selectDiscussRows(@Param("userId") int userId);

    /**
     * 发布帖子
     */
    int insertDiscussPost(DiscussPost discussPost);

    /**
     * 查看帖子详情
     */
    DiscussPost selectDiscussPostById(int id);

    int updateCommentCount(@Param("id") int id, @Param("commentCount") int commentCount);

    int updateType(@Param("id") int id, @Param("type") int type);

    int updateStatus(@Param("id") int id, @Param("status") int status);

    int updateScore(@Param("id") int id, @Param("score") double score);

}
