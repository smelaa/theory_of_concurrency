import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.lang.ProcessBuilder;
import java.lang.Process;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ConcurrentHashMap;

public class GaussianElimination {
    private static Float[][] parse_file(String fileName) {
        //Parsing input file with matrix data
        File matrixFile = new File(fileName);
        List<Float[]> rows = new ArrayList<>();
        try {
            Scanner reader = new Scanner(matrixFile);
            int row_n = -1;
            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                String[] data_split = data.split("[,/]");
                Float[] row = Arrays.stream(data_split).map(Float::parseFloat).toArray(Float[]::new);
                if (row_n == -1) {
                    row_n = row.length;
                } else if (row_n != row.length) {
                    System.out.println("Wrong input. Each row needs to have the same number of elements.");
                    System.exit(1);
                }
                rows.add(row);
            }
            reader.close();
            if (row_n != rows.size() + 1) {
                System.out.println("Wrong input. Matrix size should be N x (N+1).elements.");
                System.exit(1);
            }
        } catch (FileNotFoundException exception) {
            System.out.println("Cannot find file " + fileName + ".\n");
            System.exit(1);
        }
        Float[][] matrix = rows.stream().toArray(Float[][]::new);
        return matrix;
    }

    private static void runScriptToGetClasses(int matSize, String fileToSave) {
        //Running Python script to get FNF saved to a temporary csv file
        try {
            Process p = new ProcessBuilder("python.exe", "get_classes.py", String.valueOf(matSize), fileToSave).start();
            p.waitFor();
        } catch (Exception e) {
            System.out.println("Cannot run Python script.\n");
            e.printStackTrace(System.out);
            System.exit(1);
        }
    }

    private static List<List<Action>> renderClasses(String fileName, Matrix matrix) {
        //Parsing FNF from temporary csv file
        File file = new File(fileName);
        List<List<Action>> rows = new ArrayList<>();
        try {
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                if (data.length() != 0) {
                    String[] data_split = data.split(",");
                    List<Action> row = Arrays.stream(data_split).map(elem -> new Action(elem, matrix)).toList();
                    rows.add(row);
                }
            }
            reader.close();
        } catch (FileNotFoundException exception) {
            System.out.println("Cannot find file " + fileName + ".\n");
            System.exit(1);
        }
        return rows;
    }

    public static void main(String args[]) {
        //Render file with input
        String fileName = "examples/example1.txt";
        if (args.length != 0) {
            fileName = args[0];
        }
        Matrix mat = new Matrix(parse_file(fileName));

        //Run Python script to get order of operations
        String classesFile = "tmp_classes.csv";
        runScriptToGetClasses(mat.getSize(), classesFile);
        mat.addClasses(renderClasses(classesFile, mat));
        File file = new File(classesFile); 
        file.delete();

        //Print matrix before algorithm
        System.out.println("Before Gaussian elimination:\n");
        mat.print();

        //Gaussian elimination
        try {
            mat.gaussian_elimination();
        } catch (InterruptedException e) {
            System.out.println("Exception while running Gaussian elimination.\n");
            e.printStackTrace(System.out);
            System.exit(1);
        }

        //Print result
        System.out.println("After Gaussian elimination:\n");
        mat.print();
    }
}

class Action extends Thread {
    enum Act {
        A,
        B,
        C
    }

    private Matrix matrix;
    private final Act action;
    private final int i;
    private final int j;
    private final int k;
    private CountDownLatch latch = new CountDownLatch(1);

    public Action(String to_parse, Matrix mat) {
        matrix = mat;
        this.setName(to_parse);
        String[] to_parse_split = to_parse.split("_");
        if (to_parse_split[0].equals("A")) {
            action = Act.A;
            i = Integer.parseInt(to_parse_split[1]);
            k = Integer.parseInt(to_parse_split[2]);
            j = -1;
        } else {
            i = Integer.parseInt(to_parse_split[1]);
            j = Integer.parseInt(to_parse_split[2]);
            k = Integer.parseInt(to_parse_split[3]);
            if (to_parse_split[0].equals("B")) {
                action = Act.B;
            } else {
                action = Act.C;
            }
        }
    }

    public void assignLatch(CountDownLatch newLatch) {
        latch = newLatch;
    }

    public void run() {
        if (action == Act.A) {
            matrix.doA(i, k);
        } else if (action == Act.B) {
            matrix.doB(i, j, k);
        } else {
            matrix.doC(i, j, k);
        }
        latch.countDown();
    }

    public String str() {
        switch (action) {
            case Act.A: {
                return "A" + String.valueOf(i) + String.valueOf(k);
            }
            case Act.B: {
                return "B" + String.valueOf(i) + String.valueOf(j) + String.valueOf(k);
            }
            case Act.C: {
                return "C" + String.valueOf(i) + String.valueOf(j) + String.valueOf(k);
            }
        }
        return "";
    }
}

class Matrix {
    private Float[][] matrix;
    private Map<String, Float> workspace = new ConcurrentHashMap<>();
    private int size;

    private List<List<Action>> classes;

    public Matrix(Float[][] mat) {
        matrix = mat;
        size = matrix.length;
    }

    public void addClasses(List<List<Action>> c) {
        classes = c;
    }

    public int getSize() {
        return size;
    }

    public void gaussian_elimination() throws InterruptedException {
        for (List<Action> cl : classes) {
            CountDownLatch latch = new CountDownLatch(cl.size());
            for (Action act : cl) {
                act.assignLatch(latch);
                act.start();
            }
            latch.await();
        }
    }

    public void print() {
        for (Float[] row : matrix) {
            System.out.print("| ");
            for (Float elem : row) {
                System.out.print(elem + " | ");
            }
            System.out.print("\n");
        }
        System.err.print("\n");
    }

    public void doA(int i, int k) {
        Float m = matrix[k - 1][i - 1] / matrix[i - 1][i - 1];
        workspace.put(m(k, i), m);
    }

    public void doB(int i, int j, int k) {
        float m = workspace.get(m(k, i));
        float n = matrix[i - 1][j - 1] * m;
        workspace.put(n(k, i, j), n);
    }

    public void doC(int i, int j, int k) {
        float n = workspace.get(n(k, i, j));
        matrix[k - 1][j - 1] -= n;
    }

    private static String m(int k, int i) {
        return "m_" + String.valueOf(k) + "_" + String.valueOf(i);
    }

    private static String n(int k, int i, int j) {
        return "n_" + String.valueOf(k) + "_" + String.valueOf(i) + "_" + String.valueOf(j);
    }

}