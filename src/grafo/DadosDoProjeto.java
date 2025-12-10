package grafo;

import model.Disciplina;
import model.Professor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DadosDoProjeto {

    private final List<Professor> professores;
    private final List<Disciplina> disciplinas;

    // Número de disciplinas para cada professor
    private static final Map<String, Integer> CAPACIDADES_PROFESSORES = Map.ofEntries(

            Map.entry("Amaury", 3),
            Map.entry("Cristiane", 3),
            Map.entry("Gabriel", 2),
            Map.entry("Jean", 1),
            Map.entry("Júnio", 3),
            Map.entry("Jucelino", 1),
            Map.entry("Luciana", 1),
            Map.entry("Marcos", 1),
            Map.entry("Nattane", 4),
            Map.entry("Paulo", 3),
            Map.entry("Rachel", 2)

    );

    // Vagas por disciplina
    private static final Map<String, Integer> VAGAS_DISCIPLINAS = Map.of(
            "Projeto Integrador III", 3,
            "Projeto Integrador IV", 3,
            "Lógica", 1,
            "Álgebra Linear", 1
            // Todas as outras disciplinas têm 1 vaga por padrão
    );


    public DadosDoProjeto() {
        this.professores = new ArrayList<>();
        this.disciplinas = new ArrayList<>();
        carregarDisciplinas();
        carregarProfessores();
    }


    private void carregarDisciplinas() {
        String[] nomesDisciplinas = {
                "Arquitetura de Software", "Bancos de Dados II", "Construção de Sistemas",
                "Elaboração de Projetos", "Engenharia de Software", "Estrutura de Dados II",
                "Ética, Computador e Sociedade", "Introdução a Mineração de Dados",
                "Optativa I", "Optativa III", "Processo de Análise e Desenvolvimento de Sistemas",
                "Programação II", "Programação para Dispositivos Móveis",
                "Programação para Web I", "Projeto Integrador III",
                "Projeto Integrador IV", "Redes de Computadores", "Sistemas Operacionais"
        };

        for (String nome : nomesDisciplinas) {
            int vagas = VAGAS_DISCIPLINAS.getOrDefault(nome, 1);
            disciplinas.add(new Disciplina(nome, vagas));
        }

        disciplinas.add(new Disciplina("Lógica", VAGAS_DISCIPLINAS.get("Lógica")));
        disciplinas.add(new Disciplina("Álgebra Linear", VAGAS_DISCIPLINAS.get("Álgebra Linear")));
    }

    private void carregarProfessores() {
        for (Map.Entry<String, Integer> entry : CAPACIDADES_PROFESSORES.entrySet()) {
            professores.add(new Professor(entry.getKey(), entry.getValue()));
        }

        // Definição das Preferências
        for (Professor p : professores) {
            switch (p.getNome()) {
                case "Amaury":
                    p.adicionarPreferencia("Sistemas Operacionais", 5);
                    p.adicionarPreferencia("Redes de Computadores", 5);
                    p.adicionarPreferencia("Projeto Integrador III", 3);
                    break;
                case "Cristiane":
                    p.adicionarPreferencia("Programação II", 5);
                    p.adicionarPreferencia("Bancos de Dados II", 4);
                    p.adicionarPreferencia("Elaboração de Projetos", 3);
                    break;
                case "Gabriel":
                    p.adicionarPreferencia("Arquitetura de Software", 5);
                    p.adicionarPreferencia("Construção de Sistemas", 5);
                    break;
                case "Jean":
                    p.adicionarPreferencia("Projeto Integrador IV", 5);
                    break;
                case "Júnio":
                    p.adicionarPreferencia("Estrutura de Dados II", 5);
                    p.adicionarPreferencia("Programação para Dispositivos Móveis", 4);
                    p.adicionarPreferencia("Optativa III", 3);
                    break;
                case "Jucelino":
                    p.adicionarPreferencia("Lógica", 5);
                    break;
                case "Luciana":
                    p.adicionarPreferencia("Projeto Integrador IV", 5);
                    break;
                case "Marcos":
                    p.adicionarPreferencia("Álgebra Linear", 5);
                    break;
                case "Nattane":
                    p.adicionarPreferencia("Optativa I", 3);
                    p.adicionarPreferencia("Programação para Web I", 5);
                    p.adicionarPreferencia("Introdução a Mineração de Dados", 4);
                    p.adicionarPreferencia("Projeto Integrador III", 3);
                    break;
                case "Paulo":
                    p.adicionarPreferencia("Engenharia de Software", 5);
                    p.adicionarPreferencia("Processo de Análise e Desenvolvimento de Sistemas", 5);
                    p.adicionarPreferencia("Projeto Integrador IV", 4);
                    break;
                case "Rachel":
                    p.adicionarPreferencia("Ética, Computador e Sociedade", 5);
                    p.adicionarPreferencia("Projeto Integrador III", 4);
                    break;
            }
        }
    }

    public List<Professor> getProfessores() {
        return professores;
    }

    public List<Disciplina> getDisciplinas() {
        return disciplinas;
    }
}
