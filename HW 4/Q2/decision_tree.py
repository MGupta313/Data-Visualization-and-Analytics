import numpy as np 
import ast
import util

class DecisionTree(object):
	def __init__(self, max_depth):
		# Initializing the tree as an empty dictionary or list, as preferred
		self.tree = {}
		self.max_depth = max_depth
		pass
		
	def learn(self, X, y, par_node = {}, depth=0):
		# TODO: Train the decision tree (self.tree) using the the sample X and labels y
		# You will have to make use of the functions in utils.py to train the tree

		# Use the function best_split in util.py to get the best split and 
		# data corresponding to left and right child nodes
		
		# One possible way of implementing the tree:
		#    Each node in self.tree could be in the form of a dictionary:
		#       https://docs.python.org/2/library/stdtypes.html#mapping-types-dict
		#    For example, a non-leaf node with two children can have a 'left' key and  a 
		#    'right' key. You can add more keys which might help in classification
		#    (eg. split attribute and split value)
		### Implement your code here
		def decideLabel(labels):
			ones = sum(labels)
			if len(labels)-ones >= ones: 
				return 0
			return 1 
		def decideSplitAttrVal(X, y, attrs):
			# iterate all attribute in X's instance, choose the best attribute for spliting
			for i in range(len(X[0])):  # iterate all attribute
				#print("     Comparing No."+str(i)+" attr \n")
				maxInfoGain = splitVal = splitAttr = -1
				values_of_per_Attr = [[X[k][i]] for k in range(len(X))]
				for split_val_try in values_of_per_Attr:
					Xleft, Xright, yleft, yright = partition_classes(values_of_per_Attr, y, 0, split_val_try[0])
					curInfoGain = information_gain(y, [yleft,yright])
					# Update maxInfoGain
					if curInfoGain > maxInfoGain:
						splitAttr = i
						splitVal = split_val_try[0]
						maxInfoGain = curInfoGain
				attrs.append((1-maxInfoGain, splitAttr, splitVal))
			heapq.heapify(attrs)
			return
		def buildTree(X, y, dep, attrs):
			# If depth exceed or there is only one feature in instance of X, return label (0 or 1) directly.
			if dep >= self.max_depth or len(attrs) <= 1:
				return decideLabel(y)
			# If features in y are the same, no need for more branch
			if sum(y) == len(y) or sum(y) == 0:
				return y[0]
			grades, splitAttr, splitVal = heapq.heappop(attrs)
			
			Xleft, Xright, yleft, yright = partition_classes(X, y, splitAttr, splitVal)
			if len(Xleft) == 0 or len(Xright) == 0:
				return decideLabel(y)
			else:
				tree = {}
				#tree[splitAttr] = [splitVal, buildTree(Xleft, yleft, dep+1), buildTree(Xright, yright, dep+1)]
				tree[splitAttr] = [splitVal, buildTree(Xleft, yleft, dep+1, copy.deepcopy(attrs)),
				buildTree(Xright, yright, dep+1, copy.deepcopy(attrs))]
				return tree
		attrs = []
		util.best_split(X, y, attrs)
		self.tree = buildTree(X, y, 1, attrs)


	def classify(self, record):
		# TODO: classify the record using self.tree and return the predicted label
		### Implement your code here
		cur = self.tree
		# remeber the keys of self.tree is splitAttr, which is index
		tmp = record[:]
		while isinstance(cur, dict):
			feature = list(cur.keys())[0]
			if isinstance(tmp[feature], int) or isinstance(tmp[feature], float):
				if tmp[feature] <= cur[feature][0]:
					cur = cur[feature][1]
				else:
					cur = cur[feature][2]
			else:
				if tmp[feature] == cur[feature][0]:
					cur = cur[feature][1]
				else:
					cur = cur[feature][2]
			#tmp = tmp[:feature]+tmp[feature+1:]
		return cur
