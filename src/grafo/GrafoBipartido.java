package grafo;

import model.Disciplina;
import model.Professor;
import model.Slot; // Importando sua nova classe

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GrafoBipartido {

    public List<Slot> slotsProfessores = new ArrayList<>();
    public List<Slot> slotsDisciplinas = new ArrayList<>();

    private Map<String, Professor> mapaProfessores = new HashMap<>();
    private Map<String, Disciplina> mapaDisciplinas = new HashMap<>();

    private int[][] matrizCusto;
    private final int DIMENSAO = 25; // Matriz 25x25 
    private final int C_MAX = 5;     // Nota máxima para inversão

    public GrafoBipartido(List<Professor> professores, List<Disciplina> disciplinas) {
        // Mapeamento rápido
        for (Professor p : professores) mapaProfessores.put(p.getNome(), p);
        for (Disciplina d : disciplinas) mapaDisciplinas.put(d.getNome(), d);

        criarSlots(professores, disciplinas);
    }

    private void criarSlots(List<Professor> professores, List<Disciplina> disciplinas) {
        int indiceGeral = 0;

        // 1. Criar Slots Reais de Professores
        for (Professor p : professores) {
            for (int i = 0; i < p.getCapacidadeMaxima(); i++) {
                String id = "P_" + p.getNome() + "_" + (i + 1);
                slotsProfessores.add(new Slot(id, p.getNome(), "PROFESSOR", indiceGeral++));
            }
        }

        // Preencher com Professores Fitícios se a soma for menor que 25
        // Isso impede o IndexOutOfBoundsException se reduzir as capacidades
        while (slotsProfessores.size() < DIMENSAO) {
            slotsProfessores.add(new Slot("P_Ficticio_" + indiceGeral, "SEM_PROFESSOR", "PROFESSOR", indiceGeral++));
        }

        indiceGeral = 0; 

        // 3. Criar Slots Reais de Disciplinas
        for (Disciplina d : disciplinas) {
            for (int i = 0; i < d.getVagas(); i++) {
                String id = "D_" + d.getNome() + "_" + (i + 1);
                slotsDisciplinas.add(new Slot(id, d.getNome(), "DISCIPLINA", indiceGeral++));
            }
        }

        // 4. Preencher com Disciplinas Fictícias (Sobras)
        while (slotsDisciplinas.size() < DIMENSAO) {
            slotsDisciplinas.add(new Slot("D_Ficticia_" + indiceGeral, "FIC_SOBRA", "DISCIPLINA", indiceGeral++));
        }

        System.out.println("DEBUG: Slots Prof: " + slotsProfessores.size() + " | Slots Disc: " + slotsDisciplinas.size());
    }

    public int[][] gerarMatrizCusto() {
        matrizCusto = new int[DIMENSAO][DIMENSAO];

        for (int i = 0; i < DIMENSAO; i++) {
            for (int j = 0; j < DIMENSAO; j++) {

                Slot slotProf = slotsProfessores.get(i);
                Slot slotDisc = slotsDisciplinas.get(j);

                // Regra 1: Se for Disciplina Fictícia (Sobra), custo é 0 para qualquer um
                if (slotDisc.getNomeOrigem().equals("FIC_SOBRA")) {
                    matrizCusto[i][j] = 0;
                    continue;
                }

                // Regra 2: Se for Professor Fictício (Ninguém)
                if (slotProf.getNomeOrigem().equals("SEM_PROFESSOR")) {
                    matrizCusto[i][j] = 0;
                    continue;
                }

                // Regra 3: Casamento Real
                Professor prof = mapaProfessores.get(slotProf.getNomeOrigem());

                // Verifica a preferência
                Integer preferencia = null;
                if (prof != null) {
                    preferencia = prof.getPreferencias().get(slotDisc.getNomeOrigem());
                }

                if (preferencia != null) {
                    matrizCusto[i][j] = C_MAX - preferencia;
                } else {
                    matrizCusto[i][j] = Integer.MAX_VALUE;
                }
            }
        }
        return matrizCusto;
    }
}
