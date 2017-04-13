The project for D-Tree is written in Python, which contains only one dtree.py file.

This Python program will take in the first command line argument as input training data, print out the feature indices, whether the feature with corresponding index being continuous or not, a JSON formatted tree structure trained by the input data, and finnaly an accuracy from a 10-fold cross validation.

Commands to run production selection:
python dtree.py /path/to/input/csv/file

Note:
1. The JSON formatted tree structure looks like {"feat_idx_1": {"feat_value_1": lable1, "feat_value_2": {feat_idx_2:{...}}}}}. We used a dictionary to recursively define the tree, and the feat_index denotes the number of column of the feature. We are printing it in JSON because there are many online JSON formating tools to display the tree structure. 
2. The input file must be clean .csv file.
3. Since we are using "LESSTHAN" and "NOLESSTHAN" as keys in the tree to separate continuous variables. If the input file contains these two words in any field except the last column would result in exceptions in accuracy calculation part.
