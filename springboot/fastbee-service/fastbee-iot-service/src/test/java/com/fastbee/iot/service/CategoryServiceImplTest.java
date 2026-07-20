package com.fastbee.iot.service;

import static com.fastbee.framework.core.util.RandomUtils.randomLongId;
import static com.fastbee.framework.core.util.RandomUtils.randomString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.test.util.ReflectionTestUtils;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.extend.core.domin.entity.SysDept;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.core.domin.model.LoginUser;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.domain.Category;
import com.fastbee.iot.mapper.CategoryMapper;
import com.fastbee.iot.model.IdAndName;
import com.fastbee.iot.model.vo.CategoryVO;
import com.fastbee.iot.service.impl.CategoryServiceImpl;

/**
 * {@link CategoryServiceImpl} 单元测试
 *
 * 测试范围：不依赖 SecurityUtils 静态上下文的方法，使用纯 Mockito 进行测试。
 * 对于 insertCategory 等依赖 SecurityUtils 的方法，建议通过集成测试（BaseDbUnitTest）覆盖。
 *
 * @author fastbee
 */
@DisplayName("产品分类 Service 单元测试")
public class CategoryServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryMapper categoryMapper;

    private MockedStatic<MessageUtils> messageUtilsMock;
    private MockedStatic<SecurityUtils> securityUtilsMock;

    @BeforeEach
    void setUp() {
        // 初始化 MyBatis-Plus Lambda 缓存，解决纯 Mockito 测试中 LambdaWrapper 报错
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), Category.class);
        // 将 categoryMapper 注入到 ServiceImpl 的 baseMapper 字段，确保 baseMapper 与 @Mock 同一实例
        ReflectionTestUtils.setField(categoryService, "baseMapper", categoryMapper);
        // Mock MessageUtils.message() 静态方法，避免 SpringUtils 未初始化导致 NPE
        messageUtilsMock = mockStatic(MessageUtils.class);
        messageUtilsMock.when(() -> MessageUtils.message(anyString())).thenReturn("mocked message");
    }

    @AfterEach
    void tearDown() {
        if (messageUtilsMock != null) {
            messageUtilsMock.close();
        }
        if (securityUtilsMock != null) {
            securityUtilsMock.close();
        }
    }

    // ======================== selectCategoryByCategoryId ========================

    @Test
    @DisplayName("selectCategoryByCategoryId - 分类存在时，应返回对应分类")
    void testSelectCategoryByCategoryId_Exists_ShouldReturnCategory() {
        // given
        Category query = new Category();
        query.setCategoryId(randomLongId());

        Category category = new Category();
        category.setCategoryId(query.getCategoryId());
        category.setCategoryName(randomString());
        when(categoryMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(category);

        // when
        Category result = categoryService.selectCategoryByCategoryId(query);

        // then
        assertNotNull(result);
        assertEquals(query.getCategoryId(), result.getCategoryId());
        assertEquals(category.getCategoryName(), result.getCategoryName());
        verify(categoryMapper).selectOne(any(LambdaQueryWrapper.class));
    }

    // ======================== updateCategory ========================

    @Test
    @DisplayName("updateCategory - 正常更新分类，应返回 1 并设置更新时间")
    void testUpdateCategory_Normal_ShouldReturnOneAndSetUpdateTime() {
        // given
        Category category = new Category();
        category.setCategoryId(randomLongId());
        category.setCategoryName(randomString());
        when(categoryMapper.updateById(any(Category.class))).thenReturn(1);

        // when
        int result = categoryService.updateCategory(category);

        // then
        assertEquals(1, result);
        assertNotNull(category.getUpdateTime());
        verify(categoryMapper).updateById(category);
    }

    @Test
    @DisplayName("updateCategory - 更新失败时，应返回 0")
    void testUpdateCategory_Fail_ShouldReturnZero() {
        // given
        Category category = new Category();
        category.setCategoryId(randomLongId());
        when(categoryMapper.updateById(any(Category.class))).thenReturn(0);

        // when
        int result = categoryService.updateCategory(category);

        // then
        assertEquals(0, result);
        verify(categoryMapper).updateById(category);
    }

    // ======================== deleteCategoryByCategoryIds ========================

    @Test
    @DisplayName("deleteCategoryByCategoryIds - 分类下有产品，应返回错误且不执行删除")
    void testDeleteCategoryByCategoryIds_HasProducts_ShouldReturnError() {
        // given
        Long[] categoryIds = {randomLongId(), randomLongId()};
        when(categoryMapper.productCountInCategorys(categoryIds)).thenReturn(2);

        // when
        AjaxResult result = categoryService.deleteCategoryByCategoryIds(categoryIds);

        // then
        assertNotEquals(200, result.get(AjaxResult.CODE_TAG));
        verify(categoryMapper, never()).deleteBatchIds(anyList());
    }

    @Test
    @DisplayName("deleteCategoryByCategoryIds - 分类下无产品且删除成功，应返回 200")
    void testDeleteCategoryByCategoryIds_NoProducts_DeleteSuccess_ShouldReturnSuccess() {
        // given
        Long[] categoryIds = {randomLongId()};
        when(categoryMapper.productCountInCategorys(categoryIds)).thenReturn(0);
        when(categoryMapper.deleteBatchIds(anyList())).thenReturn(1);

        // when
        AjaxResult result = categoryService.deleteCategoryByCategoryIds(categoryIds);

        // then
        assertEquals(200, result.get(AjaxResult.CODE_TAG));
        verify(categoryMapper).deleteBatchIds(Arrays.asList(categoryIds));
    }

    @Test
    @DisplayName("deleteCategoryByCategoryIds - 分类下无产品但删除失败，应返回错误")
    void testDeleteCategoryByCategoryIds_NoProducts_DeleteFail_ShouldReturnError() {
        // given
        Long[] categoryIds = {randomLongId()};
        when(categoryMapper.productCountInCategorys(categoryIds)).thenReturn(0);
        when(categoryMapper.deleteBatchIds(anyList())).thenReturn(0);

        // when
        AjaxResult result = categoryService.deleteCategoryByCategoryIds(categoryIds);

        // then
        assertNotEquals(200, result.get(AjaxResult.CODE_TAG));
        verify(categoryMapper).deleteBatchIds(Arrays.asList(categoryIds));
    }

    // ======================== selectCategoryShortList ========================

    @Test
    @DisplayName("selectCategoryShortList - 无数据时应返回空分页")
    void testSelectCategoryShortList_NoData_ShouldReturnEmptyPage() {
        // given
        CategoryVO categoryVO = new CategoryVO();
        categoryVO.setPageNum(1);
        categoryVO.setPageSize(10);

        Page<Category> emptyPage = new Page<>();
        emptyPage.setTotal(0);
        when(categoryMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(emptyPage);

        // when
        Page<IdAndName> result = categoryService.selectCategoryShortList(categoryVO);

        // then
        assertNotNull(result);
        assertEquals(0, result.getTotal());
        assertTrue(result.getRecords().isEmpty());
    }

    @Test
    @DisplayName("selectCategoryShortList - 有数据时应返回对应 IdAndName 分页")
    void testSelectCategoryShortList_WithData_ShouldReturnPage() {
        // given
        CategoryVO categoryVO = new CategoryVO();
        categoryVO.setPageNum(1);
        categoryVO.setPageSize(10);

        Long categoryId = randomLongId();
        String categoryName = randomString();
        Category category = new Category();
        category.setCategoryId(categoryId);
        category.setCategoryName(categoryName);

        Page<Category> page = new Page<>();
        page.setTotal(1);
        page.setRecords(List.of(category));
        when(categoryMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        // when
        Page<IdAndName> result = categoryService.selectCategoryShortList(categoryVO);

        // then
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
        assertEquals(categoryId, result.getRecords().get(0).getId());
        assertEquals(categoryName, result.getRecords().get(0).getName());
    }

    // ======================== pageCategoryVO ========================

    @Test
    @DisplayName("pageCategoryVO - 正常分页查询，应返回转换后的分页结果")
    void testPageCategoryVO_Normal_ShouldReturnConvertedPage() {
        // given
        CategoryVO categoryVO = new CategoryVO();
        categoryVO.setPageNum(1);
        categoryVO.setPageSize(10);

        Category category = new Category();
        category.setCategoryId(randomLongId());
        category.setCategoryName(randomString());

        Page<Category> categoryPage = new Page<>();
        categoryPage.setTotal(1);
        categoryPage.setRecords(List.of(category));
        when(categoryMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(categoryPage);

        // when
        Page<CategoryVO> result = categoryService.pageCategoryVO(categoryVO);

        // then
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
        assertEquals(category.getCategoryId(), result.getRecords().get(0).getCategoryId());
        assertEquals(category.getCategoryName(), result.getRecords().get(0).getCategoryName());
    }

    @Test
    @DisplayName("pageCategoryVO - 无数据时，应返回空分页")
    void testPageCategoryVO_NoData_ShouldReturnEmptyPage() {
        // given
        CategoryVO categoryVO = new CategoryVO();
        categoryVO.setPageNum(1);
        categoryVO.setPageSize(10);

        Page<Category> emptyPage = new Page<>();
        emptyPage.setTotal(0);
        when(categoryMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(emptyPage);

        // when
        Page<CategoryVO> result = categoryService.pageCategoryVO(categoryVO);

        // then
        assertNotNull(result);
        assertEquals(0, result.getTotal());
        assertTrue(result.getRecords().isEmpty());
    }

    // ======================== listCategoryVO ========================

    @Test
    @DisplayName("listCategoryVO - 有数据时，应返回转换后的分类列表")
    void testListCategoryVO_WithData_ShouldReturnConvertedList() {
        // given
        Category query = new Category();
        query.setCategoryName("分类");

        Category category = new Category();
        category.setCategoryId(randomLongId());
        category.setCategoryName("分类A");

        when(categoryMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(category));

        // when
        List<CategoryVO> result = categoryService.listCategoryVO(query);

        // then
        assertEquals(1, result.size());
        assertEquals(category.getCategoryId(), result.get(0).getCategoryId());
        assertEquals(category.getCategoryName(), result.get(0).getCategoryName());
    }

    // ======================== insertCategory ========================

    @Test
    @DisplayName("insertCategory - 非管理员创建分类时，应设置租户与创建信息")
    void testInsertCategory_NotAdmin_ShouldFillTenantAndCreator() {
        // given
        Category category = new Category();
        category.setCategoryName(randomString());

        SysDept dept = new SysDept();
        dept.setDeptUserId(randomLongId());
        dept.setDeptName("租户机构");

        SysUser user = new SysUser();
        user.setUserId(2L);
        user.setUserName("tester");
        user.setDeptId(100L);
        user.setDept(dept);

        LoginUser loginUser = new LoginUser();
        loginUser.setUser(user);

        SecurityUtilsTestHelper.prepareSecurityUtils();
        securityUtilsMock = mockStatic(SecurityUtils.class);
        securityUtilsMock.when(SecurityUtils::getLoginUser).thenReturn(loginUser);
        securityUtilsMock.when(() -> SecurityUtils.isAdmin(2L)).thenReturn(false);
        when(categoryMapper.insert(any(Category.class))).thenReturn(1);

        // when
        int result = categoryService.insertCategory(category);

        // then
        assertEquals(1, result);
        assertEquals(0, category.getIsSys());
        assertEquals(dept.getDeptUserId(), category.getTenantId());
        assertEquals(dept.getDeptName(), category.getTenantName());
        assertEquals(user.getUserName(), category.getCreateBy());
        assertNotNull(category.getCreateTime());
        verify(categoryMapper).insert(category);
    }

    @Test
    @DisplayName("insertCategory - 管理员且无机构时，应回落使用用户信息作为租户")
    void testInsertCategory_AdminWithoutDept_ShouldUseUserInfo() {
        // given
        Category category = new Category();

        SysUser user = new SysUser();
        user.setUserId(1L);
        user.setUserName("admin");

        LoginUser loginUser = new LoginUser();
        loginUser.setUser(user);

        SecurityUtilsTestHelper.prepareSecurityUtils();
        securityUtilsMock = mockStatic(SecurityUtils.class);
        securityUtilsMock.when(SecurityUtils::getLoginUser).thenReturn(loginUser);
        securityUtilsMock.when(() -> SecurityUtils.isAdmin(1L)).thenReturn(true);
        when(categoryMapper.insert(any(Category.class))).thenReturn(1);

        // when
        int result = categoryService.insertCategory(category);

        // then
        assertEquals(1, result);
        assertEquals(1, category.getIsSys());
        assertEquals(user.getUserId(), category.getTenantId());
        assertEquals(user.getUserName(), category.getTenantName());
        assertEquals(user.getUserName(), category.getCreateBy());
        verify(categoryMapper).insert(category);
    }
}
