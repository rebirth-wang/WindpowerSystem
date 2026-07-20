package com.fastbee.jt808.codec;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fastbee.protocol.WModelManager;

@Configuration
public class JT808Config {

    @Bean
    public MultiPacketListener multiPacketListener() {
        return new MultiPacketListener(20); // 超时时间20秒
    }

    @Bean
    public JT808Encoder jt808Encoder(WModelManager wModelManager) {
        return new JT808Encoder(wModelManager);
    }

    @Bean
    public JT808Decoder jt808Decoder(WModelManager wModelManager, MultiPacketListener multiPacketListener) {
        return new MultiPacketDecoder(wModelManager, multiPacketListener);
    }

}
