import torch.nn as nn
import torch
import numpy as np
import pandas as pd
import os
import torch.nn.functional as F

os.environ["CUDA_VISIBLE_DEVICES"] = "0"


def get_data(csv_path):
    csv_data = pd.read_csv(csv_path, sep=',', header=None)
    data = csv_data.values.astype(np.float)[:, 0:64]
    label = csv_data.values.astype(np.int)[:, 64:]

    total_data = np.hstack((data, label))

    np.random.shuffle(total_data)

    total_size = len(total_data)
    train_size = int(0.8 * total_size)
    # test_size = total_size - train_size

    train_data = torch.from_numpy(total_data[0:train_size, :-1]).float()
    test_data = torch.from_numpy(total_data[train_size:, :-1]).float()
    train_label = torch.from_numpy(total_data[0:train_size, -1]).long()
    test_label = torch.from_numpy(total_data[train_size:, -1]).long()
    # test_label = torch.from_numpy(total_data[train_size:, -1].reshape(-1, 1)).int()

    return train_data, test_data, train_label, test_label


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


if __name__ == '__main__':
    net = Net(n_feature=64, n_hidden1=10, n_hidden2=20, n_hidden3=30, n_hidden4=20, n_hidden5=10, n_output=2)
    print(net)
    net.cuda()
    # optimize parameter
    optimizer = torch.optim.Adam(net.parameters(), lr=0.001)
    # calculate loss
    loss_func = torch.nn.CrossEntropyLoss()

    train_data, test_data, train_label, test_label = \
        get_data('G:\\share\\CloneData\\data\\training\\data.csv')

    for epoch in range(10000):
        out = net(train_data.cuda())
        loss = loss_func(out, train_label.cuda())

        optimizer.zero_grad()
        loss.backward()
        optimizer.step()

        if epoch % 200 == 0:
            prediction = torch.max(out, 1)[1].cuda()
            pred_y = prediction.data.cpu().numpy()
            target_y = train_label.data.cpu().numpy()
            accuracy = float((pred_y == target_y).astype(int).sum()) / float(target_y.size)
            print("accuracy=", accuracy)

    torch.save(net.state_dict(), 'checkpoint.pth')
    out = net(test_data.cuda())
    prediction = torch.max(out, 1)[1].cuda()

    pred_y = prediction.data.cpu().numpy()
    target_y = test_label.data.cpu().numpy()
    # print(pred_y)
    # print(target_y)

    accuracy = float((pred_y == target_y).astype(int).sum()) / float(target_y.size)
    print("accuracy=", accuracy)
