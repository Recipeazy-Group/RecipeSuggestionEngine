from gensim.models import KeyedVectors
model =  KeyedVectors.load_word2vec_format('GoogleNews-vectors-negative300.bin.gz', 
binary=True)

vector = model['easy']

vector.shape

vectors = [model[x] for x in "This is some text I am processing with Spacy".split(' ')]

print(vectors);
