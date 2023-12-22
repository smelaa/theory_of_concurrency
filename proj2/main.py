import sys
import string
import graphviz
from queue import Queue

def parse_file(filename):
    try:
        file = open(filename, "r")
    except IOError:
        print("Cannot open {0} file".format(filename))
        sys.exit(0)
    alphabet=file.readline()[:-1]
    read=dict()
    written=dict()
    for i in range (len(alphabet)):
        line=file.readline()[:-1]
        action=line[1]
        if not action in alphabet:
            raise Exception(f"{action} (line {i+1}) not in the alphabet")
        read[action]=set()
        written[action]=set()
        curr=written
        for elem in line[3:]:
            if elem=='=': 
                curr=read
            elif elem in string.ascii_lowercase:
                curr[action].add(elem)
    word=file.readline()
    if len(alphabet)==0 or len(word)==0:
        raise Exception(f"Wrong example file. Alphabet and word cannot be empty.")
    file.close()
    return alphabet, read, written, word

def find_dependencies(alphabet, read, written):
    dependencies=set()
    independencies=set()
    for i, elem in enumerate(alphabet):
        others=alphabet[i+1:]
        dependencies.add((elem, elem))
        for other in others:
            if written[elem].intersection(written[other]) or written[elem].intersection(read[other]) or read[elem].intersection(written[other]):
                dependencies.add((elem, other))
            else:
                independencies.add((elem, other))
    return dependencies, independencies

def build_dependencies_graph(word, written, read):
    G=[]
    for i, elem in enumerate(word):
        edges=set()
        for j, other in enumerate(word[i+1:]):
            if written[elem].intersection(written[other]) or written[elem].intersection(read[other]) or read[elem].intersection(written[other]):
                edges.add(i+j+1)
        G.append(edges)
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

def get_fnf(word, G):
    #bfs without visited
    classes=[-1 for _ in range(len(G))]
    q=Queue()
    classes[0]=0
    q.put(0)
    while not q.empty():
        tmp=q.get()
        for i in G[tmp]:
            classes[i]=classes[tmp]+1
            q.put(i)
    #end of bfs

    fnf_classes=[[] for _ in range(max(classes)+1)]
    for i, i_class in enumerate(classes):
        fnf_classes[i_class].append(word[i])
    return tuple([tuple(sorted(elem)) for elem in fnf_classes])

def render_min_dep_graph(min_dep_graph):
    graph = graphviz.Digraph()
    for i, elem in enumerate(word):
        graph.node(str(i), elem)
    edges=[]
    for i, elem in enumerate(word):
        for j in min_dep_graph[i]:
            edges.append(str(i)+str(j))
    graph.edges(edges)
    graph.render('min_dep_graph.gv', view=True) 
    return graph

if __name__ == '__main__':
    alphabet, read, written, word=parse_file(sys.argv[1] if len(sys.argv) > 1 else "example1.txt")
    dependencies, independencies=find_dependencies(alphabet, read, written)
    print(f"D = {dependencies}")
    print(f"I = {independencies}")
    dep_graph=build_dependencies_graph(word, written, read)
    fnf=get_fnf(word, dep_graph)
    print(f"FNF('{word}') = {fnf}")
    min_dep_graph=get_min_dep_graph(dep_graph)
    rendered_graph=render_min_dep_graph(min_dep_graph)
    