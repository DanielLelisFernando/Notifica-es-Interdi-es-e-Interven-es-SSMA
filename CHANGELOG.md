# Changelog

## [1.0.0] - 2026-11-27

### Inicialização
- Estrutura inicial Android nativa em Kotlin e Jetpack Compose.
- Documentação do escopo e da análise do formulário oficial.
- Workflow inicial para APK debug.

### Limitações conhecidas
- A fonte do formulário deverá ser versionada em `docs/referencias/` para manter rastreabilidade.
- A interface inicial é uma fundação navegável; persistência, fluxos completos, PDFs, backup e testes físicos ainda precisam ser implementados e validados.

## [1.2.0] - Em desenvolvimento

- Implementação inicial do cadastro digital dos três tipos de registro.
- Persistência local Room/SQLite.
- Consulta, painel de interdições ativas, tratativas e indicadores locais.
- Registro de eventos/histórico.
- Fluxo de liberação formal com identificação, gerência e evidência/verificação.
- Exportação inicial de PDF para compartilhamento.
- Ícone vetorial de barreira e alerta, integração inicial ao launcher.
- Workflow habilitado para compilar a branch 1.2.0.
- Pendentes nesta versão: revisão/validação de build, PDF paginado e fiel, anexos fotográficos robustos, ZIP, backup/restauração, pesquisa/filtragem, testes e validação física/offline.

### Ajuste de escopo — 1.2.0
- Removidos da tela inicial e navegação os módulos de Indicadores e Acompanhamento de Ações/Tratativas.
- Removidos campos e comandos de registro de tratativa independente na tela de detalhe.
- Mantidos cadastro, Providências Tomadas (campo do formulário original), status de interdição, evidências, histórico do registro e liberação formal.
