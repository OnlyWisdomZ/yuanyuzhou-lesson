package com.ming.service;

import com.ming.dto.SeasonInsertDTO;
import com.ming.dto.SeasonPageDTO;
import com.ming.dto.SeasonUpdateDTO;
import com.ming.vo.SeasonSimpleListVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.ming.entity.Season;

import java.util.List;

/**
 * 季次表 服务层。
 *
 * @author Ming
 * @since v1.0.0
 */
public interface SeasonService extends IService<Season> {
    boolean insert(SeasonInsertDTO dto);
    Season select(Long id);
    List<SeasonSimpleListVO> simpleList();
    Page<Season> page(SeasonPageDTO dto);
    boolean update(SeasonUpdateDTO dto);
    boolean delete(Long id);
    boolean deleteBatch(List<Long> ids);
}
