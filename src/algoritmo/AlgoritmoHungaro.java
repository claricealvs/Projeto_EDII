package algoritmo;

import java.util.Arrays;

public class AlgoritmoHungaro {
    private int[][] matrizCusto;       // Matriz original/trabalho
    private int[][] matrizAtribuicao;  // 1 = casado, 0 = não casado
    private int numLinhas;             // 25

    private int[] linhasCobertas;
    private int[] colunasCobertas;
    private int[] starNoCol; // Mapeia qual linha tem a "estrela" (zero escolhido) na coluna
    private int[] primeNoLinha; // Mapeia zeros "primos" (candidatos)

    public AlgoritmoHungaro() {

    }

    public int resolver(int[][] matrizEntrada) {
        this.numLinhas = matrizEntrada.length;
        this.matrizCusto = copiarMatriz(matrizEntrada);
        this.matrizAtribuicao = new int[numLinhas][numLinhas];

        initArrays();

        // --- Passo 1 do PDF: Redução de Linhas e Colunas ---
        reduzirLinhas();
        reduzirColunas();

        // Encontrar emparelhamento inicial (marcar zeros estelares)
        marcarEstrelasIniciais();

        int passo = 2; // Começamos tentando cobrir os zeros
        while (passo != 7) { // 7 = FIM
            switch (passo) {
                case 2:
                    passo = cobrirZeros(); // Passo 2 e 3: Cobre e testa otimização
                    break;
                case 3:
                    passo = encontrarZeroNaoCoberto(); // Busca zeros para expandir o caminho
                    break;
                case 4:
                    passo = construirCaminhoAlternante(); // Manipula as atribuições
                    break;
                case 5:
                    passo = limparCoberturasEPrimos();
                    break;
                case 6:
                    passo = ajustarMatriz(); // Passo 4 do PDF: Ajuste com menor elemento
                    break;
            }
        }

        // Montar a matriz de atribuição final (0 ou 1) baseada nas "estrelas"
        montarMatrizAtribuicao();

        return calcularCustoTotal(matrizEntrada);
    }

    // Redução
    private void reduzirLinhas() {
        for (int i = 0; i < numLinhas; i++) {
            int min = Integer.MAX_VALUE;
            // Achar min
            for (int j = 0; j < numLinhas; j++) {
                if (matrizCusto[i][j] < min) min = matrizCusto[i][j];
            }
            // Subtrair min (cuidando para não mexer no MAX_VALUE)
            if (min > 0 && min != Integer.MAX_VALUE) {
                for (int j = 0; j < numLinhas; j++) {
                    if (matrizCusto[i][j] != Integer.MAX_VALUE) {
                        matrizCusto[i][j] -= min;
                    }
                }
            }
        }
    }

    private void reduzirColunas() {
        for (int j = 0; j < numLinhas; j++) {
            int min = Integer.MAX_VALUE;
            for (int i = 0; i < numLinhas; i++) {
                if (matrizCusto[i][j] < min) min = matrizCusto[i][j];
            }
            if (min > 0 && min != Integer.MAX_VALUE) {
                for (int i = 0; i < numLinhas; i++) {
                    if (matrizCusto[i][j] != Integer.MAX_VALUE) {
                        matrizCusto[i][j] -= min;
                    }
                }
            }
        }
    }

    // --- Auxiliar: Emparelhamento Inicial ---
    private void marcarEstrelasIniciais() {
        boolean[] linhaUsada = new boolean[numLinhas];
        boolean[] colUsada = new boolean[numLinhas];

        for (int i = 0; i < numLinhas; i++) {
            for (int j = 0; j < numLinhas; j++) {
                if (matrizCusto[i][j] == 0 && !linhaUsada[i] && !colUsada[j]) {
                    starNoCol[j] = i; // Estrela na posição (i, j)
                    linhaUsada[i] = true;
                    colUsada[j] = true;
                    break;
                }
            }
        }
    }

    // Cobrir Zeros e Teste de Otimização
    private int cobrirZeros() {
        int count = 0;
        for (int j = 0; j < numLinhas; j++) {
            if (starNoCol[j] != -1) {
                colunasCobertas[j] = 1; // Cobre coluna que tem estrela
                count++;
            }
        }

        // Se número de colunas cobertas = Dimensão, achamos a solução ótima!
        if (count >= numLinhas) {
            return 7; // FIM
        } else {
            return 3; // Vai procurar zeros não cobertos
        }
    }

    // Lógica auxiliar para encontrar zeros e criar caminhos (Step 4 padrão do Munkres)
    private int encontrarZeroNaoCoberto() {
        int linha = -1;
        int col = -1;
        boolean feito = false;

        while (!feito) {
            linha = -1;
            col = -1;

            // Procura zero não coberto
            for (int i = 0; i < numLinhas; i++) {
                if (linhasCobertas[i] == 0) {
                    for (int j = 0; j < numLinhas; j++) {
                        if (colunasCobertas[j] == 0 && matrizCusto[i][j] == 0) {
                            linha = i;
                            col = j;
                            feito = true;
                            break;
                        }
                    }
                }
                if (feito) break;
            }

            if (linha == -1) {
                return 6; // Não achou zero? Precisa ajustar a matriz (Passo 4 do PDF)
            }

            primeNoLinha[linha] = col; // Marca como zero primo

            // Se já existe uma estrela na linha do primo
            int starCol = encontrarEstrelaNaLinha(linha);
            if (starCol != -1) {
                linhasCobertas[linha] = 1;      // Cobre a linha
                colunasCobertas[starCol] = 0;   // Descobre a coluna da estrela
            } else {
                // Caminho de aumento encontrado
                caminhoLinha0 = linha;
                caminhoCol0 = col;
                return 4; // Construir caminho
            }
        }
        return 3;
    }

    // --- Passo 4 do PDF: Ajuste da Matriz ---
    private int ajustarMatriz() {
        int min = Integer.MAX_VALUE;

        // Encontrar o menor valor não coberto
        for (int i = 0; i < numLinhas; i++) {
            if (linhasCobertas[i] == 0) {
                for (int j = 0; j < numLinhas; j++) {
                    if (colunasCobertas[j] == 0) {
                        if (matrizCusto[i][j] < min) {
                            min = matrizCusto[i][j];
                        }
                    }
                }
            }
        }

        // Ajustar os valores
        for (int i = 0; i < numLinhas; i++) {
            for (int j = 0; j < numLinhas; j++) {
                if (matrizCusto[i][j] == Integer.MAX_VALUE) continue; // Pula infinitos

                if (linhasCobertas[i] == 1) {
                    matrizCusto[i][j] += min; // Adiciona na interseção (linha coberta)
                }
                if (colunasCobertas[j] == 0) {
                    matrizCusto[i][j] -= min; // Subtrai dos não cobertos
                }
            }
        }
        return 3; // Volta para procurar zeros
    }

    // Variáveis para construção do caminho (fases internas do algoritmo)
    private int caminhoLinha0, caminhoCol0;
    private int[][] caminho;

    private int construirCaminhoAlternante() {
        caminho = new int[numLinhas * 2][2];
        int count = 0;
        caminho[count][0] = caminhoLinha0;
        caminho[count][1] = caminhoCol0;

        boolean feito = false;
        while (!feito) {
            int starCol = encontrarEstrelaNaLinha(caminho[count][0]);
            // Nota: Na implementação padrão Munkres, buscamos estrela na COLUNA
            int col = caminho[count][1];
            int linha = starNoCol[col];

            if (linha != -1) {
                count++;
                caminho[count][0] = linha;
                caminho[count][1] = col;

                // Agora busca primo na linha
                int colPrimo = primeNoLinha[linha];
                count++;
                caminho[count][0] = linha;
                caminho[count][1] = colPrimo;
            } else {
                feito = true;
            }
        }

        // Flip nas estrelas
        for (int i = 0; i <= count; i++) {
            int l = caminho[i][0];
            int c = caminho[i][1];
            if (starNoCol[c] == l) {
                starNoCol[c] = -1;
            } else {
                starNoCol[c] = l;
            }
        }

        limparCoberturasEPrimos();
        return 5;
    }

    private int limparCoberturasEPrimos() {
        Arrays.fill(linhasCobertas, 0);
        Arrays.fill(colunasCobertas, 0);
        Arrays.fill(primeNoLinha, -1);
        return 2; // Volta para cobrir zeros
    }

    // --- Helpers ---
    private int encontrarEstrelaNaLinha(int linha) {
        for (int j = 0; j < numLinhas; j++) {
            if (starNoCol[j] == linha) return j;
        }
        return -1;
    }

    private void montarMatrizAtribuicao() {
        for (int i = 0; i < numLinhas; i++) {
            for (int j = 0; j < numLinhas; j++) {
                matrizAtribuicao[i][j] = 0;
            }
        }
        for (int j = 0; j < numLinhas; j++) {
            if (starNoCol[j] != -1) {
                matrizAtribuicao[starNoCol[j]][j] = 1;
            }
        }
    }

    private int calcularCustoTotal(int[][] original) {
        int total = 0;
        for (int i = 0; i < numLinhas; i++) {
            for (int j = 0; j < numLinhas; j++) {
                if (matrizAtribuicao[i][j] == 1) {
                    // Se foi atribuído, mas o custo era "Infinito" (proibido), algo deu errado
                    if (original[i][j] == Integer.MAX_VALUE) return -1; // Erro ou Impossível
                    total += original[i][j];
                }
            }
        }
        return total;
    }

    private void initArrays() {
        linhasCobertas = new int[numLinhas];
        colunasCobertas = new int[numLinhas];
        starNoCol = new int[numLinhas];
        Arrays.fill(starNoCol, -1);
        primeNoLinha = new int[numLinhas];
        Arrays.fill(primeNoLinha, -1);
    }

    private int[][] copiarMatriz(int[][] original) {
        int[][] copia = new int[original.length][original[0].length];
        for (int i = 0; i < original.length; i++) {
            System.arraycopy(original[i], 0, copia[i], 0, original[i].length);
        }
        return copia;
    }

    public int[][] getMatrizAtribuicao() {
        return matrizAtribuicao;
    }
}
