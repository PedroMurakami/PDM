# Calculadora de Saude Pro

## Sobre o Projeto
O objetivo da aplicação é oferecer uma ferramenta robusta para avaliação antropométrica e de composição corporal, implementando fórmulas científicas reconhecidas para fornecer dados mais precisos do que calculadoras genéricas de IMC.

## Funcionalidades Principais

1. Dashboard Interativo
Menu inicial com navegação clara, permitindo ao usuário escolher entre avaliações rápidas ou detalhadas.

2. Calculo de IMC (Indice de Massa Corporal)
Calcula o índice com base no peso e altura e fornece a classificação correspondente baseada nas diretrizes da Organização Mundial da Saúde (OMS).

3. Estimativa de Gordura Corporal
Utiliza a "Formula da Marinha Americana" (US Navy Method). Diferente do IMC, este método utiliza medidas de circunferência (pescoço, cintura e quadril) para estimar o percentual de gordura corporal, oferecendo uma distinção mais precisa entre massa magra e massa gorda.

4. Taxa Metabolica Basal (TMB)
Implementa a equação de Mifflin-St Jeor para calcular o gasto energético do corpo em repouso, considerando sexo, peso, altura e idade.

5. Necessidade Calorica Diaria
Calcula o gasto energético total multiplicando a TMB pelo fator de atividade física selecionado pelo usuário (Sedentário, Leve, Moderado ou Intenso).

6. Peso Ideal
Estimativa baseada na fórmula de Devine, utilizada como referência para metas de saúde.

7. Historico Persistente
Utiliza um banco de dados local (Room Database) para salvar todas as avaliações. O usuário pode consultar seus resultados anteriores e excluir registros antigos.

## Arquitetura e Tecnologias

O projeto foi desenvolvido seguindo as práticas de Modern Android Development (MAD).

- Linguagem: Kotlin
- Interface de Usuario (UI): Jetpack Compose (Declarativa)
- Arquitetura: MVVM (Model-View-ViewModel)
- Persistencia de Dados: Room Database (SQLite)
- Gerenciamento de Estado: StateFlow e MutableState
- Assincronicidade: Kotlin Coroutines
- Navegacao: Navigation Compose

## Estrutura do Projeto

O código está organizado em três camadas principais para garantir a separação de responsabilidades:

- ui: Contém os componentes visuais (Telas), o ViewModel e a configuração de Tema. Esta camada é responsável apenas por exibir dados e capturar interações do usuário.
- domain: Contém a lógica de negócios e matemática (Objeto CalculadoraSaude). As fórmulas residem aqui, isoladas de dependências do Android.
- data: Contém a camada de persistência. Inclui a configuração do Banco de Dados (AppDatabase), o modelo de dados (Registro) e o objeto de acesso a dados (DAO).

## Como Executar o Projeto

1. Clone este repositório para sua máquina local.
2. Abra o Android Studio (versão Iguana ou superior recomendada).
3. Selecione a opção "Open" e navegue até a pasta do projeto clonado.
4. Aguarde a sincronização do Gradle.
5. Conecte um dispositivo Android físico com depuração USB ativada ou inicie um emulador (AVD).
6. Clique no botão de execução (Run) no Android Studio.

## Melhorias Futuras

Para versões futuras, o projeto prevê as seguintes implementações:

- Visualização de dados em gráficos de linha para acompanhar a evolução do peso e gordura corporal.
- Integração com Firebase para autenticação de usuário e backup de dados na nuvem.
- Implementação da API Google Health Connect para leitura automática de dados de outros dispositivos.
- Exportação de relatórios de histórico em formato PDF.

## Autor

Desenvolvido como parte de portfólio de desenvolvimento mobile Android.
