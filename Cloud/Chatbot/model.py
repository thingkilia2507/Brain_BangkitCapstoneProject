import dialogflow
from google.protobuf.json_format import MessageToDict
from google.api_core.exceptions import InvalidArgument


# call ML model to predict message status?
def predict(message, name):
    messageValue, suggestionValue, detected_intent = detect_intent_texts(message)
    
    greetings_intent = ['greetings.default', 'greetings.morning', 'greetings.noon', 'greetings.night']

    last_chars = messageValue[0][-2:]
    contains_alphabet = any(c.isalpha() for c in last_chars)
    nickname = ' ' + name if detected_intent in greetings_intent else ', ' + name

    if contains_alphabet:
      messageValue[0] = messageValue[0][:-1] + nickname + messageValue[0][-1:]
    else:
      messageValue[0] = messageValue[0][:-2] + nickname + messageValue[0][-2:]
  
    return messageValue, suggestionValue, name


def detect_intent_texts(message, project_id='soulmood', session_id='me', language_code='id'):
    """
        Returns the result of detect intent with texts as inputs.

        Using the same `session_id` between requests allows continuation
        of the conversation.
    """

    session_client = dialogflow.SessionsClient()
    session = session_client.session_path(project_id, session_id)

    text_input = dialogflow.types.TextInput(text=message, language_code=language_code)
    query_input = dialogflow.types.QueryInput(text=text_input)

    try:
        response = session_client.detect_intent(session=session, query_input=query_input)
        response = MessageToDict(response, preserving_proto_field_name = True)
    except InvalidArgument:
        raise

    fulfillment_text = response['query_result']['fulfillment_text']
    messageValue = [msg.replace('\n', '') for msg in fulfillment_text.split('\n\n')]
    detected_intent = response['query_result']['intent']['display_name']

    try:
      quick_replies = response['query_result']['fulfillment_messages'][0]['quick_replies']['quick_replies']
      suggestionValue = quick_replies
    except KeyError:
      suggestionValue = []

    return messageValue, suggestionValue, detected_intent