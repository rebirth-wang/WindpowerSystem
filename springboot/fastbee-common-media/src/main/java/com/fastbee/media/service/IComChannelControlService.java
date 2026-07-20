package com.fastbee.media.service;

import java.util.List;

import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.model.*;

public interface IComChannelControlService {
    Boolean ptz(CommonChannel channel, FrontEndControlCodeForPTZ frontEndControlCode);
    Boolean fi(CommonChannel channel, FrontEndControlCodeForFI frontEndControlCode);
    Boolean preset(CommonChannel channel, FrontEndControlCodeForPreset frontEndControlCode);
    Boolean tour(CommonChannel channel, FrontEndControlCodeForTour frontEndControlCode);
    Boolean scan(CommonChannel channel, FrontEndControlCodeForScan frontEndControlCode);
    Boolean wiper(CommonChannel channel, FrontEndControlCodeForWiper controlCode);
    Boolean auxiliary(CommonChannel channel, FrontEndControlCodeForAuxiliary frontEndControlCode);
    List<Preset> queryPresetList(CommonChannel channel);
}
