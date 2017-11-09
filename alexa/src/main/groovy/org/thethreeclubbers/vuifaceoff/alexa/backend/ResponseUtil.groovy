package org.thethreeclubbers.vuifaceoff.alexa.backend

import com.amazon.speech.speechlet.SpeechletResponse
import com.amazon.speech.ui.PlainTextOutputSpeech
import com.amazon.speech.ui.Reprompt
import com.amazon.speech.ui.SimpleCard
import com.amazon.speech.ui.SsmlOutputSpeech;

class ResponseUtil {

    static SpeechletResponse newTellResponse(String outSpeechText) {
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech()
        speech.text = outSpeechText
        SpeechletResponse.newTellResponse(speech)
    }

    static SpeechletResponse getSpeechletResponse(String speechText, String repromptText,
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

    static SpeechletResponse newAskResponse(String outputSpeechText, String repromptText) {
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech()
        speech.text = outputSpeechText
        PlainTextOutputSpeech repromptOutputSpeech = new PlainTextOutputSpeech()
        repromptOutputSpeech.text = repromptText
        Reprompt reprompt = new Reprompt()
        reprompt.outputSpeech = repromptOutputSpeech
        SpeechletResponse.newAskResponse(speech, reprompt)
    }

    static SpeechletResponse newAskSsmlResponse(String outputSpeechText, String repromptText) {
        def speech = new SsmlOutputSpeech()
        speech.ssml = "<speak>$outputSpeechText</speak>"
        def repromptOutputSpeech = new SsmlOutputSpeech()
        repromptOutputSpeech.ssml = "<speak>$repromptText</speak>"
        Reprompt reprompt = new Reprompt()
        reprompt.outputSpeech = repromptOutputSpeech
        SpeechletResponse.newAskResponse(speech, reprompt)
    }
}
