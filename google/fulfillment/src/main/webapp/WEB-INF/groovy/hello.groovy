import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

import java.util.logging.Logger

def slurper = new JsonSlurper()

def str = request.reader.text

Logger.getLogger('hello').severe(str as String)

def text = slurper.parseText(str)
def action = text.result.action

def responseText = 'I didn\'t get this one'
def ctxOut = []
def defect = ''
def defects = ['WTF-18', 'WTF-28', 'WTF-78']
switch (action) {
    case 'STANDUP_STATUS' :
        responseText = 'The status is shit'
        break
    case 'TOP_USER_DEFECTS' :
        def name = text.result.parameters.username
        responseText = "The top 3 defects for $name are: ${defects.join(",")}"
        ctxOut << [name:'current-user-id', parameters:[username : name]]
        break
    case 'MORE_USER_DEFECTS' :
        def name = text.result.parameters.username
        responseText = "There are 100 more defects for $name"
        break
    case 'DEFECT_SEVERITY' :
        defect = text.result.parameters.defect_id
        responseText = "Defect $defect is a catastrophic one!"
        ctxOut << [name:'current-defect-id', parameters:[result_defect_id : defect]]
        break
    case 'BLOCK_DEFECT' :
        defect = text.result.parameters.defect_id
        responseText = "Defect $defect is a blocker now, go fix it!"
        break
}

def json = new JsonBuilder()

json {
    speech responseText
    displayText responseText
    if(ctxOut) contextOut ctxOut
}

println json