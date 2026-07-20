package com.fastbee.onvif.callback;

import lombok.Data;

@Data
public class RequestMessage {

	private String id;

	private String key;

	private Object data;
}
