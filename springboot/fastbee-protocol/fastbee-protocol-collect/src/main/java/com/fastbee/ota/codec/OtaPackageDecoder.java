package com.fastbee.ota.codec;

import io.netty.buffer.ByteBuf;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fastbee.ota.model.OtaPackage;
import com.fastbee.protocol.WModelManager;
import com.fastbee.protocol.base.model.RuntimeSchema;
import com.fastbee.protocol.util.ArrayMap;

@Slf4j
@Component
@NoArgsConstructor
public class OtaPackageDecoder {

    @Autowired
    private WModelManager modelManager;
    private ArrayMap<RuntimeSchema> headerSchemaMap;

    public OtaPackageDecoder(String...basePackages) {
        this.modelManager = new WModelManager(basePackages);
        this.headerSchemaMap = this.modelManager.getActiveMap(OtaPackage.class);
    }

    public OtaPackage decode(ByteBuf in){
        this.build();
        ByteBuf copy = in.duplicate();
        byte[] bytes = new byte[in.writerIndex()];
        copy.readBytes(bytes);
        // 截掉校验位
        ByteBuf inSub = in.slice(0, in.readableBytes() - 1);
        RuntimeSchema<OtaPackage> runtimeSchema = headerSchemaMap.get(0);
        OtaPackage message = new OtaPackage();
        message.setPayload(inSub);
        runtimeSchema.mergeFrom(inSub,message,null);
        log.info("=>解析:[{}]",message);
        return message;
    }


    private void build(){
        if (this.headerSchemaMap == null) {
            this.headerSchemaMap = this.modelManager.getActiveMap(OtaPackage.class);
        }
    }

}
