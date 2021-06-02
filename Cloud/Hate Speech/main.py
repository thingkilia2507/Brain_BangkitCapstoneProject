import json
import re
import numpy as np
from flask import Flask
from flask import jsonify
from flask import request
from transformers import AlbertConfig, BertTokenizer, TFAlbertForSequenceClassification, TFTrainingArguments, set_seed

# import ML model
from model import *

app = Flask(__name__)

@app.route("/",methods=['POST'])
def main():
    # check if there is request
    # key request is 'message'
    if request.form and 'message' in request.form:
        message = request.form['message']

        # predict message respond by ML model
        messageValue, statusValue = predict(message)

        # return chatbot message in json format
        return jsonify(message=messageValue, status=statusValue)
    else:
        return 'Enter command properly!'


if __name__ == '__main__':
    app.run(host="127.0.0.1", port=8080, debug=True)
