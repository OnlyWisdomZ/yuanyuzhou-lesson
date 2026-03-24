package com.ming.service;

import com.ming.dto.ArticleInsertDTO;
import com.ming.dto.ArticlePageDTO;
import com.ming.dto.ArticleUpdateDTO;
import com.ming.vo.ArticleSimpleListVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.ming.entity.Article;

import java.util.List;

/**
 * 新闻表 服务层。
 *
 * @author Ming
 * @since v1.0.0
 */
public interface ArticleService extends IService<Article> {
    boolean insert(ArticleInsertDTO dto);
    Article select(Long id);
    List<ArticleSimpleListVO> simpleList();
    Page<Article> page(ArticlePageDTO dto);
    boolean update(ArticleUpdateDTO dto);
    boolean delete(Long id);
    boolean deleteBatch(List<Long> ids);
    /**
     * 查看前N条新闻记录，根据序号升序，序号相同根据ID降序
     *
     * @param n 前N条
     * @return 前N条新闻记录
     */
    List<Article> top(Long n);
}
