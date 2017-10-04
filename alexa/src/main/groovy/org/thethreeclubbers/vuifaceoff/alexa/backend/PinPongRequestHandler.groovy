package org.thethreeclubbers.vuifaceoff.alexa.backend;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import groovy.transform.CompileStatic;

@CompileStatic
public class PinPongRequestHandler implements RequestHandler<String, String> {

  @Override
  public String handleRequest(String input, Context context) {
    context.getLogger().log("Input: " + input);

    return "Sup? " + input + "!";
  }

}
