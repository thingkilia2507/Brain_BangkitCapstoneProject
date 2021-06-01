from flask import Flask
from flask import jsonify

# import ML model
from model import *

def main(request):
    # check if there is request
    # key request is 'message'
    if request.form and 'message' in request.form:
        message = request.form['message']

        # predict message respond by ML model
        moodValue = predict(message) 

        # return chatbot message in json format
        return jsonify(mood=moodValue)
    else:
        return f'Enter command properly!'