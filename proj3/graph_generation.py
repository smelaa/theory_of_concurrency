import sys
import re
import graphviz
import matplotlib.colors as mcolors
import csv
from queue import Queue

def parse_file(filename):
    try:
        file = open(filename, "r")
    except IOError:
        print(f"Cannot open {filename} file")
        sys.exit(0)
    matrix=[]
    n_cols=-1
    for line in file:
        row=list(map(float, re.split(", | / ", line)))
        if n_cols==-1:
            n_cols=len(row)
        elif len(row)!=n_cols:
            print("Wrong input. Each row needs to have the same number of elements.")
            sys.exit(0)
        matrix.append(row)
    if len(matrix)!=n_cols-1:
        print(f"Wrong input. Matrix size should be N x (N+1). Got {len(matrix)} x {n_cols}.")
        sys.exit(0)
    file.close()
    return matrix

def get_alphabet(N):
    alphabet=[]
    for i in range (1, N+1):
        for k in range (i+1, N+1):
            alphabet.append(f"A_{i}_{k}")
            for j in range (i, N+2):
                alphabet.append(f"B_{i}_{j}_{k}")
                alphabet.append(f"C_{i}_{j}_{k}")
    return alphabet

def get_dependencies(N):
    #in a result there is no trivial dependencies like (A, A), which theoretically should be included.
    dep=set()
    for i in range (1, N+1):
        for k in range (i+1, N+1):
            if i-1>0:
                dep.add((f"A_{i}_{k}", f"C_{i-1}_{i}_{i}"))
                dep.add((f"A_{i}_{k}", f"C_{i-1}_{i}_{k}"))
            for j in range (i, N+2):
                dep.add((f"B_{i}_{j}_{k}", f"A_{i}_{k}"))
                if i-1>0 and k-1>0:
                    dep.add((f"B_{i}_{j}_{k}", f"C_{i-1}_{j}_{k-1}"))
                dep.add((f"C_{i}_{j}_{k}", f"B_{i}_{j}_{k}"))
                if i-1>0:    
                    dep.add((f"C_{i}_{j}_{k}", f"C_{i-1}_{j}_{k}"))
    return dep

def translate_depset_to_depdict(depset, alphabet):
    def append_index(elem1, elem2, depdict):
        elem_dep=depdict[elem1]
        elem_dep.append(elem2)
        depdict[elem1]=elem_dep
    depdict=dict()
    for letter in alphabet:
        depdict[letter]=[]
    for elem in depset:
        #append_index(elem[0], elem[1], depdict)
        append_index(elem[1], elem[0], depdict)
    return depdict

def build_dependencies_graph(depset, alphabet):
    depdict=translate_depset_to_depdict(depset, alphabet)
    G=[]
    for letter in alphabet:
        G.append(set(map(alphabet.index, depdict[letter])))
    return G

def does_path_exist(G, start, end):
    n=len(G)
    visited=[False for _ in range (n)]
    q=Queue()
    visited[start]=True
    q.put(start)
    while not q.empty():
        tmp=q.get()
        for i in G[tmp]:
            if i==end: return True
            if not visited[i]:
                visited[i]=True
                q.put(i)
    return False

def get_min_dep_graph(G):
    for i in range(len(G)):
        curr=G[i].copy()
        for v in curr:
            G[i].remove(v)
            if not does_path_exist(G, i, v):
                G[i].add(v)
    return G

def get_fnf(alphabet, G):
    start_points=set(range(len(G)))
    for v_edges in G:
        start_points-=v_edges
    #bfs without visited
    classes=[-1 for _ in range(len(G))]
    q=Queue()
    for point in start_points:
        classes[point]=0
        q.put(point)
    while not q.empty():
        tmp=q.get()
        for i in G[tmp]:
            classes[i]=classes[tmp]+1
            q.put(i)
    #end of bfs

    fnf_classes=[[] for _ in range(max(classes)+1)]
    for i, i_class in enumerate(classes):
        fnf_classes[i_class].append(alphabet[i])
    return tuple([tuple(sorted(elem)) for elem in fnf_classes]), classes

def render_min_dep_graph(min_dep_graph, alphabet, classes, filename='min_dep_graph.gv'):
    colors=list(elem[4:] for elem in mcolors.TABLEAU_COLORS.keys())
    graph = graphviz.Digraph()
    for i, elem in enumerate(alphabet):
        color=colors[classes[i]%len(colors)]
        graph.node(str(i), elem, color=color)
    for i, elem in enumerate(alphabet):
        for j in min_dep_graph[i]:
            graph.edge(str(i), str(j))
    graph.render("graphs/"+filename, view=True) 
    return graph

def save_classes_to_csv(matrix_size: int, filename: str):
    alphabet=get_alphabet(matrix_size)
    dep=get_dependencies(matrix_size)
    dep_graph=build_dependencies_graph(dep, alphabet)
    fnf, colors=get_fnf(alphabet, dep_graph)
    
    f = open(filename, 'w')
    writer = csv.writer(f)
    for f_class in fnf:
        writer.writerow(f_class)
    f.close()

def print_seq(seq, title=""):
    if len(seq)==0:
        return
    if title!="":
        print(title, end=": ")
    print(f"{seq[0]}", end="")
    for elem in seq[1:]:
        print(f", {elem}", end="")
    print("\n")

if __name__ == '__main__':
    filename = sys.argv[1] if len(sys.argv) > 1 else "examples/example1.txt"
    matrix=parse_file(filename)
    alphabet=get_alphabet(len(matrix))
    print_seq(sorted(alphabet), "Alfabet")
    print_seq(alphabet, "Algorytm sekwencyjny")
    dependencies=get_dependencies(len(matrix))
    print_seq(list(dependencies), "Zależności: ")
    dep_graph=build_dependencies_graph(dependencies, alphabet)
    fnf, colors=get_fnf(alphabet, dep_graph)
    print_seq(list(fnf), "FNF")
    min_dep_graph=get_min_dep_graph(dep_graph)
    rendered_graph=render_min_dep_graph(min_dep_graph, alphabet, colors, "min_dep_grapph_"+filename[:-4]+".gv")