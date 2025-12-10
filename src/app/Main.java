package app;

import grafo.DadosDoProjeto;
import grafo.GrafoBipartido;
import algoritmo.AlgoritmoHungaro;
import model.Professor;
import model.Disciplina;
import model.Slot;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println(" SISTEMA DE ALOCAÇÃO DE PROFESSORES");
        System.out.println("   ALGORITMO HÚNGARO - OTIMIZAÇÃO");
        System.out.println("=========================================\n");

        // Etapa 1: Carregar dados do projeto
        System.out.println("ETAPA 1: CARREGANDO DADOS DO PROJETO");
        System.out.println("-----------------------------------------");
        DadosDoProjeto dados = new DadosDoProjeto();

        List<Professor> professores = dados.getProfessores();
        List<Disciplina> disciplinas = dados.getDisciplinas();

        System.out.println("Professores carregados: " + professores.size());
        System.out.println("Disciplinas carregadas: " + disciplinas.size());

        // Exibir professores e suas capacidades
        System.out.println("\n--- PROFESSORES E SUAS CAPACIDADES ---");
        for (Professor prof : professores) {
            System.out.printf("- %s: capacidade máxima de %d disciplina(s)%n",
                    prof.getNome(), prof.getCapacidadeMaxima());
        }

        // Exibir disciplinas e suas vagas
        System.out.println("\n--- DISCIPLINAS E SUAS VAGAS ---");
        for (Disciplina disc : disciplinas) {
            System.out.printf("- %s: %d vaga(s)%n",
                    disc.getNome(), disc.getVagas());
        }

        // Exibir preferências dos professores
        System.out.println("\n--- PREFERÊNCIAS DOS PROFESSORES ---");
        for (Professor prof : professores) {
            System.out.printf("\nProfessor: %s%n", prof.getNome());
            Map<String, Integer> preferencias = prof.getPreferencias();
            if (preferencias.isEmpty()) {
                System.out.println("  (Sem preferências registradas)");
            } else {
                for (Map.Entry<String, Integer> pref : preferencias.entrySet()) {
                    System.out.printf("  - %s: nível %d%n", pref.getKey(), pref.getValue());
                }
            }
        }

        // Aguardar confirmação do usuário
        System.out.print("\nPressione ENTER para continuar com a construção do grafo bipartido...");
        scanner.nextLine();

        // Etapa 2: Construir grafo bipartido
        System.out.println("\n\nETAPA 2: CONSTRUINDO GRAFO BIPARTIDO");
        System.out.println("-----------------------------------------");
        GrafoBipartido grafo = new GrafoBipartido(professores, disciplinas);

        System.out.println("Slots de professores criados: " + grafo.slotsProfessores.size());
        System.out.println("Slots de disciplinas criados: " + grafo.slotsDisciplinas.size());

        // Exibir alguns slots de exemplo
        System.out.println("\n--- EXEMPLO DE SLOTS CRIADOS ---");
        System.out.println("3 primeiros slots de professores:");
        for (int i = 0; i < Math.min(3, grafo.slotsProfessores.size()); i++) {
            Slot slot = grafo.slotsProfessores.get(i);
            System.out.printf("  %s (origem: %s, índice: %d)%n",
                    slot.getIdUnico(), slot.getNomeOrigem(), slot.getIndice());
        }

        System.out.println("\n3 primeiros slots de disciplinas:");
        for (int i = 0; i < Math.min(3, grafo.slotsDisciplinas.size()); i++) {
            Slot slot = grafo.slotsDisciplinas.get(i);
            System.out.printf("  %s (origem: %s, índice: %d)%n",
                    slot.getIdUnico(), slot.getNomeOrigem(), slot.getIndice());
        }

        // Aguardar confirmação do usuário
        System.out.print("\nPressione ENTER para gerar a matriz de custos...");
        scanner.nextLine();

        // Etapa 3: Gerar matriz de custos
        System.out.println("\n\nETAPA 3: GERANDO MATRIZ DE CUSTOS");
        System.out.println("-----------------------------------------");
        int[][] matrizCusto = grafo.gerarMatrizCusto();

        System.out.println("Matriz de custos gerada: " + matrizCusto.length + "x" + matrizCusto[0].length);

        // Exibir parte da matriz de custos (5x5 para não sobrecarregar o console)
        System.out.println("\n--- VISUALIZAÇÃO PARCIAL DA MATRIZ (5x5) ---");
        System.out.println("Legenda: número = custo, INF = impossível, 0 = sem custo");
        System.out.println();

        for (int i = 0; i < Math.min(5, matrizCusto.length); i++) {
            System.out.printf("Linha %2d (Prof slot %d): ", i, i);
            for (int j = 0; j < Math.min(5, matrizCusto[i].length); j++) {
                if (matrizCusto[i][j] == Integer.MAX_VALUE) {
                    System.out.print(" INF");
                } else {
                    System.out.printf("%4d", matrizCusto[i][j]);
                }
            }
            if (matrizCusto[i].length > 5) {
                System.out.print(" ...");
            }
            System.out.println();
        }
        if (matrizCusto.length > 5) {
            System.out.println(" ...");
        }

        // Explicação dos valores
        System.out.println("\n--- EXPLICAÇÃO DOS VALORES NA MATRIZ ---");
        System.out.println("Custo = (C_MAX - preferência)");
        System.out.println("Onde C_MAX = 5 (nota máxima para inversão)");
        System.out.println("Exemplo: preferência 5 → custo 0 (melhor)");
        System.out.println("         preferência 1 → custo 4 (pior)");
        System.out.println("         INF → impossível (professor não tem preferência pela disciplina)");

        // Aguardar confirmação do usuário
        System.out.print("\nPressione ENTER para executar o Algoritmo Húngaro...");
        scanner.nextLine();

        // Etapa 4: Executar Algoritmo Húngaro
        System.out.println("\n\nETAPA 4: EXECUTANDO ALGORITMO HÚNGARO");
        System.out.println("-----------------------------------------");

        AlgoritmoHungaro algoritmo = new AlgoritmoHungaro();
        System.out.println("Iniciando otimização...");

        long startTime = System.currentTimeMillis();
        int custoTotal = algoritmo.resolver(matrizCusto);
        long endTime = System.currentTimeMillis();

        System.out.println("Algoritmo concluído!");
        System.out.printf("Tempo de execução: %.2f segundos%n", (endTime - startTime) / 1000.0);

        if (custoTotal == -1) {
            System.out.println("\nERRO: Foi encontrada uma atribuição impossível!");
            System.out.println("Verifique as preferências dos professores.");
            scanner.close();
            return;
        }

        System.out.printf("Custo total da alocação ótima: %d%n", custoTotal);

        // Aguardar confirmação do usuário
        System.out.print("\nPressione ENTER para ver os resultados detalhados...");
        scanner.nextLine();

        // Etapa 5: Exibir resultados
        System.out.println("\n\nETAPA 5: RESULTADOS DA ALOCAÇÃO");
        System.out.println("-----------------------------------------");

        int[][] matrizAtribuicao = algoritmo.getMatrizAtribuicao();

        // Mapear slots para nomes reais
        System.out.println("\n--- ALOCAÇÕES REALIZADAS ---");

        int alocacoesValidas = 0;
        int alocacoesFicticias = 0;

        for (int i = 0; i < matrizAtribuicao.length; i++) {
            for (int j = 0; j < matrizAtribuicao[i].length; j++) {
                if (matrizAtribuicao[i][j] == 1) {
                    Slot slotProf = grafo.slotsProfessores.get(i);
                    Slot slotDisc = grafo.slotsDisciplinas.get(j);

                    String nomeProf = slotProf.getNomeOrigem();
                    String nomeDisc = slotDisc.getNomeOrigem();

                    // Ignorar alocações fictícias
                    if (nomeProf.equals("SEM_PROFESSOR") || nomeDisc.equals("FIC_SOBRA")) {
                        alocacoesFicticias++;
                        continue;
                    }

                    alocacoesValidas++;

                    // Calcular preferência original
                    Professor professor = null;
                    int preferencia = 0;
                    for (Professor p : professores) {
                        if (p.getNome().equals(nomeProf)) {
                            professor = p;
                            break;
                        }
                    }

                    if (professor != null) {
                        Integer pref = professor.getPreferencias().get(nomeDisc);
                        if (pref != null) {
                            preferencia = pref;
                        }
                    }

                    System.out.printf("✓ %s → %s (Preferência: %d, Custo: %d)%n",
                            nomeProf, nomeDisc, preferencia, matrizCusto[i][j]);
                }
            }
        }

        System.out.println("\n--- RESUMO ---");
        System.out.printf("Total de alocações válidas: %d%n", alocacoesValidas);
        System.out.printf("Slots fictícios utilizados: %d%n", alocacoesFicticias);
        System.out.printf("Custo total da alocação: %d%n", custoTotal);

        // Exibir professores sem alocação
        System.out.println("\n--- PROFESSORES NÃO ALOCADOS (ou alocados parcialmente) ---");
        for (Professor prof : professores) {
            int capacidade = prof.getCapacidadeMaxima();
            int alocado = 0;

            for (int i = 0; i < matrizAtribuicao.length; i++) {
                Slot slotProf = grafo.slotsProfessores.get(i);
                if (slotProf.getNomeOrigem().equals(prof.getNome())) {
                    for (int j = 0; j < matrizAtribuicao[i].length; j++) {
                        if (matrizAtribuicao[i][j] == 1) {
                            Slot slotDisc = grafo.slotsDisciplinas.get(j);
                            if (!slotDisc.getNomeOrigem().equals("FIC_SOBRA")) {
                                alocado++;
                            }
                        }
                    }
                }
            }

            if (alocado < capacidade) {
                System.out.printf("- %s: alocado %d de %d disciplina(s)%n",
                        prof.getNome(), alocado, capacidade);
            }
        }

        // Exibir disciplinas não preenchidas
        System.out.println("\n--- DISCIPLINAS NÃO PREENCHIDAS ---");
        for (Disciplina disc : disciplinas) {
            int vagas = disc.getVagas();
            int preenchidas = 0;

            for (int j = 0; j < matrizAtribuicao[0].length; j++) {
                Slot slotDisc = grafo.slotsDisciplinas.get(j);
                if (slotDisc.getNomeOrigem().equals(disc.getNome())) {
                    for (int i = 0; i < matrizAtribuicao.length; i++) {
                        if (matrizAtribuicao[i][j] == 1) {
                            Slot slotProf = grafo.slotsProfessores.get(i);
                            if (!slotProf.getNomeOrigem().equals("SEM_PROFESSOR")) {
                                preenchidas++;
                            }
                        }
                    }
                }
            }

            if (preenchidas < vagas) {
                System.out.printf("- %s: preenchida %d de %d vaga(s)%n",
                        disc.getNome(), preenchidas, vagas);
            }
        }

        // Exibir matriz de atribuição simplificada
        System.out.print("\nDeseja ver a matriz de atribuição completa? (s/n): ");
        String resposta = scanner.nextLine();

        if (resposta.equalsIgnoreCase("s")) {
            System.out.println("\n--- MATRIZ DE ATRIBUIÇÃO COMPLETA (25x25) ---");
            System.out.println("1 = atribuído, 0 = não atribuído");
            System.out.println();

            // Cabeçalho das colunas (disciplinas)
            System.out.print("      ");
            for (int j = 0; j < Math.min(10, matrizAtribuicao[0].length); j++) {
                System.out.printf("%3d", j);
            }
            if (matrizAtribuicao[0].length > 10) {
                System.out.print(" ...");
            }
            System.out.println();
            System.out.print("      ");
            for (int j = 0; j < Math.min(10, matrizAtribuicao[0].length); j++) {
                System.out.print("---");
            }
            System.out.println();

            // Dados da matriz
            for (int i = 0; i < Math.min(10, matrizAtribuicao.length); i++) {
                System.out.printf("%3d | ", i);
                for (int j = 0; j < Math.min(10, matrizAtribuicao[i].length); j++) {
                    System.out.printf("%3d", matrizAtribuicao[i][j]);
                }
                if (matrizAtribuicao[i].length > 10) {
                    System.out.print(" ...");
                }
                System.out.println();
            }
            if (matrizAtribuicao.length > 10) {
                System.out.println(" ...");
            }
        }

        // Conclusão
        System.out.println("\n=========================================");
        System.out.println("    ALOCAÇÃO CONCLUÍDA COM SUCESSO!");
        System.out.println("=========================================");

        scanner.close();
    }
}
