import sys

import torch.nn as nn
import torch
import numpy as np
import torch.nn.functional as F


class Net(nn.Module):
    def __init__(self, n_feature, n_hidden1, n_hidden2, n_hidden3, n_hidden4, n_hidden5, n_output):
        super(Net, self).__init__()
        self.hidden1 = torch.nn.Linear(n_feature, n_hidden1)
        self.hidden2 = torch.nn.Linear(n_hidden1, n_hidden2)
        self.hidden3 = torch.nn.Linear(n_hidden2, n_hidden3)
        self.hidden4 = torch.nn.Linear(n_hidden3, n_hidden4)
        self.hidden5 = torch.nn.Linear(n_hidden4, n_hidden5)
        self.out = torch.nn.Linear(n_hidden5, n_output)

    def forward(self, x):
        x = F.relu(self.hidden1(x))  # activation function for hidden layer
        x = F.relu(self.hidden2(x))
        x = F.relu(self.hidden3(x))
        x = F.relu(self.hidden4(x))
        x = F.relu(self.hidden5(x))
        x = self.out(x)
        return x


def restore_net_check(x):
    data = torch.from_numpy(x).float()

    net = Net(n_feature=64, n_hidden1=10, n_hidden2=20, n_hidden3=30, n_hidden4=20, n_hidden5=10, n_output=2)

    net.load_state_dict(torch.load(f='checkpoint.pth', map_location='cpu'))

    out = net(data)
    prediction = torch.max(out, 1)[1]
    print(prediction.numpy()[0])


if __name__ == '__main__':
    feature_path = sys.argv[1]
    x = np.zeros((1, 64))
    with open(feature_path, 'r') as feature_file:
        line = feature_file.readline()
        cols = line.split(' ')
        for i in range(len(cols)):
            x[0][i] = float(cols[i])
    restore_net_check(x)
