package org.thethreeclubbers.vuifaceoff.alexa.backend

import com.amazon.speech.speechlet.IntentRequest
import com.amazon.speech.speechlet.LaunchRequest
import com.amazon.speech.speechlet.Session
import com.amazon.speech.speechlet.SessionEndedRequest
import com.amazon.speech.speechlet.SessionStartedRequest
import com.amazon.speech.speechlet.Speechlet
import com.amazon.speech.speechlet.SpeechletException
import com.amazon.speech.speechlet.SpeechletResponse
import com.amazon.speech.ui.OutputSpeech
import com.amazon.speech.ui.PlainTextOutputSpeech
import com.amazon.speech.ui.Reprompt
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@CompileStatic
@Slf4j
class PingPongSpeechlet implements Speechlet {

    void onSessionStarted(SessionStartedRequest request, Session session)
            throws SpeechletException {
        log.info "onSessionStarted requestId=$request.requestId, sessionId=$session.sessionId"

    }


    SpeechletResponse onLaunch(LaunchRequest request, Session session) throws SpeechletException {
        log.info "onLaunch requestId=$request.requestId, sessionId=$session.sessionId"
        newAskResponse("Ping", "ping")
    }


    SpeechletResponse onIntent(IntentRequest intentRequest, Session session)
            throws SpeechletException {
        log.info "onIntent requestId=$intentRequest.requestId, sessionId=$session.sessionId"
    }


    void onSessionEnded(SessionEndedRequest request, Session session) throws SpeechletException {
        log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(),
                 session.getSessionId())
    }

    static SpeechletResponse newAskResponse(String outputSpeechText, String repromptText) {
        OutputSpeech speech = new PlainTextOutputSpeech()
        speech.text = outputSpeechText
        OutputSpeech repromptOutputSpeech = new PlainTextOutputSpeech()
        repromptOutputSpeech.text = repromptText
        Reprompt reprompt = new Reprompt()
        reprompt.outputSpeech = repromptOutputSpeech
        SpeechletResponse.newAskResponse(speech, reprompt)
    }
}
