package com.toonapps.toon.controller;

import com.toonapps.toon.entity.UsageInfo;

public interface IGasAndElecFlowListener {

    void onUsageChanged(UsageInfo usageInfo);
    void onUsageError(Exception e);
}