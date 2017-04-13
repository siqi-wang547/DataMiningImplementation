def get_label(data, tree):
    if not isinstance(tree, dict):
        return tree
    idx = tree.keys()[0] # '3'
    print 'idx = ' + str(idx)
    tree = tree[idx]
    keys = tree.keys()
    print "keys: " + str(keys)
    if 'BIGGERTHAN' in keys[0] or 'SMALLERTHAN' in keys[0]:
        threshold = keys[0].strip().split(' ')[1]
        print threshold, data[int(idx)]
        if data[int(idx)] >= float(threshold):
            tree = tree['BIGGERTHAN ' + threshold]
        else: tree = tree['SMALLERTHAN ' + threshold]
    else:
        tree = tree[data[int(idx)]]
    return get_label(data, tree)

tree = {"0": {"librarian": {"2": {"SMALLERTHAN 22.0": "C4", "BIGGERTHAN 22.0": "C2"}}, "professor": {"2": {"BIGGERTHAN 32.0": "C2", "SMALLERTHAN 32.0": {"3": {"SMALLERTHAN 16.0": "C4", "BIGGERTHAN 16.0": "C5"}}}}, "doctor": {"2": {"SMALLERTHAN 26.0": "C4", "BIGGERTHAN 26.0": "C3"}}, "student": {"4": {"SMALLERTHAN 20.02": {"2": {"SMALLERTHAN 23.0": "C4", "BIGGERTHAN 23.0": "C3"}}, "BIGGERTHAN 20.02": "C1"}}, "engineer": {"3": {"SMALLERTHAN 38.0": {"2": {"SMALLERTHAN 30.0": {"5": {"BIGGERTHAN 5.7642": {"4": {"SMALLERTHAN 19.95": {"1": {"spend<saving": "C4", "spend>>saving": "C4", "spend>saving": "C1", "spend<<saving": "C1"}}, "BIGGERTHAN 19.95": "C4"}}, "SMALLERTHAN 5.7642": "C1"}}, "BIGGERTHAN 30.0": "C1"}}, "BIGGERTHAN 38.0": {"2": {"BIGGERTHAN 47.0": "C1", "SMALLERTHAN 47.0": "C5"}}}}}}

data = ['doctor', 'spend>saving', 7.0, 155.0, 24.91, 10.6717, 'C3']
print get_label(data, tree)