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
import com.amazon.speech.ui.SimpleCard
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@CompileStatic
@Slf4j
class StandupHelperSpeechlet implements Speechlet {

    void onSessionStarted(SessionStartedRequest request, Session session)
            throws SpeechletException {
        log.info "onSessionStarted requestId=$request.requestId, sessionId=$session.sessionId"

    }


    SpeechletResponse onLaunch(LaunchRequest request, Session session) throws SpeechletException {
        log.info "onLaunch requestId=$request.requestId, sessionId=$session.sessionId"
        newAskResponse('''
                       Hello to all JavaOne attendees! 
                       I'm standup helper. I can help you run your everyday 
                       standups more efficiently''',
                       "Oh captain my captain, waiting for your " +
                       "command")
    }


    SpeechletResponse onIntent(IntentRequest intentRequest, Session session)
            throws SpeechletException {

        log.info "onIntent requestId=$intentRequest.requestId, sessionId=$session.sessionId"

        def responseText = 'I didn\'t get this one'
        def ctxOut = []
        def defect = ''
        def defects = ['WTF-18', 'WTF-28', 'WTF-78']
        def numOfCriticalIssues = 42
        switch (intentRequest.intent.name) {
            case 'StandupStatus':
                return getSpeechletResponse("There are ${numOfCriticalIssues} critical issues",
                                            'anything else?', true)
            case 'TopUserDefects':
                def name = intentRequest.intent.getSlot("USER").value
                responseText = "The top 3 defects for $name are: ${defects.join(",")}"
                session.attributes['current-user'] = name
                // + ctxOut.join(",")
                return getSpeechletResponse(responseText, "do you want to know more?", true)
            case 'MoreUserDefects':
                def name = session.getAttribute('current-user')
                responseText = "There are 69 more defects for $name. What a shame!"
                return newTellResponse(responseText)
            case 'SeverityForDefect':
                defect = intentRequest.intent.slots['DEFECT'].value
                //defect = text.result.parameters.defect_id
                responseText = "oh no! Defect $defect is a catastrophic one! Someone needs to do " +
                               "something!"
                session.attributes['current-defect'] = defect
                //ctxOut << [name:'current-defect-id', parameters:[result_defect_id : defect]]
                return getSpeechletResponse(responseText, "anything else", true)
            case 'BlockCurrentDefect':
                //defect = intentRequest.intent.slots['DEFECT'].value
                //defect = text.result.parameters.defect_id
                defect = session.attributes['current-defect']
                responseText = "Defect $defect is a blocker now, go fix it!"
                return newAskResponse(responseText, "is there anything else?")
            case 'Goodbye':
                return newTellResponse("ba buy! nazdorov'ya!")
            default:
                return newTellResponse(responseText)
        }

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

    static SpeechletResponse newTellResponse(String outSpeechText) {
        OutputSpeech speech = new PlainTextOutputSpeech()
        speech.text = outSpeechText
        SpeechletResponse.newTellResponse(speech)
    }

    static private SpeechletResponse getSpeechletResponse(String speechText, String repromptText,
                                                          boolean isAskResponse) {
        // Create the Simple card content.
        def card = new SimpleCard()
        card.setTitle("Session")
        card.setContent(speechText)

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText)

        if (isAskResponse) {
            // Create reprompt
            PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech()
            repromptSpeech.setText(repromptText)
            Reprompt reprompt = new Reprompt()
            reprompt.setOutputSpeech(repromptSpeech)

            return SpeechletResponse.newAskResponse(speech, reprompt, card);

        } else {
            return SpeechletResponse.newTellResponse(speech, card);
        }
    }
}

