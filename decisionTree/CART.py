from numpy import *


# Load dataset into a matrix
def loadData(filepath):
    matrix = []
    with open(filepath, 'rb') as f:
        for line in f.readlines():
            cur = line.strip().split(',')
            matrix.append(cur)
        return matrix


def binarySplit(dataSet, feature, value):
    mat0 = dataSet[nonzero(dataSet[:, feature] > value)[0], :][0]
    mat1 = dataSet[nonzero(dataSet[:, feature] <= value)[0], :][0]
    return mat0, mat1


def leafFunc(dataSet):
    return mean(dataSet[:,-1])


# error function
def errFun(dataSet):
    return var(dataSet[:,-1]) * shape(dataSet)[0]


def optimalSplit(dataSet, leafType=leafFunc, errType=errFun, ops=(1,4)):
    tolS = ops[0]
    tolN = ops[1]
    print dataSet[:,-1].T.A1

    if len(set(dataSet[:,-1].T.A1.tolist())) == 1:
        return None, leafFunc(dataSet)
    m,n = shape(dataSet)
    S = errFun(dataSet)
    bestSplit = inf
    bestIdx = 0
    bestVal = 0
    for featIdx in range(n-1):
        for splitVal in set(dataSet[:,featIdx]):
            mat0, mat1 = binarySplit(dataSet, featIdx, splitVal)
            if shape(mat0)[0] < tolN or shape(mat1)[0] < tolN: continue
            currS = errFun(mat0) + errFun(mat1)
            if currS < bestSplit:
                bestIdx = featIdx
                bestVal = splitVal
                bestSplit = currS
    if S - bestSplit < tolS:
        return None, leafFunc(dataSet)
    mat0, mat1 = binarySplit(dataSet, bestIdx, bestVal)
    if shape(mat0)[0] < tolN or shape(mat1)[0] < tolN:
        return None, leafFunc(dataSet)
    return bestIdx, bestVal


def createTree(dataSet, leafType=leafFunc, errType=errFun, ops=(1,4)):
    feat, val = optimalSplit(dataSet, leafType, errType, ops)
    if feat is None: return val
    resTree = {}
    resTree['splitIdx'] = feat
    resTree['splitVal'] = val
    left, right = binarySplit(dataSet, feat, val)
    resTree['l'] = createTree(left, leafType, errType, ops)
    resTree['r'] = createTree(right, leafType, errType, ops)
    return resTree

def main():
    data = loadData('/Users/siqiwang/siqiw2/KNN/decisionTree/trainProdSelection')
    mymatrix = mat(data)
    tree = createTree(mymatrix)


if __name__ == '__main__':
    main()