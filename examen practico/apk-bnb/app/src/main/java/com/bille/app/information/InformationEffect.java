package com.bille.app.information;

import com.bille.app.common.UserMessage;

public sealed interface InformationEffect
        permits InformationEffect.ShowLocationPrompt,
        InformationEffect.OpenAuthentication,
        InformationEffect.ShowMessage {

    record ShowLocationPrompt() implements InformationEffect {
    }

    record OpenAuthentication(String receiptId) implements InformationEffect {
    }

    record ShowMessage(UserMessage message) implements InformationEffect {
    }
}
