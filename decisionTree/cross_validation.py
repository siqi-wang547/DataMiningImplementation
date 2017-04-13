import types

def get_label(data, tree):
    if not isinstance(tree, dict):
        return tree
    idx = tree.keys()[0] # '3'
    print 'idx = ' + str(idx)
    tree = tree[idx]
    keys = tree.keys()
    print "keys: " + str(keys)
    if '>=' in keys[0] or '<' in keys[0]:
        threshold = keys[0].strip().split(' ')[1]
        print threshold, data[int(idx)]
        if data[int(idx)] >= float(threshold):
            tree = tree['>= ' + threshold]
        else: tree = tree['< ' + threshold]
    else:
        tree = tree[data[int(idx)]]
    return get_label(data, tree)

tree = {"0": {"librarian": {"2": {">= 22.0": "C4", "< 22.0": "C2"}}, "professor": {"2": {">= 32.0": {"3": {"< 16.0": "C5", ">= 16.0": "C4"}}, "< 32.0": "C2"}}, "doctor": {"2": {"< 26.0": "C3", ">= 26.0": "C4"}}, "student": {"4": {">= 20.02": {"2": {"< 23.0": "C3", ">= 23.0": "C4"}}, "< 20.02": {"5": {">= 5.4591": {"2": {">= 10.0": "C1", "< 10.0": "C3"}}, "< 5.4591": "C1"}}}}, "engineer": {"3": {">= 38.0": {"1": {"spend<saving": "C4", "spend>>saving": {"5": {"< 5.7642": {"2": {">= 53.0": "C1", "< 53.0": "C4"}}, ">= 5.7642": "C1"}}, "spend>saving": {"2": {">= 42.0": "C4", "< 42.0": "C1"}}, "spend<<saving": {"4": {"< 20.28": "C1", ">= 20.28": {"5": {"< 3.5298": "C1", ">= 3.5298": {"2": {"< 34.0": "C1", ">= 34.0": "C4"}}}}}}}}, "< 38.0": {"2": {">= 47.0": "C5", "< 47.0": "C1"}}}}}}
data = ['professor', 'spend<saving', 32.0, 38.0, 20.38, 1.4075, 'C4']
print get_label(data, tree)
