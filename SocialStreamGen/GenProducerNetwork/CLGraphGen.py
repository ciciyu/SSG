import sys
import networkx as nx

degree =[]
with open(sys.argv[1],'r') as f:
    for line in f:
        degree.append(int(line))


G=nx.expected_degree_graph(degree)
nx.write_edgelist(G, sys.argv[2],data=False,delimiter='\t')
