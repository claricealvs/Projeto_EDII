package model;

import java.util.HashMap;
import java.util.Map;

public class Professor {

    private String nome;
    private int capacidadeMaxima;
    private Map<String, Integer> preferencias;

    /**
     * Construtor para a classe Professor.
     * @param nome O nome completo do professor.
     * @param capacidadeMaxima O número máximo de disciplinas que o professor pode ministrar.
     */
    public Professor(String nome, int capacidadeMaxima) {
        this.nome = nome;
        this.capacidadeMaxima = capacidadeMaxima;
        this.preferencias = new HashMap<>();
    }

    /**
     * Adiciona ou atualiza a preferência do professor por uma disciplina.
     * @param nomeDisciplina O nome da disciplina.
     * @param nivelPreferencial O nível de preferência (1 a 5).
     */
    public void adicionarPreferencia(String nomeDisciplina, int nivelPreferencial) {
        if (nivelPreferencial >= 1 && nivelPreferencial <= 5) {
            this.preferencias.put(nomeDisciplina, nivelPreferencial);
        } else if (nivelPreferencial == 0) {
            this.preferencias.remove(nomeDisciplina);
        } else {
            System.err.println("Nível de preferência inválido para " + nomeDisciplina);
        }
    }

    public String getNome() {
        return nome;
    }

    public int getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public Map<String, Integer> getPreferencias() {
        return preferencias;
    }
}
