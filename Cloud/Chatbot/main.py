from flask import Flask
from flask import jsonify

# import ML model
from model import *

def main(request):
    # check if there is request
    # key request is 'message'
    if request.form and 'message' in request.form and 'name' in request.form:
        message = request.form['message']
        name = request.form['name']

        # predict message respond by ML model
        messageValue, suggestionValue, nameValue = predict(message, name)

        # return chatbot message in json format
        return jsonify(name=nameValue, message=messageValue, suggestion=suggestionValue)
    else:
        return f'Enter command properly!'