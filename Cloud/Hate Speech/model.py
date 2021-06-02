import re
import numpy as np
from transformers import AlbertConfig, BertTokenizer, TFAlbertForSequenceClassification, TFTrainingArguments, set_seed

# load model
global PRETRAINED_MODEL, tokenizer, model
PRETRAINED_MODEL = 'celine/hate-speech_indobenchmark-indobert-lite-base-p1'
tokenizer = BertTokenizer.from_pretrained(PRETRAINED_MODEL, do_lower_case=True)
model = TFAlbertForSequenceClassification.from_pretrained('./hate_speech')
# model = TFAlbertForSequenceClassification.from_pretrained(PRETRAINED_MODEL)


def clean(text):
    rgx_multi_space = r' {2,}'
    rgx_byte_format = r'''\n|\\n|b'|b"'''

    text = text.lower()
    cleansed = re.sub(rgx_byte_format, '', text)
    cleansed = re.sub(rgx_multi_space, ' ', cleansed)                    # remove multiple spaces
    cleansed = cleansed.strip()                                          # remove trailing spaces
    return cleansed


def split_into_smaller_chunks(subwords, idx_start, chunk_size, remaining_size):
    subwords_chunk = {}
    try:
        subwords_chunk['input_ids'] = tf.slice(subwords['input_ids'], [0,idx_start], [1,chunk_size])
        subwords_chunk['token_type_ids'] = tf.slice(subwords['token_type_ids'], [0,idx_start], [1,chunk_size])
        subwords_chunk['attention_mask'] = tf.slice(subwords['attention_mask'], [0,idx_start], [1,chunk_size])

    except tf.errors.InvalidArgumentError as e:
        subwords_chunk['input_ids'] = tf.slice(subwords['input_ids'], [0,idx_start], [1,remaining_size])
        subwords_chunk['token_type_ids'] = tf.slice(subwords['token_type_ids'], [0,idx_start], [1,remaining_size])
        subwords_chunk['attention_mask'] = tf.slice(subwords['attention_mask'], [0,idx_start], [1,remaining_size])

    return subwords_chunk


# call ML model to predict bad message 
def predict(message):
    ID2LABEL = {0: 'non_hs', 1: 'hs'}
    MAX_SEQ_LEN = 512
    hs_idxs = []

    # prepare text
    preprocessed_text = clean(message)

    # tokenizing
    subwords = tokenizer(preprocessed_text, return_tensors='tf')
    input_seq_len = subwords['input_ids'].shape[1]

    '''
    Source: https://stackoverflow.com/a/58643108
    Handle token > MAX_SEQ_LEN by:
      1. Splitting the tokenization results into smaller chunks
      2. Classify each of them and combine the results back together
            (choose the class which was predicted for most of the chunks for example, dengan kata lain ambil modus nya)
    '''
    if input_seq_len > MAX_SEQ_LEN:
        # https://www.kdnuggets.com/2021/04/apply-transformers-any-length-text.html
        chunk_size = input_seq_len % MAX_SEQ_LEN
        remaining_size = input_seq_len % chunk_size

        for i in range(0, input_seq_len, chunk_size):
            subwords_chunk = split_into_smaller_chunks(subwords, i, chunk_size, remaining_size)
            # predict
            predictions = model(subwords_chunk)['logits']
            predicted_class_idx = np.argmax(predictions, axis=1)[0]
            hs_idxs.append(predicted_class_idx)

        # select mode
        hs_idx = max(set(hs_idxs), key=hs_idxs.count)
        predicted_class_label = ID2LABEL[hs_idx]

    else:
        # predict
        predictions = model(subwords)['logits']
        predicted_class_idx = np.argmax(predictions, axis=1)[0]
        predicted_class_label = ID2LABEL[predicted_class_idx]

    if predicted_class_label == 'non_hs':
        statusValue = False
        message = message
    else:
        statusValue = True
        message = 'Pesan ini dihapus karena mengandung muatan negatif'

    return message, statusValue
