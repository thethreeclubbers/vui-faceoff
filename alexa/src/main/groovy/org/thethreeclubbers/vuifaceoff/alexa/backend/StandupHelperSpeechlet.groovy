package org.thethreeclubbers.vuifaceoff.alexa.backend

import com.amazon.speech.slu.ConfirmationStatus
import com.amazon.speech.slu.Slot
import com.amazon.speech.speechlet.*
import com.amazon.speech.speechlet.dialog.directives.DelegateDirective
import com.amazon.speech.speechlet.dialog.directives.DialogIntent
import com.amazon.speech.speechlet.dialog.directives.DialogSlot
import com.amazon.speech.ui.SsmlOutputSpeech
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j2
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.builder.ReflectionToStringBuilder

import static org.thethreeclubbers.vuifaceoff.alexa.backend.ResponseUtil.*

@CompileStatic
@Slf4j(loggingStrategy = Log4j2.Log4j2LoggingStrategy, category = 'StandupHelperSpeechlet')
class StandupHelperSpeechlet implements Speechlet {

    private String eventName = 'Devoxx'

    void onSessionStarted(SessionStartedRequest request, Session session)
            throws SpeechletException {
        log.info "onSessionStarted requestId=$request.requestId, sessionId=$session.sessionId"

    }


    SpeechletResponse onLaunch(LaunchRequest request, Session session) throws SpeechletException {
        log.info "onLaunch requestId=$request.requestId, sessionId=$session.sessionId"
        def helloResponses = [

                """<p><prosody volume="x-loud">Hello world!</prosody></p> <p>I am Alexa <break time="0.5s"/> your humble servant!</p> <p>Amazon Alexa will win  this fight  !!!</p>""",

                "Hello ${eventName} people, I am your humble stand up helper ",
                """
"<prosody volume="x-loud"> <emphasis level="strong">This is Sparta!!!! </emphasis> </prosody> <emphasis level="reduced">Oh Wait!!! </emphasis> This is ${
                    eventName
                } - I will win nevertheless
""",
                """
    <prosody pitch="x-high"><phoneme alphabet="ipa" ph="ola">Hola</phoneme> Amigo!!!</prosody> <break time="0.5s"/> I am ready to  serve""",
                "Ready to Serve!!!",
                "Live long and prosper ${eventName} people, we are ready to battle"
        ]
        Collections.shuffle(helloResponses)

        newAskSsmlResponse(helloResponses.first() as String,
                           "<p>Oh captain my captain,</p> <p>waiting for your command</p>")
    }


    SpeechletResponse onIntent(IntentRequest intentRequest, Session session)
            throws SpeechletException {

        log.info "onIntent requestId=$intentRequest.requestId, sessionId=$session.sessionId intentRquest=${ReflectionToStringBuilder.toString(intentRequest)} " +
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

        log.info "username is ${name}"
        log.info "dialog state ${intentRequest.dialogState}"

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
            speechletResp.nullableShouldEndSession = false
            return speechletResp
        } else {
            def result = new SsmlOutputSpeech()
            result.ssml = """
                            <speak>Ok google <break time="1s"/>, how are you ?
                            <break time="3s"/>  Turn yourself off</speak>       
                         """
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
        speechletResp.nullableShouldEndSession = false

        return speechletResp
    }

    void onSessionEnded(SessionEndedRequest request, Session session) throws SpeechletException {
        log.info "onSessionEnded requestId=${request.getRequestId()}, sessionId=${session.getSessionId()}"
    }


}

