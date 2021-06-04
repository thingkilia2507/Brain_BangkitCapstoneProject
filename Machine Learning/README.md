# Machine Learning

## 1. Emotion Detection
Emotion Detection is used to detect what emotion the user is feeling based on the user's chat with the bot.
### Dataset:
* ### http://saifmohammad.com/WebPages/EmotionIntensity-SharedTask.html.
```
Part of the 8th Workshop on Computational Approaches to Subjectivity, Sentiment and Social Media Analysis (WASSA-2017), 
which is to be held in conjunction with EMNLP-2017.

Background and Significance: Existing emotion datasets are mainly annotated categorically without an indication of degree of emotion. 
Further, the tasks are almost always framed as classification tasks (identify 1 among n emotions for this sentence). 
In contrast, it is often useful for applications to know the degree to which an emotion is expressed in text. 
This is the first task where systems have to automatically determine the intensity of emotions in tweets.

Data: Training and test datasets are provided for four emotions: joy, sadness, fear, and anger. For example, the anger training 
dataset has tweets along with a real-valued score between 0 and 1 indicating the degree of anger felt by the speaker. 
The test data includes only the tweet text. Gold emotion intensity scores will be released after the evaluation period.
```
The dataset which consists of:
* Train
* Validation
* Test

with each categories split into four (joy, sadness, anger, fear) is merged according to categories for training purposes. 
This dataset is in english so we use Google Translate API to translate the whole dataset.

* ### https://github.com/meisaputri21/Indonesian-Twitter-Emotion-Dataset
```
Author: Mei Silviana Saputri, Rahmad Mahendra, and Mirna Adriani, "Emotion Classification on Indonesian Twitter Dataset", 
in Proceeding of International Conference on Asian Language Processing 2018. 2018.

This dataset contains 4.403 Indonesian tweets which are labeled into five emotion classes: love, anger, sadness, joy and fear.

Data Format: Each line consists of a tweet and its respective emotion label separated by semicolon (,). The first line is a header. 
For a tweet with coma (,) inside the text, there is an quote (" ") to avoid column separation.
The tweets in this dataset has been pre-processed using the following criterias:
1. Username mention (@) has been replaced with term [USERNAME]
2. URL/hyperlink (http://... or https://...) has been replaced with term [URL]
3. Sensitive number, such as phone number, invoice number and courier tracking number has been replaced with term [SENSITIVE-NO]
```
This second dataset is split into five classes: love, anger, sadness, happy and fear. For training purposes love is dropped and happy is transformed into joy. The dataset is then merged with the first dataset.

### Training
For training we merged both datasets. For the test set we used the test dataset from the first dataset. The final distribution of the dataset are:
- [Training Set](datasets/emotion_detection/train_emotion_fix.csv) (7277 samples)
- [Validation Set](datasets/emotion_detection/dev_emotion_fix.csv) (347 samples)
- [Test Set](datasets/emotion_detection/test_emotion_fix.csv) (3047 samples)

### Model Performance on Test Set:
<img src="results/Emotion Detection - Model Performance on Test Set.JPG" width="500" height="250">

## 2. Hate Speech Detection
Hate speech detection is used to block messages containing hate speech in the live-chat forum (between users).
### Dataset:
* https://github.com/okkyibrohim/id-multi-label-hate-speech-and-abusive-language-detection. The work of:
```
@inproceedings{ibrohim-budi-2019-multi,
    title = "Multi-label Hate Speech and Abusive Language Detection in {I}ndonesian Twitter",
    author = "Ibrohim, Muhammad Okky  and
      Budi, Indra",
    booktitle = "Proceedings of the Third Workshop on Abusive Language Online",
    month = aug,
    year = "2019",
    address = "Florence, Italy",
    publisher = "Association for Computational Linguistics",
    url = "https://www.aclweb.org/anthology/W19-3506",
    doi = "10.18653/v1/W19-3506",
    pages = "46--57",
    abstract = "Hate speech and abusive language spreading on social media need to be detected automatically to avoid conflict between citizen. Moreover, hate speech has a target, category, and level that also needs to be detected to help the authority in prioritizing which hate speech must be addressed immediately. This research discusses multi-label text classification for abusive language and hate speech detection including detecting the target, category, and level of hate speech in Indonesian Twitter using machine learning approach with Support Vector Machine (SVM), Naive Bayes (NB), and Random Forest Decision Tree (RFDT) classifier and Binary Relevance (BR), Label Power-set (LP), and Classifier Chains (CC) as the data transformation method. We used several kinds of feature extractions which are term frequency, orthography, and lexicon features. Our experiment results show that in general RFDT classifier using LP as the transformation method gives the best accuracy with fast computational time.",
}
```
The original dataset is Twitter-based so the preprocessing part includes:
* Removing `'RT', '\n', 'username', 'USER'`
* Masking `'link'` into `<link>`
* Replacing HTML character symbols, such as `&gt; &lt;`
* Converting bytes to emoji symbols, for example: `\\xf0\\x9f\\x8e\\xb6`--> `🎶`
* Lowercasing
* Removing byte (`b'` atau `b"`)
* Removing trailing spaces and multi spaces

We convert the original multi-label hate speech dataset into binary label, then we split it into `train:val:test` with ratio `0.6:0.2:0.2`. The final preprocessed datasets can be accessed here:
- [Training Set](datasets/hate_speech/train_emo_v2.csv) (7873 samples)
- [Validation Set](datasets/hate_speech/val_emo_v2.csv) (2625 samples)
- [Test Set](datasets/hate_speech/test_emo_v2.csv) (2625 samples)

### Model Performance on Test Set:
<img src="results/Hate Speech Detection - Model Performance on Test Set.JPG" width="500" height="250">

## 3. Model and Deployment
Both the Emotion Detection and Hate Speech Detection use <b>ALBERT</b> pre-trained language model [(IndoBERT-lite)](https://huggingface.co/indobenchmark/indobert-lite-base-p1) from [IndoNLU](https://www.indobenchmark.com/) and then adapted to the corresponding downstream tasks. The IndoBERT-lite is a work of:
```
@inproceedings{wilie2020indonlu,
  title={IndoNLU: Benchmark and Resources for Evaluating Indonesian Natural Language Understanding},
  author={Bryan Wilie and Karissa Vincentio and Genta Indra Winata and Samuel Cahyawijaya and X. Li and Zhi Yuan Lim and S. Soleman and R. Mahendra and Pascale Fung and Syafri Bahar and A. Purwarianti},
  booktitle={Proceedings of the 1st Conference of the Asia-Pacific Chapter of the Association for Computational Linguistics and the 10th International Joint Conference on Natural Language Processing},
  year={2020}
}
```

Both the models are deployed to [HuggingFace](https://huggingface.co/celine).

### How to Use The Adapted Model:
#### Load Model, Tokenizer, and Configuration
```python
from transformers import AlbertConfig, BertTokenizer, TFAlbertForSequenceClassification

PRETRAINED_MODEL = 'celine/hate-speech_indobenchmark-indobert-lite-base-p1'
hs_tokenizer = BertTokenizer.from_pretrained(PRETRAINED_MODEL, do_lower_case=True)
hs_config = AlbertConfig.from_pretrained(PRETRAINED_MODEL)
hs_model = TFAlbertForSequenceClassification.from_pretrained(PRETRAINED_MODEL, config=hs_config)
```
```python
from transformers import AlbertConfig, BertTokenizer, TFAlbertForSequenceClassification

PRETRAINED_MODEL = 'celine/emotion-detection_indobenchmark-indobert-lite-base-p1'
ed_tokenizer = BertTokenizer.from_pretrained(PRETRAINED_MODEL, do_lower_case=True)
ed_config = AlbertConfig.from_pretrained(PRETRAINED_MODEL)
ed_model = TFAlbertForSequenceClassification.from_pretrained(PRETRAINED_MODEL, config=ed_config)
```

#### Predict
```python
text = 'Dasar kau bego!'
subwords = hs_tokenizer(text, return_tensors='tf')
predictions = hs_model(subwords)['logits']
predicted_class_idx = np.argmax(predictions, axis=1)[0]
predicted_class_label = hs_config.id2label[predicted_class_idx]
```
```python
text = 'Aku gamau denger mama marah lagi!'
subwords = ed_tokenizer(text, return_tensors='tf')
predictions = ed_model(subwords)['logits']
predicted_class_idx = np.argmax(predictions, axis=1)[0]
predicted_class_label = ed_config.id2label[predicted_class_idx]
```
## 4. Acknowledgment
To train the model on our downstream tasks, we use TensorFlow for HuggingFace. We mainly use and refer to codes from below, then modified accordingly:
* https://github.com/huggingface/transformers/blob/master/examples/tensorflow/text-classification/run_text_classification.py
* https://towardsdatascience.com/tensorflow-and-transformers-df6fceaf57cc
* https://towardsdatascience.com/working-with-hugging-face-transformers-and-tf-2-0-89bf35e3555a
