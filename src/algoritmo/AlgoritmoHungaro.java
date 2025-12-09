package Algoritmo;
import java.util.Arrays;
public class AlgoritmoHungaro {

        private int n; // dimensão da matriz

        private int[][] custo;     // matriz de trabalho
        private int[][] marcas;    // 1 = estrela, 2 = primo
        private boolean[] linhaCoberta;
        private boolean[] colunaCoberta;

        private int zeroPrimeLinha = -1;
        private int zeroPrimeColuna = -1;

        public int[][] resolver(int[][] matrizCusto) {

            this.n = matrizCusto.length;

            // Copiar matriz
            this.custo = new int[n][n];
            for (int i = 0; i < n; i++) {
                custo[i] = Arrays.copyOf(matrizCusto[i], n);
            }

            marcas = new int[n][n];
            linhaCoberta = new boolean[n];
            colunaCoberta = new boolean[n];

            // Passo 1
            reduzirLinhas();
            reduzirColunas();

            // Passo 2: marcar zeros iniciais
            marcarZerosIniciais();
            cobrirColunasComEstrelas();

            // Loop principal
            while (!todasColunasCobertas()) {

                int[] posZero = encontrarZeroNaoCoberto();

                if (posZero == null) {
                    // Passo 4: ajustar matriz
                    ajustarMatriz();
                } else {

                    int i = posZero[0];
                    int j = posZero[1];

                    marcas[i][j] = 2; // marca como "primo"

                    int estrelaNaLinha = encontrarEstrelaNaLinha(i);

                    if (estrelaNaLinha == -1) {
                        // Construir caminho alternante
                        construirCaminhoAlternante(i, j);

                        limparCoberturasEPrimos();
                        cobrirColunasComEstrelas();
                    } else {
                        linhaCoberta[i] = true;
                        colunaCoberta[estrelaNaLinha] = false;
                    }
                }
            }

            // Construir matriz final de resultado
            int[][] resultado = new int[n][n];

            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    if (marcas[i][j] == 1)
                        resultado[i][j] = 1;

            return resultado;
        }

        private void reduzirLinhas() {
            for (int i = 0; i < n; i++) {
                int menor = Arrays.stream(custo[i]).min().getAsInt();
                for (int j = 0; j < n; j++)
                    custo[i][j] -= menor;
            }
        }

        private void reduzirColunas() {
            for (int j = 0; j < n; j++) {
                int menor = Integer.MAX_VALUE;

                for (int i = 0; i < n; i++)
                    menor = Math.min(menor, custo[i][j]);

                for (int i = 0; i < n; i++)
                    custo[i][j] -= menor;
            }
        }

        private void marcarZerosIniciais() {
            boolean[] linhaUsada = new boolean[n];
            boolean[] colunaUsada = new boolean[n];

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (custo[i][j] == 0 && !linhaUsada[i] && !colunaUsada[j]) {
                        marcas[i][j] = 1; // estrela
                        linhaUsada[i] = true;
                        colunaUsada[j] = true;
                    }
                }
            }
        }

        private void cobrirColunasComEstrelas() {
            Arrays.fill(colunaCoberta, false);

            for (int j = 0; j < n; j++) {
                for (int i = 0; i < n; i++) {
                    if (marcas[i][j] == 1) {
                        colunaCoberta[j] = true;
                        break;
                    }
                }
            }
        }

        private boolean todasColunasCobertas() {
            for (boolean c : colunaCoberta)
                if (!c) return false;
            return true;
        }

        private int[] encontrarZeroNaoCoberto() {
            for (int i = 0; i < n; i++) {
                if (!linhaCoberta[i]) {
                    for (int j = 0; j < n; j++) {
                        if (!colunaCoberta[j] && custo[i][j] == 0) {
                            return new int[]{i, j};
                        }
                    }
                }
            }
            return null;
        }

        private int encontrarEstrelaNaLinha(int linha) {
            for (int j = 0; j < n; j++)
                if (marcas[linha][j] == 1)
                    return j;
            return -1;
        }

        private int encontrarEstrelaNaColuna(int coluna) {
            for (int i = 0; i < n; i++)
                if (marcas[i][coluna] == 1)
                    return i;
            return -1;
        }

        private int encontrarPrimoNaLinha(int linha) {
            for (int j = 0; j < n; j++)
                if (marcas[linha][j] == 2)
                    return j;
            return -1;
        }

        private void construirCaminhoAlternante(int linha, int coluna) {

            int[][] caminho = new int[n * 2][2]; // tamanho máximo possível
            int k = 0;

            caminho[k][0] = linha;
            caminho[k][1] = coluna;
            k++;

            boolean continuar = true;

            while (continuar) {
                int linhaEstrela = encontrarEstrelaNaColuna(caminho[k - 1][1]);

                if (linhaEstrela != -1) {
                    caminho[k][0] = linhaEstrela;
                    caminho[k][1] = caminho[k - 1][1];
                    k++;
                } else {
                    continuar = false;
                    break;
                }

                int colunaPrimo = encontrarPrimoNaLinha(caminho[k - 1][0]);

                caminho[k][0] = caminho[k - 1][0];
                caminho[k][1] = colunaPrimo;
                k++;
            }

            // Trocar estrelas/primos no caminho
            for (int i = 0; i < k; i++) {
                int r = caminho[i][0];
                int c = caminho[i][1];

                if (marcas[r][c] == 1)
                    marcas[r][c] = 0;
                else if (marcas[r][c] == 2)
                    marcas[r][c] = 1;
            }
        }

        private void ajustarMatriz() {
            int menor = Integer.MAX_VALUE;

            for (int i = 0; i < n; i++) {
                if (!linhaCoberta[i]) {
                    for (int j = 0; j < n; j++) {
                        if (!colunaCoberta[j]) {
                            menor = Math.min(menor, custo[i][j]);
                        }
                    }
                }
            }

            // Aplicar ajustes
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {

                    if (!linhaCoberta[i] && !colunaCoberta[j])
                        custo[i][j] -= menor;

                    else if (linhaCoberta[i] && colunaCoberta[j])
                        custo[i][j] += menor;
                }
            }
        }

        private void limparCoberturasEPrimos() {
            Arrays.fill(linhaCoberta, false);
            Arrays.fill(colunaCoberta, false);

            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    if (marcas[i][j] == 2)
                        marcas[i][j] = 0;
        }
    }

}
