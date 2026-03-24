package com.ming.service.impl;

import cn.hutool.core.bean.BeanUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.*;
import cn.hutool.crypto.SecureUtil;

import com.ming.component.MyRedis;
import com.ming.constant.ML;
import com.ming.dto.*;
import com.ming.entity.*;
import com.ming.exception.IllegalParamException;
import com.ming.exception.RepeatRecordException;
import com.ming.exception.ServerErrorException;
import com.ming.mapper.UserMapper;
import com.ming.mapper.UserRoleMapper;
import com.ming.service.UserService;
import com.ming.util.JwtUtil;
import com.ming.util.MinioUtil;
import com.ming.util.UserUtil;
import com.ming.vo.LoginVO;
import com.ming.vo.UserSimpleListVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.relation.RelationManager;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.ming.entity.table.MenuTableDef.MENU;
import static com.ming.entity.table.RoleMenuTableDef.ROLE_MENU;
import static com.ming.entity.table.RoleTableDef.ROLE;
import static com.ming.entity.table.UserRoleTableDef.USER_ROLE;
import static com.ming.entity.table.UserTableDef.USER;
import static com.mybatisflex.core.query.QueryMethods.*;

/**
 * 用户表 服务层实现。
 *
 * @author Ming
 * @since v1.0.0
 */
@Service
@CacheConfig(cacheNames = "user")
public class UserServiceImpl extends ServiceImpl<UserMapper, User>  implements UserService {


    @Override
    @CacheEvict(allEntries = true)
    public boolean remove(QueryWrapper query) {
        return super.remove(query);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean removeByIds(Collection<? extends Serializable> ids) {
        return super.removeByIds(ids);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean update(User entity, QueryWrapper query) {
        return super.update(entity, query);
    }

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(User entity, boolean ignoreNulls) {
        return super.updateById(entity, ignoreNulls);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean updateBatch(Collection<User> entities, int batchSize) {
        return super.updateBatch(entities, batchSize);
    }

    @Override
    @Cacheable(key = "#id")
    public User getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public User getOne(QueryWrapper query) {
        return super.getOne(query);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public <R> R getOneAs(QueryWrapper query, Class<R> asType) {
        return super.getOneAs(query, asType);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public Object getObj(QueryWrapper query) {
        return super.getObj(query);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public <R> R getObjAs(QueryWrapper query, Class<R> asType) {
        return super.getObjAs(query, asType);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public List<Object> objList(QueryWrapper query) {
        return super.objList(query);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public <R> List<R> objListAs(QueryWrapper query, Class<R> asType) {
        return super.objListAs(query, asType);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public List<User> list(QueryWrapper query) {
        return super.list(query);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public <R> List<R> listAs(QueryWrapper query, Class<R> asType) {
        return super.listAs(query, asType);
    }

    /**
     * @deprecated 无法通过注解进行缓存操作。
     */
    @Override
    @Deprecated
    public List<User> listByIds(Collection<? extends Serializable> ids) {
        return super.listByIds(ids);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public long count(QueryWrapper query) {
        return super.count(query);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #page.getPageSize() + ':' + #page.getPageNumber() + ':' + #query.toSQL()")
    public <R> Page<R> pageAs(Page<R> page, QueryWrapper query, Class<R> asType) {
        return super.pageAs(page, query, asType);
    }
    @Resource
    private UserRoleMapper userRoleMapper;

    @CacheEvict(allEntries = true)
    @Override
    public boolean insert(UserInsertDTO dto) {
        String idcard = dto.getIdcard();
        // 校验身份证号
        if (!IdcardUtil.isValidCard(idcard)) {
            throw new IllegalParamException("身份证号错误");
        }
        // 登录账号查重
        // select count(*) from user where username = ?
        if (QueryChain.of(mapper)
                .where(USER.USERNAME.eq(dto.getUsername()))
                .exists()) {
            throw new RepeatRecordException("账号已存在");
        }
        // 身份证号查重
        // select count(*) from user where idcard = ?
        if (QueryChain.of(mapper)
                .where(USER.IDCARD.eq(dto.getIdcard()))
                .exists()) {
            throw new RepeatRecordException("身份证号已存在");
        }
        // 手机号码查重
        // select count(*) from user where phone = ?
        if (QueryChain.of(mapper)
                .where(USER.PHONE.eq(dto.getPhone()))
                .exists()) {
            throw new RepeatRecordException("手机号码已存在");
        }
        // 电子邮箱查重
        // select count(*) from user where email = ?
        if (QueryChain.of(mapper)
                .where(USER.EMAIL.eq(dto.getEmail()))
                .exists()) {
            throw new RepeatRecordException("邮箱已存在");
        }
        // 组装实体类
        User user = BeanUtil.copyProperties(dto, User.class);
        user.setNickname(RandomUtil.randomString(10));
        user.setGender(UserUtil.defaultGender(idcard));
        user.setAge(UserUtil.defaultAge(idcard));
        user.setZodiac(UserUtil.defaultZodiac(idcard));
        user.setAvatar(UserUtil.defaultAvatar(idcard));
        user.setProvince(UserUtil.defaultProvince(idcard));
        user.setPassword(SecureUtil.md5(user.getPassword()));
        user.setInfo(StrUtil.isEmpty(dto.getInfo()) ? "该用户很懒，没留下任何描述。" : dto.getInfo());
        user.setCreated(LocalDateTime.now());
        user.setUpdated(LocalDateTime.now());
        // insert into user (username, password, nickname, avatar, phone, email, gender, age, zodiac, province, realname, idcard, info, created, updated) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        return mapper.insert(user) > 0;
    }

    @Cacheable(key = "#p0", condition = "#p0 != null", unless = "#result == null")
    @Override
    public User select(Long id) {
        // select * from user where id = ?
        User user = mapper.selectOneWithRelationsById(id);
        if (ObjectUtil.isNull(user)) {
            throw new ServerErrorException("记录不存在");
        }
        // 数据脱敏
        return UserUtil.desensitization(user);
    }

    @Override
    @Cacheable(key = "#root.methodName", unless = "#result == null")
    public List<UserSimpleListVO> simpleList() {
        // select * from user
        return QueryChain.of(mapper)
                .withRelations()
                .listAs(UserSimpleListVO.class);
    }

    @Cacheable(key = "#root.methodName + ':' + #p0.toString()", condition = "#p0 != null", unless = "#result == null")
    @Override
    public Page<User> page(UserPageDTO dto) {
        QueryChain<User> queryChain = QueryChain.of(mapper);
        // username条件
        String username = dto.getUsername();
        if (ObjectUtil.isNotNull(username)) {
            queryChain.where(USER.USERNAME.like(username));
        }
        // nickname条件
        String nickname = dto.getNickname();
        if (ObjectUtil.isNotNull(nickname)) {
            queryChain.where(USER.NICKNAME.like(nickname));
        }
        // phone条件
        String phone = dto.getPhone();
        if (ObjectUtil.isNotNull(phone)) {
            queryChain.where(USER.PHONE.like(phone));
        }
        // DB分页
        Page<User> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        Page<User> result = queryChain.withRelations().page(page);
        // 脱敏
        result.setRecords(UserUtil.desensitization(result.getRecords()));
        return result;
    }

    @CacheEvict(allEntries = true)
    @Override
    public boolean update(UserUpdateDTO dto) {
        // 邮箱查重
        // select count(*) from user where email = ? and id != ?
        if (StrUtil.isNotEmpty(dto.getEmail()) && QueryChain.of(mapper)
                .where(USER.EMAIL.eq(dto.getEmail()))
                .and(USER.ID.ne(dto.getId()))
                .exists()) {
            throw new RepeatRecordException("邮箱已存在");
        }
        // 组装实体类
        User user = BeanUtil.copyProperties(dto, User.class);
        user.setUpdated(LocalDateTime.now());
        // update user set username=?, password=?, nickname=?, avatar=?, phone=?, email=?, gender=?, age=?, zodiac=?, province=?, realname=?, idcard=?, info=?, updated=? where id = ?
        return UpdateChain.of(user)
                .where(USER.ID.eq(user.getId()))
                .update();
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(allEntries = true)
    @Override
    public boolean delete(Long id) {
        // 删除中间表
        // delete from user_role where fk_user_id = ?
        UpdateChain.of(userRoleMapper)
                .where(USER_ROLE.FK_USER_ID.eq(id))
                .remove();
        // 删除基本表
        // delete from user where id = ?
        return mapper.deleteById(id) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(allEntries = true)
    @Override
    public boolean deleteBatch(List<Long> ids) {
        // 删除中间表
        // delete from user_role where fk_user_id in (?)
        UpdateChain.of(userRoleMapper)
                .where(USER_ROLE.FK_USER_ID.in(ids))
                .remove();
        // 删除基本表
        // delete from user where id in (?)
        return mapper.deleteBatchByIds(ids) == ids.size();
    }
    @CacheEvict(allEntries = true)
    @Override
    public boolean resetPassword(Long id) {
        // 重置密码
        // update user set password = ? where id = ?
        return UpdateChain.of(mapper)
                .set(USER.PASSWORD, SecureUtil.md5(ML.User.DEFAULT_PASSWORD))
                .where(USER.ID.eq(id))
                .update();
    }
    @CacheEvict(allEntries = true)
    @Override
    public boolean updatePassword(UserUpdatePasswordDTO dto) {
        Long userId = dto.getId();
        // 判断用户是否存在以及原密码是否正确
        // select count(*) from user where id = ? and password = ?
        if (!QueryChain.of(mapper)
                .where(USER.ID.eq(userId))
                .and(USER.PASSWORD.eq(SecureUtil.md5(dto.getOldPassword())))
                .exists()) {
            throw new IllegalParamException("账号或密码错误");
        }
        // update user set password = ? where id = ?
        return UpdateChain.of(mapper)
                .set(USER.PASSWORD, SecureUtil.md5(dto.getNewPassword()))
                .where(USER.ID.eq(userId))
                .update();
    }
    @Override
    public List<UserExcelDTO> getExcelData() {
        // 查询全部用户记录
        // select * from user
        List<User> users = QueryChain.of(mapper).withRelations().list();
        // 类型转换：List<User> -> List<UserExcelDTO>
        List<UserExcelDTO> result = new ArrayList<>();
        users.forEach(user -> {
            UserExcelDTO userExcelDTO = BeanUtil.copyProperties(user, UserExcelDTO.class);
            userExcelDTO.setGender(ML.User.genderFormat(user.getGender()));
            userExcelDTO.setRealname(DesensitizedUtil.chineseName(user.getRealname()));
            userExcelDTO.setIdcard(DesensitizedUtil.idCardNum(user.getIdcard(), 6, 3));
            userExcelDTO.setPhone(DesensitizedUtil.mobilePhone(user.getPhone()));
            result.add(userExcelDTO);
        });
        return result;
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    @CacheEvict(allEntries = true)
    public String uploadAvatar(MultipartFile newFile, Long id) {
        // 按主键查询记录
        // select * from user where id = ?
        User user = mapper.selectOneById(id);
        if (ObjectUtil.isNull(user)) {
            throw new ServerErrorException("记录不存在");
        }
        // 备份旧文件名
        String oldFileName = user.getAvatar();
        // 生成新文件名
        String newFileName = MinioUtil.randomFilename(newFile);
        // DB更新文件名
        user.setAvatar(newFileName);
        if (mapper.update(user) <= 0) {
            throw new ServerErrorException("DB更新失败");
        }
        try {
            // MinIO删除旧文件（默认文件不删除）
            if (!ML.User.DEFAULT_AVATARS.contains(oldFileName)) {
                MinioUtil.delete(oldFileName, ML.MinIO.AVATAR_DIR, ML.MinIO.BUCKET_NAME);
            }
            // MinIO上传新文件
            MinioUtil.upload(newFile, newFileName, ML.MinIO.AVATAR_DIR, ML.MinIO.BUCKET_NAME);
        } catch (Exception e) {
            throw new ServerErrorException("MinIO操作失败：" + e.getMessage());
        }
        // 返回新文件名
        return newFileName;
    }
    @Resource
    private MyRedis redis;

    @Override
    public String getUnboundVcode(Long id) {
        // 通过用户主键查询旧的手机号码
        // select phone from user where id = ?
        String phone = QueryChain.of(mapper)
                .select(USER.PHONE)
                .where(USER.ID.eq(id))
                .objAs(String.class);
        if (ObjectUtil.isNull(phone)) {
            throw new IllegalParamException("手机号码不存在");
        }
        // 将短信验证码存入redis中，有效期5分钟
        String key = ML.Redis.UNBOUND_VCODE_PREFIX + phone;
        String val = RandomUtil.randomNumbers(6);
        redis.setEx(key, val, 5, TimeUnit.MINUTES);
        // todo: 向指定手机号码发送验证码
        // 将短信验证码返回给客户端
        return val;
    }

    @Override
    public boolean checkUnboundVcode(Long id, String vcode) {
        // 通过用户主键获取 phone 字段
        // select phone from user where id = ?
        String phone = QueryChain.of(mapper)
                .select(USER.PHONE)
                .where(USER.ID.eq(id))
                .objAs(String.class);
        if (ObjectUtil.isNull(phone)) {
            throw new IllegalParamException("手机号码不存在");
        }
        // 校验验证码是否有效
        String key = ML.Redis.UNBOUND_VCODE_PREFIX + phone;
        String vcodeFromRedis = redis.get(key);
        if (ObjectUtil.isNull(vcodeFromRedis)) {
            throw new IllegalParamException("验证码已失效");
        }
        // 校验验证码是否正确，校验成功后，删除旧的验证码
        boolean result = vcodeFromRedis.equals(vcode);
        if (result) redis.del(key);
        return result;
    }

    @Override
    public String getBoundVcode(String phone) {
        // 手机号码查重
        // select count(*) from user where phone = ?
        if (QueryChain.of(mapper)
                .select(USER.PHONE)
                .where(USER.PHONE.eq(phone))
                .exists()) {
            throw new RepeatRecordException("手机号码已存在");
        }
        // 将短信验证码存入redis中，有效期5分钟
        String key = ML.Redis.BOUND_VCODE_PREFIX + phone;
        String val = RandomUtil.randomNumbers(6);
        redis.setEx(key, val, 5, TimeUnit.MINUTES);
        // todo: 向指定手机号码发送验证码
        // 将短信验证码返回给客户端
        return val;
    }

    @Override
    public boolean updatePhone(UserUpdatePhoneDTO dto) {
        String phone = dto.getPhone();
        // 手机号码查重
        // select count(*) from user where phone = ?
        if (QueryChain.of(mapper)
                .select(USER.PHONE)
                .where(USER.PHONE.eq(dto.getPhone()))
                .exists()) {
            throw new RepeatRecordException("手机号码已存在");
        }
        // 校验验证码是否有效
        String key = ML.Redis.BOUND_VCODE_PREFIX + phone;
        String vcodeFromRedis = redis.get(key);
        if (ObjectUtil.isNull(vcodeFromRedis)) {
            throw new IllegalParamException("验证码已失效");
        }
        // 校验验证码是否正确
        if (!vcodeFromRedis.equals(dto.getVcode())) {
            throw new IllegalParamException("验证码错误");
        }
        // 修改用户手机号，修改成功后，删除旧的验证码
        // update user set phone = ? where id = ?
        boolean result = UpdateChain.of(mapper)
                .set(USER.PHONE, phone)
                .where(USER.ID.eq(dto.getId()))
                .update();
        if (result) redis.del(key);
        return result;
    }
    @Override
    public LoginVO loginByAccount(LoginByAccountDTO dto) {
        // 按账号密码查询用户记录
        // select * from user where username = ? and password = ?
        User user = QueryChain.of(mapper)
                .where(USER.USERNAME.eq(dto.getUsername()))
                .and(USER.PASSWORD.eq(SecureUtil.md5(dto.getPassword())))
                .one();
        if (ObjectUtil.isNull(user)) {
            throw new IllegalParamException("账号密码错误");
        }
        // 构建登录VO
        return buildLoginVO(user);
    }

    @Override
    public String getVcode(String phone) {
        // 检查手机号码是否存在
        // select count(*) from user where phone = ?
        if (!QueryChain.of(mapper)
                .where(USER.PHONE.eq(phone))
                .exists()) {
            throw new IllegalParamException("手机号码不存在");
        }
        // 将短信验证码存入redis中，有效期5分钟
        String key = ML.Redis.LOGIN_VCODE_PREFIX + phone;
        String val = RandomUtil.randomNumbers(6);
        redis.setEx(key, val, 5, TimeUnit.MINUTES);
        // todo: 向指定手机号码发送验证码
        // 将短信验证码返回给客户端
        return val;
    }

    @Override
    public LoginVO loginByPhone(LoginByPhoneDTO dto) {
        // 校验验证码是否有效
        String key = ML.Redis.LOGIN_VCODE_PREFIX + dto.getPhone();
        String vcodeFromRedis = redis.get(key);
        if (ObjectUtil.isNull(vcodeFromRedis) || !vcodeFromRedis.equals(dto.getVcode())) {
            throw new IllegalParamException("验证码无效");
        }
        // 根据手机号码查询用户记录
        // select * from user where phone = ?
        User user = QueryChain.of(mapper)
                .where(USER.PHONE.eq(dto.getPhone()))
                .one();
        if (ObjectUtil.isNull(user)) {
            throw new IllegalParamException("手机号码不存在");
        }
        // 删除旧的验证码
        redis.del(key);
        return buildLoginVO(user);
    }

    /**
     * 组装LoginVO
     *
     * @param user 用户实体
     * @return LoginVO: 包含用户信息，对应该用户的Token令牌，角色标题列表以及菜单列表
     */
    private LoginVO buildLoginVO(User user) {
        LoginVO result = new LoginVO();
        // 查询用户角色ID列表
        // select fk_role_id from user_role where fk_user_id = ?
        List<Long> roleIds = QueryChain.of(UserRole.class)
                .select(USER_ROLE.FK_ROLE_ID)
                .where(USER_ROLE.FK_USER_ID.eq(user.getId()))
                .objListAs(Long.class);
        // 用户不存在角色
        if (CollUtil.isEmpty(roleIds)) {
            result.setRoleTitles(null);
            result.setMenus(null);
            result.setUser(UserUtil.desensitization(user));
            result.setToken(JwtUtil.build(user.getId(), user.getNickname(), user.getAvatar()));
            return result;
        }
        // 查询角色标题列表
        // select title from role where id in (1, 2, 3, 4, 5)
        List<String> roleTitles = QueryChain.of(Role.class)
                .select(ROLE.TITLE)
                .where(ROLE.ID.in(roleIds))
                .objListAs(String.class);
        // 查询用户菜单ID列表
        // select fk_menu_id from role_menu where fk_role_id in (1, 2, 3, 4, 5)
        List<Long> menuIds = QueryChain.of(RoleMenu.class)
                .select(ROLE_MENU.FK_MENU_ID)
                .where(ROLE_MENU.FK_ROLE_ID.in(roleIds))
                .objListAs(Long.class);
        // 角色不存在菜单
        if (CollUtil.isEmpty(menuIds)) {
            result.setRoleTitles(roleTitles);
            result.setMenus(null);
            result.setUser(UserUtil.desensitization(user));
            result.setToken(JwtUtil.build(user.getId(), user.getNickname(), user.getAvatar()));
            return result;
        }
        // 查询用户菜单列表，只查询父菜单，级联子菜单
        // select * from menu where id in (1, 2, 3, 4, 5) and pid = 0 order by idx asc, id desc
        RelationManager.addIgnoreRelations("parentMenu");
        List<Menu> menus = QueryChain.of(Menu.class)
                .where(MENU.ID.in(menuIds))
                .and(MENU.PID.eq(ML.Menu.ROOT_ID))
                .orderBy(MENU.IDX.asc(), MENU.ID.desc())
                .withRelations()
                .list();
        // 组装VO
        result.setRoleTitles(roleTitles);
        result.setMenus(menus);
        result.setUser(UserUtil.desensitization(user));
        result.setToken(JwtUtil.build(user.getId(), user.getNickname(), user.getAvatar()));
        return result;
    }
    @Cacheable(key = "#root.methodName")
    @Override
    public Map<String, Object> statistics() {
        Map<String, Object> result = new HashMap<>();
        // 统计用户性别比例
        // select gender as name, count(*) as value from `user` group by gender
        result.put("genderCount", QueryChain.of(mapper)
                .select(USER.GENDER.as("name"), QueryMethods.count().as("value"))
                .groupBy(USER.GENDER)
                .orderBy(USER.GENDER.asc())
                .listAs(Map.class));
        // 统计今日用户数
        // select count(*) from `user` where datediff(curdate(), date_format(created, '%Y-%m-%d')) = 0
        double todayCount = QueryChain.of(mapper)
                .where(dateDiff(currentDate(), dateFormat(USER.CREATED, "%Y-%m-%d")).eq(0))
                .count();
        // 统计昨日用户数
        // select count(*) from `user` where datediff(curdate(), date_format(created, '%Y-%m-%d')) = 1
        double yesterdayCount = QueryChain.of(mapper)
                .where(dateDiff(currentDate(), dateFormat(USER.CREATED, "%Y-%m-%d")).eq(1))
                .count();
        // 统计今年用户数
        // select count(*) from `user` where year(created) = year(current_date);
        double thisYearCount = QueryChain.of(mapper)
                .where(year(USER.CREATED).eq(year(currentDate())))
                .count();
        // 统计去年用户总数
        // select count(*) from `user` where year(created) - year(current_date) = -1;
        double lastYearCount = QueryChain.of(mapper)
                .where(year(USER.CREATED).subtract(year(currentDate())).eq(-1))
                .count();
        result.put("todayCount", todayCount);
        result.put("yesterdayCount", yesterdayCount);
        result.put("dayIncrease", increase(todayCount, yesterdayCount));
        result.put("thisYearCount", thisYearCount);
        result.put("lastYearCount", lastYearCount);
        result.put("yearIncrease", increase(thisYearCount, lastYearCount));
        return result;
    }

    /**
     * 计算a到b的增长率
     *
     * @param a 第一个操作数
     * @param b 第二个操作数
     * @return 保留两位小数的增长率
     */
    private static String increase(double a, double b) {
        if (b == 0) {
            return a > b ? "-100.00" : a < b ? "100.00" : "0";
        }
        return String.format("%.2f", (a - b) / b);
    }
}
