package model;

public class Slot {

    private String idUnico; // Ex: "P_Cristiane_1" ou "D_PIII_TurmaA"
    private String nomeOrigem; // Nome original (ex: "Cristiane" ou "Projeto Integrador III")
    private String tipo;       // "PROFESSOR" ou "DISCIPLINA"
    private int indice;        // Posição (linha/coluna) na matriz 25x25

    /**
     * Construtor para a classe Slot.
     * @param idUnico ID único para o slot (incluindo o número da réplica).
     * @param nomeOrigem Nome da entidade original (Professor ou Disciplina).
     * @param tipo Indica se é um Slot de Professor ou de Disciplina.
     * @param indice A posição que este slot ocupará na matriz (0 a 24).
     */
    public Slot(String idUnico, String nomeOrigem, String tipo, int indice) {
        this.idUnico = idUnico;
        this.nomeOrigem = nomeOrigem;
        this.tipo = tipo;
        this.indice = indice;
    }

    public String getIdUnico() {
        return idUnico;
    }

    public String getNomeOrigem() {
        return nomeOrigem;
    }

    public String getTipo() {
        return tipo;
    }

    public int getIndice() {
        return indice;
    }

    @Override
    public String toString() {
        return idUnico;
    }
}
