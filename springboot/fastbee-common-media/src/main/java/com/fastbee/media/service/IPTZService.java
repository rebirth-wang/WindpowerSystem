package com.fastbee.media.service;

import java.util.List;

import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.model.*;

/**
 * 资源能力接入-云台控制
 */
public interface IPTZService {

    Boolean ptz(CommonChannel channel, PtzInput imput);

    Boolean frontEndCommand(CommonChannel channel, Integer cmdCode, Integer parameter1, Integer parameter2, Integer combindCode);

    Boolean ptz(CommonChannel channel, FrontEndControlCodeForPTZ frontEndControlCode);

    Boolean preset(CommonChannel channel, FrontEndControlCodeForPreset frontEndControlCode);

    Boolean fi(CommonChannel channel, FrontEndControlCodeForFI frontEndControlCode);

    Boolean tour(CommonChannel channel, FrontEndControlCodeForTour frontEndControlCode);

    Boolean scan(CommonChannel channel, FrontEndControlCodeForScan frontEndControlCode);

    Boolean auxiliary(CommonChannel channel, FrontEndControlCodeForAuxiliary frontEndControlCode);

    Boolean wiper(CommonChannel channel, FrontEndControlCodeForWiper frontEndControlCode);

    List<Preset> queryPresetList(CommonChannel channel);
}
