package model;

public class Disciplina {

    private String nome;
    private int vagas;

    /**
     * Construtor para a classe Disciplina.
     * @param nome O nome da disciplina.
     * @param vagas O número de vagas que a disciplina possui.
     */
    public Disciplina(String nome, int vagas) {
        this.nome = nome;
        this.vagas = vagas;
    }

    public String getNome() {
        return nome;
    }

    public int getVagas() {
        return vagas;
    }
}
