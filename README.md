## Project structure
- EmbeddingLearning: Calculate the embedding features of the AST, CFG, and PDG code representation
- experiment: Store the source code and the corresponding features for experiment
- script: The train and test python script of the DNN model
- src: The code clone detection tool with the source code and the corresponding features
- Dockerfile: To construct the code clone detection tool

## Usage
### Generate the embedding features
The embedding features of the data set have been created and store [here](https://drive.google.com/open?id=1U_58C2__o7ULerIpLrNKhHhLOKvtcAN_).

### Run clone detection
```bash
docker run --rm -i -v /mnt/share/CloneData/data/experiment:/workspace cdetector:latest -F1 /workspace/src/1/1.c -F2 /workspace/src/1/2.c
```

### View specific feature by feature id, {file path}:{feature id}
```bash
/workspace/src/1/1.c:0
```

### Exit
```bash
exit
```