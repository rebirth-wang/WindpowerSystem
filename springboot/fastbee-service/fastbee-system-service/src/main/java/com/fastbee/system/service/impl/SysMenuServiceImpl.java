package com.fastbee.system.service.impl;

import static com.fastbee.common.extend.utils.SecurityUtils.getLoginUser;

import java.util.*;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.fastbee.common.constant.Constants;
import com.fastbee.common.constant.UserConstants;
import com.fastbee.common.extend.core.domin.entity.SysDept;
import com.fastbee.common.extend.core.domin.entity.SysRole;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.core.domin.model.LoginUser;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.system.convert.SysMenuConvert;
import com.fastbee.system.domain.SysMenu;
import com.fastbee.system.domain.SysMenuTranslate;
import com.fastbee.system.domain.model.TreeSelect;
import com.fastbee.system.domain.vo.MetaVo;
import com.fastbee.system.domain.vo.RouterVo;
import com.fastbee.system.domain.vo.SysMenuVO;
import com.fastbee.system.mapper.SysDeptMapper;
import com.fastbee.system.mapper.SysMenuMapper;
import com.fastbee.system.mapper.SysRoleMapper;
import com.fastbee.system.mapper.SysRoleMenuMapper;
import com.fastbee.system.service.ISysMenuService;
import com.fastbee.system.service.ISysMenuTranslateService;

/**
 * 菜单 业务层处理
 *
 * @author ruoyi
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {
    public static final String PREMISSION_STRING = "perms[\"{0}\"]";

    @Resource
    private SysMenuMapper menuMapper;

    @Resource
    private SysRoleMapper roleMapper;

    @Resource
    private SysRoleMenuMapper roleMenuMapper;

    @Resource
    private SysDeptMapper sysDeptMapper;
    @Resource
    private ISysMenuTranslateService menuTranslateService;

    /** 代码生成区域 可直接覆盖**/
    /**
     * 查询菜单权限
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param menuId 主键
     * @return 菜单权限
     */
    @Override
    @Cacheable(cacheNames = "SysMenu", key = "#menuId")
    public SysMenu queryByIdWithCache(Long menuId){
        return this.getById(menuId);
    }

    /**
     * 查询菜单权限
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param menuId 主键
     * @return 菜单权限
     */
    @Override
    @Cacheable(cacheNames = "SysMenu", key = "#menuId")
    public SysMenu selectSysMenuById(Long menuId){
        return this.getById(menuId);
    }

    /**
     * 查询菜单权限分页列表
     *
     * @param sysMenu 菜单权限
     * @return 菜单权限
     */
    @Override
    public Page<SysMenuVO> pageSysMenuVO(SysMenu sysMenu) {
        LambdaQueryWrapper<SysMenu> lqw = buildQueryWrapper(sysMenu);
        Page<SysMenu> sysMenuPage = baseMapper.selectPage(new Page<>(sysMenu.getPageNum(), sysMenu.getPageSize()), lqw);
        return SysMenuConvert.INSTANCE.convertSysMenuVOPage(sysMenuPage);
    }

    /**
     * 查询菜单权限列表
     *
     * @param sysMenu 菜单权限
     * @return 菜单权限
     */
    @Override
    public List<SysMenuVO> listSysMenuVO(SysMenu sysMenu) {
        LambdaQueryWrapper<SysMenu> lqw = buildQueryWrapper(sysMenu);
        List<SysMenu> sysMenuList = baseMapper.selectList(lqw);
        return SysMenuConvert.INSTANCE.convertSysMenuVOList(sysMenuList);
    }

    private LambdaQueryWrapper<SysMenu> buildQueryWrapper(SysMenu query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<SysMenu> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getMenuId() != null, SysMenu::getMenuId, query.getMenuId());
        lqw.like(StringUtils.isNotBlank(query.getMenuName()), SysMenu::getMenuName, query.getMenuName());
        lqw.eq(query.getParentId() != null, SysMenu::getParentId, query.getParentId());
        lqw.eq(query.getOrderNum() != null, SysMenu::getOrderNum, query.getOrderNum());
        lqw.eq(StringUtils.isNotBlank(query.getPath()), SysMenu::getPath, query.getPath());
        lqw.eq(StringUtils.isNotBlank(query.getComponent()), SysMenu::getComponent, query.getComponent());
        lqw.eq(StringUtils.isNotBlank(query.getQueryParam()), SysMenu::getQueryParam, query.getQueryParam());
        lqw.eq(query.getIsFrame() != null, SysMenu::getIsFrame, query.getIsFrame());
        lqw.eq(query.getIsCache() != null, SysMenu::getIsCache, query.getIsCache());
        lqw.eq(StringUtils.isNotBlank(query.getMenuType()), SysMenu::getMenuType, query.getMenuType());
        lqw.eq(StringUtils.isNotBlank(query.getVisible()), SysMenu::getVisible, query.getVisible());
        lqw.eq(query.getStatus() != null, SysMenu::getStatus, query.getStatus());
        lqw.eq(StringUtils.isNotBlank(query.getPerms()), SysMenu::getPerms, query.getPerms());
        lqw.eq(StringUtils.isNotBlank(query.getIcon()), SysMenu::getIcon, query.getIcon());
        lqw.eq(StringUtils.isNotBlank(query.getCreateBy()), SysMenu::getCreateBy, query.getCreateBy());
        lqw.eq(query.getCreateTime() != null, SysMenu::getCreateTime, query.getCreateTime());
        lqw.eq(StringUtils.isNotBlank(query.getUpdateBy()), SysMenu::getUpdateBy, query.getUpdateBy());
        lqw.eq(query.getUpdateTime() != null, SysMenu::getUpdateTime, query.getUpdateTime());
        lqw.eq(StringUtils.isNotBlank(query.getRemark()), SysMenu::getRemark, query.getRemark());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(SysMenu::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    /**
     * 新增菜单权限
     *
     * @param add 菜单权限
     * @return 是否新增成功
     */
    @Override
    public Boolean insertWithCache(SysMenu add) {
        validEntityBeforeSave(add);
        boolean saved = this.save(add);
        SysMenuTranslate translate = new SysMenuTranslate();
        if (saved) {
            translate.setId(add.getMenuId());
            translate.setZhCn(add.getMenuName());
            translate.setEnUs(add.getMenuName_en_US());
            menuTranslateService.insertWithCache(translate);
        }
        return saved;
    }

    /**
     * 修改菜单权限
     *
     * @param update 菜单权限
     * @return 是否修改成功
     */
    @Override
    @CacheEvict(cacheNames = "SysMenu", key = "#update.menuId")
    public Boolean updateWithCache(SysMenu update) {
        validEntityBeforeSave(update);
        boolean updated = this.updateById(update);
        SysMenuTranslate translate = new SysMenuTranslate();
        if (updated) {
            translate.setId(update.getMenuId());
            translate.setZhCn(update.getMenuName());
            translate.setEnUs(update.getMenuName_en_US());
            menuTranslateService.updateWithCache(translate);
        }
        return updated;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SysMenu entity) {
        // 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除菜单权限信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @CacheEvict(cacheNames = "SysMenu", keyGenerator = "deleteKeyGenerator" )
    public Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid) {
        if (isValid) {
            // 做一些业务上的校验,判断是否需要校验
        }
        boolean removed = this.removeByIds(Arrays.asList(ids));
        if (removed) {
            menuTranslateService.deleteWithCacheByIds(ids,false);
        }
        return removed;
    }
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/

    /**
     * 根据用户查询系统菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> selectMenuList(Long userId) {
        return selectMenuList(new SysMenu(), userId);
    }

    /**
     * 查询系统菜单列表
     *
     * @param menu 菜单信息
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> selectMenuList(SysMenu menu, Long userId) {
        menu.setLanguage(SecurityUtils.getLanguage());
        List<SysMenu> menuList = null;
        // 管理员显示所有菜单信息
        if (SysUser.isAdmin(userId)) {
            menuList = menuMapper.selectMenuList(menu);
        } else {
            menu.getParams().put("userId", userId);
            menuList = menuMapper.selectMenuListByUserId(menu);
        }
        return menuList;
    }

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    @Override
    public Set<String> selectMenuPermsByUserId(Long userId) {
        List<String> perms = menuMapper.selectMenuPermsByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (String perm : perms) {
            if (StringUtils.isNotEmpty(perm)) {
                permsSet.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * 根据角色ID查询权限
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    @Override
    public Set<String> selectMenuPermsByRoleId(Long roleId) {
        List<String> perms = menuMapper.selectMenuPermsByRoleId(roleId);
        Set<String> permsSet = new HashSet<>();
        for (String perm : perms) {
            if (StringUtils.isNotEmpty(perm)) {
                permsSet.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * 根据用户ID查询菜单
     *
     * @param userId   用户名称
     * @param language 语言
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> selectMenuTreeByUserId(Long userId, String language) {
        List<SysMenu> menus = null;
        if (SecurityUtils.isAdmin(userId)) {
            menus = menuMapper.selectMenuTreeAll(language);
        } else {
            menus = menuMapper.selectMenuTreeByUserId(userId, language);
        }
        return getChildPerms(menus, 0);
    }

    /**
     * 根据角色ID查询菜单树信息
     *
     * @param roleId 角色ID
     * @return 选中菜单列表
     */
    @Override
    public List<Long> selectMenuListByRoleId(Long roleId) {
        SysRole role = roleMapper.selectRoleById(roleId);
        return menuMapper.selectMenuListByRoleId(roleId, role.getMenuCheckStrictly());
    }

    /**
     * 构建前端路由所需要的菜单
     *
     * @param menus 菜单列表
     * @return 路由列表
     */
    @Override
    public List<RouterVo> buildMenus(List<SysMenu> menus) {
        List<RouterVo> routers = new LinkedList<RouterVo>();
        for (SysMenu menu : menus) {
            RouterVo router = new RouterVo();
            router.setHidden("1".equals(menu.getVisible()));
            router.setName(getRouteName(menu));
            router.setPath(getRouterPath(menu));
            router.setComponent(getComponent(menu));
            router.setQuery(menu.getQueryParam());
            router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), 1 == menu.getIsCache(), menu.getPath(), menu.getTarget()));
            List<SysMenu> cMenus = menu.getChildren();
            if (!cMenus.isEmpty() && cMenus.size() > 0 && UserConstants.TYPE_DIR.equals(menu.getMenuType())) {
                router.setAlwaysShow(true);
                router.setRedirect("noRedirect");
                router.setChildren(buildMenus(cMenus));
            } else if (isMenuFrame(menu)) {
                router.setMeta(null);
                List<RouterVo> childrenList = new ArrayList<RouterVo>();
                RouterVo children = new RouterVo();
                children.setPath(menu.getPath());
                children.setComponent(menu.getComponent());
                children.setName(StringUtils.capitalize(menu.getPath()));
                children.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), 1 == menu.getIsCache(), menu.getPath(), menu.getTarget()));
                children.setQuery(menu.getQueryParam());
                childrenList.add(children);
                router.setChildren(childrenList);
            } else if (menu.getParentId().intValue() == 0 && isInnerLink(menu)) {
                router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon()));
                router.setPath("/");
                List<RouterVo> childrenList = new ArrayList<RouterVo>();
                RouterVo children = new RouterVo();
                String routerPath = innerLinkReplaceEach(menu.getPath());
                children.setPath(routerPath);
                children.setComponent(UserConstants.INNER_LINK);
                children.setName(StringUtils.capitalize(routerPath));
                children.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), menu.getPath()));
                childrenList.add(children);
                router.setChildren(childrenList);
            }
            routers.add(router);
        }
        return routers;
    }

    /**
     * 构建前端所需要树结构
     *
     * @param menus 菜单列表
     * @return 树结构列表
     */
    @Override
    public List<SysMenu> buildMenuTree(List<SysMenu> menus) {
        List<SysMenu> returnList = new ArrayList<SysMenu>();
        List<Long> tempList = menus.stream().map(SysMenu::getMenuId).collect(Collectors.toList());
        for (Iterator<SysMenu> iterator = menus.iterator(); iterator.hasNext(); ) {
            SysMenu menu = (SysMenu) iterator.next();
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(menu.getParentId())) {
                recursionFn(menus, menu);
                returnList.add(menu);
            }
        }
        if (returnList.isEmpty()) {
            returnList = menus;
        }
        return returnList;
    }

    /**
     * 构建前端所需要下拉树结构
     *
     * @param menus 菜单列表
     * @return 下拉树结构列表
     */
    @Override
    public List<TreeSelect> buildMenuTreeSelect(List<SysMenu> menus) {
        List<SysMenu> menuTrees = buildMenuTree(menus);
        return menuTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    /**
     * 根据菜单ID查询信息
     *
     * @param menuId 菜单ID
     * @return 菜单信息
     */
    @Override
    public SysMenu selectMenuById(Long menuId) {
        return menuMapper.selectMenuById(menuId, SecurityUtils.getLanguage());
    }

    /**
     * 是否存在菜单子节点
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    @Override
    public boolean hasChildByMenuId(Long menuId) {
        int result = menuMapper.hasChildByMenuId(menuId);
        return result > 0;
    }

    /**
     * 查询菜单使用数量
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    @Override
    public boolean checkMenuExistRole(Long menuId) {
        int result = roleMenuMapper.checkMenuExistRole(menuId);
        return result > 0;
    }

    /**
     * 新增保存菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    @Override
    public int insertMenu(SysMenu menu) {
        int i = menuMapper.insertMenu(menu);
        SysMenuTranslate translate = new SysMenuTranslate();
        if (i > 0) {
            translate.setId(menu.getMenuId());
            translate.setZhCn(menu.getMenuName());
            translate.setEnUs(menu.getMenuName_en_US());
            menuTranslateService.insertWithCache(translate);
        }
        return i;
    }

    /**
     * 修改保存菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    @Override
    public int updateMenu(SysMenu menu) {
        int i = menuMapper.updateMenu(menu);
        SysMenuTranslate translate = new SysMenuTranslate();
        if (i > 0) {
            translate.setId(menu.getMenuId());
            translate.setZhCn(menu.getMenuName());
            translate.setEnUs(menu.getMenuName_en_US());
            menuTranslateService.updateWithCache(translate);
        }
        return i;
    }

    /**
     * 删除菜单管理信息
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    @Override
    public int deleteMenuById(Long menuId) {
        int i = menuMapper.deleteMenuById(menuId);
        if (i > 0) {
            menuTranslateService.deleteWithCacheByIds(new Long[]{menuId}, false);
        }
        return i;
    }

    /**
     * 校验菜单名称是否唯一
     *
     * @param menu 菜单信息
     * @return 结果
     */
    @Override
    public String checkMenuNameUnique(SysMenu menu) {
        Long menuId = StringUtils.isNull(menu.getMenuId()) ? -1L : menu.getMenuId();
        // 仅匹配菜单表存储的菜单名称，不匹配菜单翻译表存储的对应语言名称
        LambdaQueryWrapper<SysMenu> lqw = Wrappers.lambdaQuery();
        lqw.eq(SysMenu::getMenuName, menu.getMenuName());
        lqw.eq(SysMenu::getParentId, menu.getParentId());
        SysMenu info = menuMapper.selectOne(lqw);
        if (StringUtils.isNotNull(info) && info.getMenuId().longValue() != menuId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Override
    public List<SysMenu> deptMenuTreeselect(Long deptId) {
        SysDept sysDept = sysDeptMapper.selectDeptById(deptId);
        if (ObjectUtil.isNull(sysDept) && null == sysDept.getDeptUserId()) {
            return new ArrayList<>();
        }
        return this.selectMenuList(sysDept.getDeptUserId());
    }

    @Override
    public List<SysMenu> deptRoleMenuTreeselect(Long deptId, Long roleId) {
        LoginUser loginUser = getLoginUser();
        Long userDeptId = loginUser.getDeptId();
        Long deptUserId = loginUser.getUser().getDept().getDeptUserId();
        SysRole sysRole = roleMapper.selectRoleById(roleId);
//        List<SysUserRole> sysUserRoleList = sysUserRoleMapper.selectRoleByUserId(sysDept.getDeptUserId());
//        SysUserRole sysUserRole = sysUserRoleList.stream().filter(s -> roleId.equals(s.getRoleId())).findAny().orElse(null);
//        if (ObjectUtil.isNotNull(sysUserRole)) {
//            return menuMapper.selectMenuList(new SysMenu());
//        }
        if (!deptId.equals(userDeptId) && "manager".equals(sysRole.getRoleKey())) {
            return this.selectMenuList(deptUserId);
        }
        SysDept sysDept = sysDeptMapper.selectDeptById(deptId);
        if (ObjectUtil.isNull(sysDept) && null == sysDept.getDeptUserId()) {
            return new ArrayList<>();
        }
        return this.selectMenuList(new SysMenu(), sysDept.getDeptUserId());
    }

    /**
     * 获取路由名称
     *
     * @param menu 菜单信息
     * @return 路由名称
     */
    public String getRouteName(SysMenu menu) {
        String routerName = StringUtils.capitalize(menu.getPath());
        // 非外链并且是一级目录（类型为目录）
        if (isMenuFrame(menu)) {
            routerName = StringUtils.EMPTY;
        }
        return routerName;
    }

    /**
     * 获取路由地址
     *
     * @param menu 菜单信息
     * @return 路由地址
     */
    public String getRouterPath(SysMenu menu) {
        String routerPath = menu.getPath();
        // 内链打开外网方式
        if (menu.getParentId().intValue() != 0 && isInnerLink(menu)) {
            routerPath = innerLinkReplaceEach(routerPath);
        }
        // 非外链并且是一级目录（类型为目录）
        if (0 == menu.getParentId().intValue() && UserConstants.TYPE_DIR.equals(menu.getMenuType())
                && UserConstants.NO_FRAME.equals(menu.getIsFrame().toString())) {
            routerPath = "/" + menu.getPath();
        }
        // 非外链并且是一级目录（类型为菜单）
        else if (isMenuFrame(menu)) {
            routerPath = "/";
        }
        return routerPath;
    }

    /**
     * 获取组件信息
     *
     * @param menu 菜单信息
     * @return 组件信息
     */
    public String getComponent(SysMenu menu) {
        String component = UserConstants.LAYOUT;
        if (StringUtils.isNotEmpty(menu.getComponent()) && !isMenuFrame(menu)) {
            component = menu.getComponent();
        } else if (StringUtils.isEmpty(menu.getComponent()) && menu.getParentId().intValue() != 0 && isInnerLink(menu)) {
            component = UserConstants.INNER_LINK;
        } else if (StringUtils.isEmpty(menu.getComponent()) && isParentView(menu)) {
            component = UserConstants.PARENT_VIEW;
        }
        return component;
    }

    /**
     * 是否为菜单内部跳转
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean isMenuFrame(SysMenu menu) {
        return menu.getParentId().intValue() == 0 && UserConstants.TYPE_MENU.equals(menu.getMenuType())
                && menu.getIsFrame().toString().equals(UserConstants.NO_FRAME);
    }

    /**
     * 是否为内链组件
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean isInnerLink(SysMenu menu) {
        return menu.getIsFrame().toString().equals(UserConstants.NO_FRAME) && StringUtils.ishttp(menu.getPath());
    }

    /**
     * 是否为parent_view组件
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean isParentView(SysMenu menu) {
        return menu.getParentId().intValue() != 0 && UserConstants.TYPE_DIR.equals(menu.getMenuType());
    }

    /**
     * 根据父节点的ID获取所有子节点
     *
     * @param list     分类表
     * @param parentId 传入的父节点ID
     * @return String
     */
    public List<SysMenu> getChildPerms(List<SysMenu> list, int parentId) {
        List<SysMenu> returnList = new ArrayList<SysMenu>();
        for (Iterator<SysMenu> iterator = list.iterator(); iterator.hasNext(); ) {
            SysMenu t = (SysMenu) iterator.next();
            // 一、根据传入的某个父节点ID,遍历该父节点的所有子节点
            if (t.getParentId() == parentId) {
                recursionFn(list, t);
                returnList.add(t);
            }
        }
        return returnList;
    }

    /**
     * 递归列表
     *
     * @param list 分类表
     * @param t    子节点
     */
    private void recursionFn(List<SysMenu> list, SysMenu t) {
        // 得到子节点列表
        List<SysMenu> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysMenu tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysMenu> getChildList(List<SysMenu> list, SysMenu t) {
        List<SysMenu> tlist = new ArrayList<SysMenu>();
        Iterator<SysMenu> it = list.iterator();
        while (it.hasNext()) {
            SysMenu n = (SysMenu) it.next();
            if (n.getParentId().longValue() == t.getMenuId().longValue()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysMenu> list, SysMenu t) {
        return getChildList(list, t).size() > 0;
    }

    /**
     * 内链域名特殊字符替换
     *
     * @return 替换后的内链域名
     */
    public String innerLinkReplaceEach(String path) {
        return StringUtils.replaceEach(path, new String[]{Constants.HTTP, Constants.HTTPS, Constants.WWW, "."},
                new String[]{"", "", "", "/"});
    }
    /** 自定义代码区域 END**/
}
