package com.fastbee.iot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * id和name
 *
 * @author kerwincui
 * @date 2021-12-16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdAndName
{
    private Long id;

    private String name;

}
