# Machine Learning

## 1. Emotion Detection
### Dataset:

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
* Converting bytes to emoji symbols
* Lowercasing
* Removing byte (`b'` atau `b"`)
* Removing trailing spaces and multi spaces

We convert the original multi-label hate speech dataset into binary label, then we split it into `train:val:test` with ratio `0.6:0.2:0.2`.

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
