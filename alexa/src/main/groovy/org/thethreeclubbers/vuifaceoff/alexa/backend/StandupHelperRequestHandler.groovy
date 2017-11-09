package org.thethreeclubbers.vuifaceoff.alexa.backend

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler
import groovy.transform.InheritConstructors

@InheritConstructors
class StandupHelperRequestHandler extends SpeechletRequestStreamHandler {

    StandupHelperRequestHandler() {
        super(new StandupHelperSpeechlet(), [] as Set<String>)
    }
}
