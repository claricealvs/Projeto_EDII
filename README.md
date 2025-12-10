# 👨🏻‍🏫 **Distribuição de Disciplinas Entre Professores**

## **Integrantes da Equipe:** Ariane Soares, Clarice Alves, Davydson Rodrigues,  Pedro Henrique e Ryan Victor.

## 🎯 **1\. Introdução e Objetivo**

O objetivo principal é distribuir disciplinas entre os professores do núcleo, respeitando a capacidade de carga horária de cada docente e maximizando a satisfação global baseada em suas preferências. A solução utiliza a modelagem de **Grafos Bipartidos** e implementa o **Algoritmo Húngaro** (Método Kuhn-Munkres) para encontrar o emparelhamento perfeito de custo mínimo (que equivale à satisfação máxima).

O Algoritmo Húngaro foi escolhido como método de solução por ser ideal para esse tipo de problema, garantindo a solução ótima, equivalente ao custo mínimo. Para adequar o problema à modelagem, os seguintes requisitos são atendidos no contexto de alocação de disciplinas:

* Cada vaga de disciplina (tarefa) deve ser atribuída a exatamente um slot de professor (pessoa).
* A alocação busca garantir que a capacidade de carga horária dos professores (pessoas disponíveis) seja totalmente utilizada, evitando desperdício de alocação ou slots sem atribuição.
* Cada atribuição entre Professor e Disciplina possui um custo associado, que é derivado da preferência manifestada pelo professor. A partir dessas designações, é encontrado o custo mínimo, que maximiza a satisfação geral.


## 📂 **2\. Arquitetura da Solução**

O sistema foi desenvolvido em **Java** utilizando o padrão arquitetural MVC (Model-View-Controller) simplificado, dividido nos seguintes pacotes:

* **model**: Define as entidades básicas (Professor, Disciplina) e a unidade granular de alocação (Slot), que representa uma fração da capacidade de um professor ou uma vaga de disciplina.
* **grafo (Adapter)**: Responsável pela transformação dos dados. A classe GrafoBipartido converte as listas de objetos em uma Matriz de Adjacência (Matriz de Custo) times.
* **algoritmo (Core)**: Contém a lógica matemática pura. A classe AlgoritmoHungaro processa a matriz de inteiros para encontrar a alocação ótima, independente das regras de negócio.

## ⌨ **3\. Especificação da Entrada**

A entrada do sistema consiste em dois conjuntos de dados pré-definidos (baseados na tabela oficial do semestre 2025/2):

1. **Conjunto de Professores:** Lista contendo Nome, Capacidade de Aulas (Slots) e Mapa de Preferências (Disciplina —\>Nota 1 a 5).
2. **Conjunto de Disciplinas:** Lista contendo Nome e Número de Vagas (Slots necessários).

Processamento da Entrada (Replicação de Slots): Como o Algoritmo Húngaro exige uma relação 1:1, os dados sofrem um pré-processamento onde cada professor é "replicado" de acordo com sua capacidade.

* *Exemplo:* Professor "Cristiane" (Capacidade 3\) torna-se 3 nós distintos: P\_Cristiane\_1, P\_Cristiane\_2, P\_Cristiane\_3.
* **Balanceamento:** Caso o número de slots de professores e disciplinas seja dividido, o sistema gera automaticamente "Slots Fictícios" para garantir uma matriz quadrada de dimensão 25x25.

## 🧾 **4\. Matriz de Custo**

A peça central da solução é a **Matriz de Custo**, gerada pela classe `GrafoBipartido`. Ela traduz as preferências subjetivas dos professores em valores numéricos que o algoritmo consegue processar.

### **4.1. Função de Custo (Lógica de Inversão)**

Como o Algoritmo Húngaro é nativamente desenhado para encontrar o **menor custo** possível, foi necessário adaptar os dados para atender ao objetivo do projeto, que é encontrar a **maior satisfação**.

Para isso, aplicamos uma lógica de inversão de valores na construção da matriz:

* O custo de cada alocação é definido pela **diferença entre a nota máxima possível (5) e a preferência indicada pelo professor**.
* **Consequência Prática:** Uma disciplina que recebeu nota máxima (5) resulta em um custo zero para o algoritmo (o cenário ideal). Já uma disciplina com nota mínima (1) resulta em um custo alto (4). Dessa forma, ao tentar "economizar" custos, o algoritmo está, na verdade, priorizando as maiores notas de satisfação.

### **4.2. Restrições e Penalidades**

Além da inversão de valores, o sistema aplica regras rígidas para impedir alocações inválidas:

* **Afinidade Zero (Proibição):** Caso um professor não tenha listado uma disciplina em suas preferências, o sistema atribui um custo "infinito" (o maior valor inteiro possível na linguagem Java) àquela posição na matriz. Isso matematicamente proíbe o algoritmo de realizar essa alocação.
* **Slots Fictícios (Balanceamento):** Para alocações que envolvem slots de sobra (fictícios), o custo é definido sempre como zero. Isso permite que o algoritmo descarte o excesso de oferta ou demanda sem penalizar o cálculo global da solução.

## 🖥 **5\. Saída**

A saída é apresentada visualmente em uma tabela contendo o pareamento ótimo:

* **Identificação:** Mapeamento do Slot do Professor para o Slot da Disciplina.
* **Métrica de Qualidade:** Exibição da "Nota de Preferência" original (recuperada revertendo o cálculo de custo).
* **Status de Alocação:** Indicação visual clara caso um professor tenha ficado sem aula ("SEM\_PROFESSOR") ou uma disciplina tenha sobrado ("FIC\_SOBRA"), decorrente do balanceamento da matriz.
* **Satisfação Total:** Somatório das preferências de todas as alocações realizadas.

## 🔢 **6\. Análise de Complexidade**

A eficiência do sistema é determinada pelo Algoritmo Húngaro.

* **Complexidade de Tempo:** A implementação padrão do algoritmo Húngaro (Kuhn-Munkres) possui complexidade assintótica de **O(n^3)**, onde n é a dimensão da matriz.
    * No contexto deste projeto, onde n \= 25, o número de operações é da ordem de 25^3 \= 15.625 operações elementares.
* **Complexidade de Espaço:** O algoritmo utiliza matrizes auxiliares para marcação (linhas cobertas, colunas cobertas, estrelas e primos), resultando em complexidade de espaço **O(n^2)** para armazenar a matriz de custo e a matriz de atribuição.

## ⚙ **7\. Lógica de Execução**

O algoritmo executa iterativamente os seguintes passos até a convergência:

1. **Redução:** Subtração do menor elemento de cada linha e coluna para gerar zeros triviais.
2. **Cobertura:** Tentativa de cobrir todos os zeros com o número mínimo de linhas/colunas. Se o número de linhas necessárias for igual a $n$ (25), a solução ótima foi encontrada.
3. **Ajuste:** Caso contrário, o algoritmo modifica a matriz subtraindo o menor elemento não coberto dos demais e somando-o nas interseções, forçando o surgimento de novos zeros em posições estratégicas para criar novos caminhos de emparelhamento.

## ➕ **8\. Informações Adicionais**

### **Como Rodar o Projeto**

Este é um projeto **Java simples** configurado para ser executado diretamente em uma IDE (como **IntelliJ IDEA**), sem a necessidade de gerenciadores de dependência como Maven ou Gradle.

#### **Pré-requisitos**

Para compilar e executar o código, você precisa ter instalado:

* **Java Development Kit (JDK):** Versão **17 ou superior**.

  #### **Passos para Execução**

1. **Clonar o Repositório:** Baixe o código para sua máquina utilizando o terminal:  
   Bash  
   git clone https://github.com/claricealvs/Projeto\_EDII.git
2. **Abrir na IDE:** Abra a pasta clonada (`Projeto_EDII`) no **IntelliJ IDEA**. A IDE configura o projeto automaticamente.
3. **Executar:** No IntelliJ, localize a classe principal em `src/app/Main.java`. Clique com o botão direito e selecione **"Run 'Main.main()'"** para iniciar a demonstração do algoritmo.

### **Estrutura Principal do Projeto**

O código principal para a implementação do **Algoritmo Húngaro** está organizado nos seguintes pacotes e arquivos:

* `src/algoritmo/AlgoritmoHungaro.java`: Contém a **lógica central** e a implementação do Algoritmo Húngaro.
* `src/app/Main.java`: É a **Classe Principal** que serve como ponto de entrada (`main`) para rodar a demonstração e os testes.
* `src/grafo/`: Contém as **Classes auxiliares para modelagem**, como `GrafoBipartido` e estruturas relacionadas.
