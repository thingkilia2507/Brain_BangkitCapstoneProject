import re
import numpy as np
from transformers import AlbertConfig, BertTokenizer, TFAlbertForSequenceClassification, TFTrainingArguments, set_seed

def clean(text):
    rgx_multi_space = r' {2,}'
    rgx_byte_format = r'''\n|\\n|b'|b"'''

    text = text.lower()
    cleansed = re.sub(rgx_byte_format, '', text)
    cleansed = re.sub(rgx_multi_space, ' ', cleansed)                                      # remove multiple spaces
    cleansed = cleansed.strip()                                                            # remove trailing spaces

    '''
      Only take chat from user, exclude chat from bot to avoid unnecessary sequence that can either:
        1. Create bias to the mood detected
        2. Overflowing the MAX_SEQ_LEN when it's actually not needed
    '''
    user_chat = re.findall('<user>:([^<]*)', cleansed)
    cleansed = '. '.join(user_chat)

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
    ID2LABEL =  {0:'Marah', 1:'Takut', 2:'Sedih', 3:'Bahagia'}
    MAX_SEQ_LEN = 512
    moods_idx = []

    # load model and tokenizer
    PRETRAINED_MODEL = 'celine/emotion-detection_indobenchmark-indobert-lite-base-p1'
    tokenizer = BertTokenizer.from_pretrained(PRETRAINED_MODEL, do_lower_case=True)
    model = TFAlbertForSequenceClassification.from_pretrained(PRETRAINED_MODEL)

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
            moods_idx.append(predicted_class_idx)

        # select mode
        mood_idx = max(set(moods_idx), key=moods_idx.count)
        mood_value = ID2LABEL[mood_idx]

    else:
        # predict
        predictions = model(subwords)['logits']
        predicted_class_idx = np.argmax(predictions, axis=1)[0]
        mood_value = ID2LABEL[predicted_class_idx]
    
    return mood_value
