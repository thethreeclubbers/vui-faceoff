package org.thethreeclubbers.vuifaceoff.alexa.backend

import com.amazon.speech.slu.ConfirmationStatus
import com.amazon.speech.slu.Slot
import com.amazon.speech.speechlet.*
import com.amazon.speech.speechlet.dialog.directives.DelegateDirective
import com.amazon.speech.speechlet.dialog.directives.DialogIntent
import com.amazon.speech.speechlet.dialog.directives.DialogSlot
import com.amazon.speech.ui.PlainTextOutputSpeech
import com.amazon.speech.ui.Reprompt
import com.amazon.speech.ui.SimpleCard
import com.amazon.speech.ui.SsmlOutputSpeech
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j2
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.builder.ReflectionToStringBuilder

@CompileStatic
@Slf4j(loggingStrategy = Log4j2.Log4j2LoggingStrategy, category = 'StandupHelperSpeechlet')
class StandupHelperSpeechlet implements Speechlet {

    void onSessionStarted(SessionStartedRequest request, Session session)
            throws SpeechletException {
        log.info "onSessionStarted requestId=$request.requestId, sessionId=$session.sessionId"

    }


    SpeechletResponse onLaunch(LaunchRequest request, Session session) throws SpeechletException {
        log.info "onLaunch requestId=$request.requestId, sessionId=$session.sessionId"
        def helloResponses = [
                "Hello world, I am alexa your humble servant, Amazon Alexa will win this fight !!!",
                "Hello Joker people, I am your humble stand up helper ",
                "This is Sparta!!!! Oh Wait!!! This is Joker - I will win nevertheless",
                "Hola Amigo!!! I am ready to serve",
                "Ready to Serve!!!",
                "Live long and prosper Joker people, we are ready to battle"
        ]
        Collections.shuffle(helloResponses)
        newAskResponse(helloResponses.first() as String,
                "Oh captain my captain, waiting for your " +
                        "command")
    }


    SpeechletResponse onIntent(IntentRequest intentRequest, Session session)
            throws SpeechletException {


        println "onIntent requestId=$intentRequest.requestId, sessionId=$session.sessionId intentRquest=${ReflectionToStringBuilder.toString(intentRequest)} " +
                "session=${ReflectionToStringBuilder.toString(session)} intent=${ReflectionToStringBuilder.toString(intentRequest.intent)} "

        String responseText = 'I didn\'t get this one'
        switch (intentRequest.intent.name) {

            case 'StandupStatus':
                return getStandUpStatus()
            case 'TopUserDefects':
                return getTopUserDefects(intentRequest, session)
            case 'MoreUserDefects':
                return moreUserDefects(session)
            case 'SeverityForDefect':
                return getSeverityForDefect(intentRequest, session)
            case 'BlockCurrentDefect':
                return blockDefect(session)
            case 'Knockout':
                return executeKO(intentRequest)
            case 'Goodbye':
                return newTellResponse("ba buy! nazdorov'ya!")
            default:
                return newTellResponse(responseText)
        }

    }

    private SpeechletResponse getStandUpStatus() {
        def numOfCriticalIssues = 42
        return getSpeechletResponse(
                "There are ${numOfCriticalIssues} critical issues! anything else?",
                "Ready to Server my Captain",
                true)
    }

    private SpeechletResponse getTopUserDefects(IntentRequest intentRequest, Session session) {
        String responseText
        def name = intentRequest.intent.getSlot("USER").value
//      println "username is ${name}"
//      println "dialog state ${intentRequest.dialogState}"

        if (name == null) {
            return popuplateRequestFields(intentRequest)
        } else {
            def defects = []
            def random = new Random()
            3.times {
                defects << "WTF-${random.nextInt(100)}"
            }
            responseText = "The top 3 defects for $name are: ${defects.join(",")}"
            session.attributes['current-user'] = name
            return getSpeechletResponse(
                    responseText,
                    "do you want to know more?",
                    true)
        }
    }

    private SpeechletResponse moreUserDefects(Session session) {
        String responseText
        def name = session.getAttribute('current-user')
        def random = new Random()

        responseText = "There are ${random.nextInt(350)} more defects for $name. " +
                "What a shame ${name}! Shame, Shame, Shame !!!"

        return newTellResponse(responseText)
    }

    private SpeechletResponse getSeverityForDefect(IntentRequest intentRequest, Session session) {
        def defect
        defect = intentRequest.intent.slots['DEFECT'].value
        if (defect == null) {
            return popuplateRequestFields(intentRequest)
        } else {
            session.attributes['current-defect'] = defect
            return getSpeechletResponse(
                    "oh no! Defect $defect is a catastrophic one! Someone needs to do something!",
                    "anything else",
                    true)
        }
    }

    private SpeechletResponse blockDefect(Session session) {
        def defect
        defect = session.attributes['current-defect']
        return newAskResponse(
                "Defect $defect is a blocker now, go fix it!",
                "is there anything else?")
    }

    private SpeechletResponse executeKO(IntentRequest intentRequest) {
        if (intentRequest.intent.confirmationStatus == ConfirmationStatus.NONE) {
            def dialogIntent = new DialogIntent()
            dialogIntent.name = intentRequest.intent.name
            DelegateDirective dd = new DelegateDirective()
            dd.setUpdatedIntent(dialogIntent)
            SpeechletResponse speechletResp = new SpeechletResponse()
            speechletResp.directives = [dd] as List<Directive>
            speechletResp.shouldEndSession = false;
            return speechletResp
        } else {
            def result = new SsmlOutputSpeech()
            result.ssml = "<speak>Ok google <break time=\"1s\" />, how are you ?" +
                    "<break time=\"3s\" />Turn yourself off</speak>"
            return SpeechletResponse.newTellResponse(result)
        }
    }

    private SpeechletResponse popuplateRequestFields(IntentRequest intentRequest) {
        DialogIntent dialogIntent = new DialogIntent()
        dialogIntent.name = intentRequest.intent.name

        DelegateDirective dd = new DelegateDirective()
        dd.updatedIntent = dialogIntent


        Map<String, DialogSlot> dialogSlots = new HashMap<String, DialogSlot>()

        Iterator iter = intentRequest.intent.getSlots().entrySet().iterator()
        while (iter.hasNext()) {
            Map.Entry pair = (Map.Entry) iter.next()
            DialogSlot dialogSlot = new DialogSlot()
            Slot slot = (Slot) pair.value
            dialogSlot.name = slot.name
            dialogSlots.put((String) pair.key, dialogSlot)
            log.debug("DialogSlot " + (String) pair.key + " with Name " + slot.name + " added.")
        }
        dialogIntent.slots = dialogSlots

        SpeechletResponse speechletResp = new SpeechletResponse()
        speechletResp.directives = [dd] as List<Directive>
        speechletResp.shouldEndSession = false

        return speechletResp
    }

    void onSessionEnded(SessionEndedRequest request, Session session) throws SpeechletException {
        println "onSessionEnded requestId=${request.getRequestId()}, sessionId=${session.getSessionId()}"
    }

    static SpeechletResponse newAskResponse(String outputSpeechText, String repromptText) {
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech()
        speech.text = outputSpeechText
        PlainTextOutputSpeech repromptOutputSpeech = new PlainTextOutputSpeech()
        repromptOutputSpeech.text = repromptText
        Reprompt reprompt = new Reprompt()
        reprompt.outputSpeech = repromptOutputSpeech
        SpeechletResponse.newAskResponse(speech, reprompt)
    }

    static SpeechletResponse newTellResponse(String outSpeechText) {
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech()
        speech.text = outSpeechText
        SpeechletResponse.newTellResponse(speech)
    }

    static private SpeechletResponse getSpeechletResponse(String speechText, String repromptText,
                                                          boolean isAskResponse) {
        // Create the Simple card content.
        SimpleCard card = new SimpleCard()
        card.title = "Standup Helper"
        card.content = speechText

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech()
        speech.text = speechText

        if (isAskResponse) {
            // Create reprompt
            PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech()
            repromptSpeech.text = repromptText
            Reprompt reprompt = new Reprompt()
            reprompt.outputSpeech = repromptSpeech

            return SpeechletResponse.newAskResponse(speech, reprompt, card)

        } else {
            return SpeechletResponse.newTellResponse(speech, card)
        }
    }
}

