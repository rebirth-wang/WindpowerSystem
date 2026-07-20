package com.fastbee.flowdev.codec;

import jakarta.annotation.Resource;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.fastbee.flowdev.model.FlowDev;
import com.fastbee.protocol.WModelManager;
import com.fastbee.protocol.base.model.RuntimeSchema;
import com.fastbee.protocol.util.ArrayMap;
import com.fastbee.protocol.util.ExplainUtils;

/**
 * @author gsb
 * @date 2023/5/17 16:45
 */
@Slf4j
@Component
@NoArgsConstructor
public class FlowDevEncoder {

    private static final ByteBufAllocator ALLOC = PooledByteBufAllocator.DEFAULT;

    @Resource
    private WModelManager modelManager;
    private ArrayMap<RuntimeSchema> headerSchemaMap;

    public FlowDevEncoder(String...basePackages){
        this.modelManager = new WModelManager(basePackages);
        this.headerSchemaMap = this.modelManager.getActiveMap(FlowDev.class);
    }

    public ByteBuf encode(FlowDev message, ExplainUtils explain){
        this.build();
        ByteBuf buf = ALLOC.buffer();
        RuntimeSchema runtimeSchema = headerSchemaMap.get(1);
        runtimeSchema.writeTo(buf,message,explain);
        return buf;
    }

    private void build() {
        if (this.headerSchemaMap == null) {
            this.headerSchemaMap = this.modelManager.getActiveMap(FlowDev.class);
        }
    }
}
