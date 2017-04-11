# !/usr/bin/python
from math import log
import operator
import random
import json


def loadData(filepath):
    matrix = []
    with open(filepath, 'rb') as f:
        for line in f.readlines():
            line = line.strip().split(',')
            cur = [float(line[i]) if is_continuous[i] else line[i] for i in range(len(line))]
            matrix.append(cur)
    return matrix
    # random.shuffle(matrix)
    # len = len(matrix) * 9 / 10
    # return matrix[:len], matrix[len+1:]


# calculate Entropy
# Shannon Entroy defined as the expectation of information of all categories
def cal_entropy(dataset):
    num = len(dataset) # size of dataset
    label_dic = {}
    for data in dataset: # count occurrence of each label
        label = data[-1]
        if label in label_dic:
            label_dic[label] += 1
        else: label_dic[label] = 1
    entropy = 0.0
    for key in label_dic:
        prob = float(label_dic[key]) / num # calculate probability
        entropy -= prob * log(prob, 2) # Entropy
    return entropy


# calculate information gain rate c45
def calculate_ig_rate(base_ent, new_ent):
    info_gain = base_ent - new_ent
    rate = info_gain / base_ent
    return rate


# split the dataset
def split_discrete(dataset, idx, value):
    # three parameters: dataset to split, feature to split on, target value of feature
    subDataSet = [vec[:idx] + vec[idx+1:] for vec in dataset if vec[idx] == value]
    return subDataSet


def split_continuous(dataset, idx, value):
    # for a continuous feature, we only do binary split by a given target value
    subDataSet1 = [vec[:idx] + vec[idx+1:] for vec in dataset if vec[idx] < value]
    subDataSet2 = [vec[:idx] + vec[idx+1:] for vec in dataset if vec[idx] >= value]
    return subDataSet1, subDataSet2


# find the best feature to split the dataset
def optimal_split_feat(dataset, is_continuous):
    print dataset[0]
    feature_num = len(dataset[0]) - 1  # number of features
    # print feature_num
    base_entropy = cal_entropy(dataset)  # initial entropy
    best_feature_idx = -1
    best_split_point = 0.0
    max_ig_rate = 0.0
    for i in range (feature_num):  # iterate through columns(feature)
        feature = [data[i] for data in dataset]

        if not is_continuous[i]:
            new_entropy = 0.0
            for value in set(feature):  # get all possible values of this column
                subDataSet = split_discrete(dataset, i, value) # for each target value, get its occurrence
                prob = len(subDataSet) / float(len(dataset))
                new_entropy += prob * cal_entropy(subDataSet) # cal the entropy
            # if new_entropy < base_entropy:  # return ith column when entropy reaches minimum
            #     base_entropy = new_entropy
            #     best_feature_idx = i
            # info_gain = base_entropy - new_entropy
            ig_rate = calculate_ig_rate(base_entropy, new_entropy)
            if ig_rate > max_ig_rate:
                max_ig_rate = ig_rate
                best_feature_idx = i
        else:
            sortedFeature = sorted(set(feature))  # sort all possible value in the given column
            for value in sortedFeature:  # from small to big, choose as split target value one by one
                new_entropy = 0.0
                subDataSet1, subDataSet2 = split_continuous(dataset, i, value)  # two subset after split
                new_entropy += len(subDataSet1) / float(len(dataset)) * cal_entropy(subDataSet1)
                new_entropy += len(subDataSet2) / float(len(dataset)) * cal_entropy(subDataSet2)
                # modification from c45 could be applied IG - log2(N-1)/|D|, N = len(sortedFeature, D = len(dataSet)
                ig_rate = calculate_ig_rate(base_entropy, new_entropy)
                if ig_rate > max_ig_rate:
                    max_ig_rate = ig_rate
                    best_feature_idx = i
                    best_split_point = value
    return best_feature_idx, best_split_point


def majorityCnt(labels):
    classCount = {}
    for vote in labels:
        if vote not in classCount.keys():
            classCount[vote] = 0
        classCount[vote] += 1
    sortedClassCount = sorted(classCount.iteritems(), key=operator.itemgetter(1), reverse=True)
    return sortedClassCount[0][0]


def createTree(dataSet, attributes, is_continuous):
    labels = [data[-1] for data in dataSet]  # labels in dataset
    # print len(labels)
    if len(set(labels)) == 1:
        return labels[0] # only one label, return
    if len(dataSet[0]) == 1:
        return majorityCnt(labels) # after traversing all features, return the majority label
    bestIdx, bestSplitPoint = optimal_split_feat(dataSet, is_continuous)
    print "Best feature index: " + str(bestIdx)
    bestAttr = attributes[bestIdx]
    # print bestIdx, bestAttr
    myTree = {bestAttr: {}}
    del(attributes[bestIdx])
    if not is_continuous[bestIdx]:
        featureValues = [data[bestIdx] for data in dataSet]
        del (is_continuous[bestIdx])
        for value in set(featureValues):  # recursively create the tree
            subAttrs = attributes[:]
            subContinuous = is_continuous[:]
            myTree[bestAttr][value] = createTree(split_discrete(dataSet, bestIdx, value), subAttrs, subContinuous)
    else:
        print "enter"
        featureValues = ['< ' + str(bestSplitPoint), '>= ' + str(bestSplitPoint)]
        del (is_continuous[bestIdx])
        for i in range(2): # recursively create the tree
            value = featureValues[i]
            subAttrs = attributes[:]
            subContinuous = is_continuous[:]
            myTree[bestAttr][value] = createTree(split_continuous(dataSet, bestIdx, value)[i], subAttrs, subContinuous)
        # sample tree structure {'feature1': {value1: label1, value2: {'feature2': {value1: label2, value2: label3}}}}
    return myTree

attributes = ['Type', 'LifeStyle', 'Vacation', 'eCredit', 'salary', 'property', 'class']
is_continuous = [False, False, True, True, True, True, False]
#filepath = 'train5lines'
filepath = 'trainProdSelection'
train = loadData(filepath)
tree = createTree(train, attributes, is_continuous)
print json.dumps(tree)
