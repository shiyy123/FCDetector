import time

import tensorflow as tf
import numpy as np

base_path = '/home/cary/Documents/Data/CloneData/csv/'

training_path = base_path + 'b_3,12,7,6,5,9,10,13,2,1_train_ident_word2vec.csv'
test_path = base_path + '17,19,14,15,16_test_ident_word2vec.csv'

dimension = 32


def main():
    training_set = tf.contrib.learn.datasets.base.load_csv_with_header(
        filename=training_path,
        target_dtype=np.int,
        features_dtype=np.float32)

    test_set = tf.contrib.learn.datasets.base.load_csv_with_header(
        filename=test_path,
        target_dtype=np.int,
        features_dtype=np.float32)

    feature_columns = [tf.contrib.layers.real_valued_column("", dimension=dimension)]
    classifier = tf.contrib.learn.DNNClassifier(feature_columns=feature_columns,
                                                hidden_units=[10, 20, 30, 20, 10],
                                                n_classes=2, model_dir='model')

    # model_dir = "model/clone_model"

    def get_train_inputs():
        x = tf.constant(training_set.data)
        y = tf.constant(training_set.target)

        return x, y

    # 现有模型可以加载后直接使用，不训练即可
    print('training begin')
    classifier.fit(input_fn=get_train_inputs, steps=2000)
    print('training end')

    def get_test_inputs():
        x = tf.constant(test_set.data)
        y = tf.constant(test_set.target)

        return x, y

    # accuracy_score = classifier.evaluate(input_fn=get_test_inputs, steps=1)["accuracy"]
    print('evaluate begin')
    res = classifier.evaluate(input_fn=get_test_inputs, steps=1)
    print('evaluate end')
    print(res)
    accuracy_score = res["accuracy"]
    with open('res.txt', 'a') as f:
        f.write("train_data:" + training_path + "\n")
        f.write("test_data:" + test_path + "\n")
        f.write(str(res) + '\n')
        f.write("F1:" + str(2 * float(res["precision/positive_threshold_0.500000_mean"]) * float(
            res["recall/positive_threshold_0.500000_mean"]) / (
                                    float(res["precision/positive_threshold_0.500000_mean"]) +
                                    float(res["recall/positive_threshold_0.500000_mean"]))) + '\n')

    print("\nTest Accuracy: {0:f}\n".format(accuracy_score))


if __name__ == '__main__':
    print("begin time:" + str(time.asctime(time.localtime(time.time()))))
    main()
    print("end time:" + str(time.asctime(time.localtime(time.time()))))
