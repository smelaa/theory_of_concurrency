import sys
from graph_generation import save_classes_to_csv

matrix_size = int(sys.argv[1]) 
filename = sys.argv[2] 
save_classes_to_csv(matrix_size, filename)