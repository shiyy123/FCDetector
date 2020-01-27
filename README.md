## Project structure
- AutoenCODE: This folder contains the source code of word2vec.
- graph2vec: This folder contains the source code of graph2vec.
- joern: This folder contains the source code of joern, which is used to extract AST and CFG.
- script: This folder contains the deep learning model to detect code clone used in our experiment.
- src: This folder contains the code of extracting the syntactical and semantic embedding features of the C++ source code. It also used to generate clone and non-clone pairs.

## Usage
### Generate the embedding features
The src/main/java/test/GenerateTrainingData.java file is the entrance of generating embedding feature data set for the C++ source code.
The embedding features of the data set have been created and store [here](https://drive.google.com/open?id=1FcJ1l4YNePKU43NOo5XuGHpZl9FSxAO2).

### Training clone detection model
The script/Classify/Classifier.py file is the entrance of training clone detection model. 