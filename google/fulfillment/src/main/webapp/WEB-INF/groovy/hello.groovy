import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
//import static groovyx.net.http.HttpBuilder.configure

import java.util.logging.Logger

def slurper = new JsonSlurper()

def str = request.reader.text

Logger.getLogger('hello').severe(str as String)

//def http = configure {
//    request.uri = 'https://vui-faceoff.atlassian.net'
//    request.auth.basic 'jbaruch@sadogursky.com', 'olUfgt3ReDXthhZvz58j85CD'
//    request.headers['Authorization'] = "Basic ${"jbaruch@sadogursky.com:olUfgt3ReDXthhZvz58j85CD".bytes.encodeBase64().toString()}" as String
//}

def text = slurper.parseText(str)
def action = text.result.action

def responseText = 'I didn\'t get this one'
def ctxOut = []
def defect
def defects = ['WTF-18', 'WTF-28', 'WTF-78']
switch (action) {
    case 'STANDUP_STATUS':
//        result = http.get {
//            request.uri.path = '/rest/api/2/search'
//            request.uri.query = [jql: 'status in ("In Progress", "To Do", "In Review")']
//        }
        def result = [total:24]
        responseText = "There are ${result.total} open critical issues."
        break
    case 'TOP_USER_DEFECTS':
        def name = text.result.parameters.username
        responseText = "The top 3 defects for $name are: ${defects.join(",")}"
        ctxOut << [name: 'current-user-id', parameters: [username: name]]
        break
    case 'MORE_USER_DEFECTS':
        def name = text.result.parameters.username
        responseText = "There are 100 more defects for $name! What a shame!"
        break
    case 'DEFECT_SEVERITY':
        defect = text.result.parameters.defect_id
        responseText = "Oh, no! Defect $defect is a catastrophic one! Someone needs to do something!"
        ctxOut << [name: 'current-defect-id', parameters: [result_defect_id: defect]]
        break
    case 'BLOCK_DEFECT':
        defect = text.result.parameters.defect_id
        responseText = "Defect $defect is a blocker now, go fix it!"
        break
}

def json = new JsonBuilder()

json {
    speech responseText
    displayText responseText
    if (ctxOut) contextOut ctxOut
}

println json