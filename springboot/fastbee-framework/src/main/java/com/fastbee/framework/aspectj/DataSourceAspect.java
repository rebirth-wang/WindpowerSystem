//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.framework.aspectj;

import java.util.Objects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.fastbee.common.annotation.DataSource;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.framework.datasource.DynamicDataSourceContextHolder;

@Aspect
@Order(1)
@Component
public class DataSourceAspect {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Pointcut("@annotation(com.fastbee.common.annotation.DataSource)|| @within(com.fastbee.common.annotation.DataSource)")
    public void dsPointCut() {
    }

    @Around("dsPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        DataSource dataSource = this.getDataSource(point);
        if (StringUtils.isNotNull(dataSource)) {
            DynamicDataSourceContextHolder.setDataSourceType(dataSource.value().name());
        }

        Object var3;
        try {
            var3 = point.proceed();
        } finally {
            DynamicDataSourceContextHolder.clearDataSourceType();
        }

        return var3;
    }

    public DataSource getDataSource(ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature)point.getSignature();
        DataSource dataSource = (DataSource)AnnotationUtils.findAnnotation(signature.getMethod(), DataSource.class);
        return Objects.nonNull(dataSource) ? dataSource : (DataSource)AnnotationUtils.findAnnotation(signature.getDeclaringType(), DataSource.class);
    }
}
