import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

//region setup
def random = new Random()
def slurper = new JsonSlurper()
def str = request.reader.text
def text = slurper.parseText(str)
def action = text.result.action
def ctxOut = []
def defect
def defects = []
3.times {
    defects << "WTF-${random.nextInt(100)}"
}
def responseText = 'I didn\'t get this one'
def reprompt = [{ speech 'Hey, are you there?' }]
//endregion

switch (action) {
    //region STANDUP_STATUS
    case 'STANDUP_STATUS':
//        result = http.get {
//            request.uri.path = '/rest/api/2/search'
//            request.uri.query = [jql: 'status in ("In Progress", "To Do", "In Review")']
//        }
        def result = [total:random.nextInt(100)]
        responseText = "There are ${result.total} open critical issues."
        break
    //endregion
    //region TOP_USER_DEFECTS
    case 'TOP_USER_DEFECTS':
        def name = text.result.parameters.username
        responseText = "The top 3 defects for $name are: ${defects.join(",")}"
        ctxOut << [name: 'current-user-id', parameters: [username: name]]
        break
    //endregion
    //region MORE_USER_DEFECTS
    case 'MORE_USER_DEFECTS':
        def name = text.result.parameters.username
        responseText = "There are ${random.nextInt(100)} more defects for $name! Not your best day, $name, is it?"
        break
    //endregion
    //region DEFECT_SEVERITY
    case 'DEFECT_SEVERITY':
        defect = text.result.parameters.defect_id
        responseText = "Oh, no! Defect $defect is a catastrophic one! Someone needs to do something!"
        break
    //endregion
    //region BLOCK_DEFECT
    case 'BLOCK_DEFECT':
        defect = text.result.parameters.defect_id
        responseText = "Defect $defect is a blocker now, go fix it!"
        break
    //endregion
}

def json = new JsonBuilder()
json {
    speech responseText
    displayText responseText
    noInputPrompts reprompt
    if (ctxOut) contextOut ctxOut
}
println json