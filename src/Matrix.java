


public class Matrix {
    private final int M;             // number of rows
    private final int N;             // number of columns
    private final double[][] data;   // M-by-N array

    // create M-by-N matrix of 0's
    public Matrix(int M, int N) {
        this.M = M;
        this.N = N;
        data = new double[M][N];
    }

    // create matrix based on 2d array
    public Matrix(double[][] data) {
        M = data.length;
        N = data[0].length;
        this.data = new double[M][N];
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                    this.data[i][j] = data[i][j];
    }
    
    /**
     * Complete copy the content of the matrix
     * @return a copy (by value) of the matrix
     */
    public Matrix copyMatrix()
    {
   	 Matrix B = new Matrix(this.M, this.N);
   	 for(int i=0; i<M; i++)
   	 {
   		 for(int j=0; j<N; j++)
   		 {
   			 B.setValue(i, j, this.getValue(i, j));
   		 }
   	 }
   	 return B;
    }

    // copy constructor
    private Matrix(Matrix A) { this(A.data); }
    
    // create and return a random M-by-N matrix with values between 0 and 1
    public static Matrix random(int M, int N) {
        Matrix A = new Matrix(M, N);
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                A.data[i][j] = Math.random();
        return A;
    }

    // create and return the N-by-N identity matrix
    public static Matrix identity(int N) {
        Matrix I = new Matrix(N, N);
        for (int i = 0; i < N; i++)
            I.data[i][i] = 1;
        return I;
    }

    // swap rows i and j
    private void swap(int i, int j) {
        double[] temp = data[i];
        data[i] = data[j];
        data[j] = temp;
    }
    
    /**
     * Set the value of specific row-column position
     * @param row - row index of target
     * @param col - column index of target
     * @param value - value be set to the position
     */
    public void setValue(int row, int col, double value)
    {
   	 data[row][col] = value;
    }
    
    /**
     * Multiply the matrix with a scalar, and return a new matrix
     * @param scalar - scalar for multiply
     * @return new matrix multiplied by scalar
     */
    public Matrix times(double scalar)
    {
   	 Matrix B = copyMatrix();
   	 for(int i=0; i<M; i++)
   	 {
   		 for(int j=0; j<N; j++)
   		 {
   			 B.setValue(i, j, B.getValue(i, j)*scalar);
   		 }
   	 }   	 
   	 return B;
    }
    /**
 	 * Perform the column normalization for the specific matrix
 	 * @param originalMatrix - original matrix to be normalized
 	 */
    public void columnNormalize()
 	 {
   	 double sum = 0.0;
   	 for(int j=0; j< N; j++)
   	 {
   		 sum = 0.0;
   		 for(int i=0; i< M; i++)
   		 {
   			 sum += getValue(i, j);
   		 }
   		 // normalize with sum   		 
   		 for(int i=0; i< M; i++)
   		 {
   			 if(sum != 0.0)
   			 {
   				 setValue(i, j, getValue(i, j)/sum); 
   			 }
   			 else
   			 {
   				setValue(i, j, 0.0); 
   			 }
   		 }
   	 }
 	 }
    
    /**
     * Return the value of specific position of matrix
     * @param row - row index of target
     * @param col - column index of target
     * @return the value of target position in the matrix
     */
    public double getValue(int row, int col)
    {
   	 return data[row][col];
    }
    
    /**
     * Get the norm of this matrix
     * @return norm of the matrix, -1 if not a vector. 
     */
    public double getNorm()
    {
   	 double sum = 0.0;
   	 if(N != 1 && M != 1)
   	 {
   		 System.out.println("<Error>\t not a vector, couldn't calculate norm");
   		 return -1;
   	 }
   	 if(N == 1)
   	 {
   		 // column vector
   		 for(int i=0;i<M;i++)
   		 {
   			 sum += Math.pow(data[i][0], 2);
   		 }   		 
   	 }
   	 else
   	 {
   		 // row vector
   		 for(int j=0;j<N;j++)
   		 {
   			 sum += Math.pow(data[0][j], 2);
   		 }   		
   	 }
   	 return Math.sqrt(sum);
    }
    /**
     * Get the size of matrix (M*N)
     * @return the size (number of elements) of matrix
     */
    public int getSize()
    {
   	 return M*N;
    }
    // create and return the transpose of the invoking matrix
    public Matrix transpose() {
        Matrix A = new Matrix(N, M);
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                A.data[j][i] = this.data[i][j];
        return A;
    }

    // return C = A + B
    public Matrix plus(Matrix B) {
        Matrix A = this;
        if (B.M != A.M || B.N != A.N) throw new RuntimeException("Illegal matrix dimensions.");
        Matrix C = new Matrix(M, N);
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                C.data[i][j] = A.data[i][j] + B.data[i][j];
        return C;
    }


    // return C = A - B
    public Matrix minus(Matrix B) {
        Matrix A = this;
        if (B.M != A.M || B.N != A.N) throw new RuntimeException("Illegal matrix dimensions.");
        Matrix C = new Matrix(M, N);
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                C.data[i][j] = A.data[i][j] - B.data[i][j];
        return C;
    }

    // does A = B exactly?
    public boolean eq(Matrix B) {
        Matrix A = this;
        if (B.M != A.M || B.N != A.N) throw new RuntimeException("Illegal matrix dimensions.");
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                if (A.data[i][j] != B.data[i][j]) return false;
        return true;
    }

    // return C = A * B
    public Matrix times(Matrix B) {
   	 //System.out.println("multiply happen");
   	 //B.show();
   	 
        Matrix A = this;
        if (A.N != B.M) throw new RuntimeException("Illegal matrix dimensions.");
        Matrix C = new Matrix(A.M, B.N);
        for (int i = 0; i < C.M; i++)
            for (int j = 0; j < C.N; j++)
                for (int k = 0; k < A.N; k++)
                    C.data[i][j] += (A.data[i][k] * B.data[k][j]);
        return C;
    }

    // return x = A^-1 b, assuming A is square and has full rank
    public Matrix solve(Matrix rhs) {
        if (M != N || rhs.M != N || rhs.N != 1)
            throw new RuntimeException("Illegal matrix dimensions.");

        // create copies of the data
        Matrix A = new Matrix(this);
        Matrix b = new Matrix(rhs);

        // Gaussian elimination with partial pivoting
        for (int i = 0; i < N; i++) {

            // find pivot row and swap
            int max = i;
            for (int j = i + 1; j < N; j++)
                if (Math.abs(A.data[j][i]) > Math.abs(A.data[max][i]))
                    max = j;
            A.swap(i, max);
            b.swap(i, max);

            // singular
            if (A.data[i][i] == 0.0) throw new RuntimeException("Matrix is singular.");

            // pivot within b
            for (int j = i + 1; j < N; j++)
                b.data[j][0] -= b.data[i][0] * A.data[j][i] / A.data[i][i];

            // pivot within A
            for (int j = i + 1; j < N; j++) {
                double m = A.data[j][i] / A.data[i][i];
                for (int k = i+1; k < N; k++) {
                    A.data[j][k] -= A.data[i][k] * m;
                }
                A.data[j][i] = 0.0;
            }
        }

        // back substitution
        Matrix x = new Matrix(N, 1);
        for (int j = N - 1; j >= 0; j--) {
            double t = 0.0;
            for (int k = j + 1; k < N; k++)
                t += A.data[j][k] * x.data[k][0];
            x.data[j][0] = (b.data[j][0] - t) / A.data[j][j];
        }
        return x;
   
    }

    // print matrix to standard output
    public void show() {
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) 
                System.out.printf("%15.10f ", data[i][j]);
            System.out.println();
        }
    }



    // test client
    public static void main(String[] args) {
        double[][] d = { { 1, 2, 3 }, { 4, 5, 6 }, { 9, 1, 3} };
        Matrix D = new Matrix(d);
        D.show();        
        System.out.println();

        Matrix A = Matrix.random(5, 5);
        A.show(); 
        System.out.println();

        A.swap(1, 2);
        A.show(); 
        System.out.println();

        Matrix B = A.transpose();
        B.show(); 
        System.out.println();

        Matrix C = Matrix.identity(5);
        C.show(); 
        System.out.println();

        A.plus(B).show();
        System.out.println();

        B.times(A).show();
        System.out.println();

        // shouldn't be equal since AB != BA in general    
        System.out.println(A.times(B).eq(B.times(A)));
        System.out.println();

        Matrix b = Matrix.random(5, 1);
        b.show();
        System.out.println();

        Matrix x = A.solve(b);
        x.show();
        System.out.println();

        A.times(x).show();
        
    }
}