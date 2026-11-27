# SCL - INI — Status de implementação
**Versão em desenvolvimento:** 1.2.0 | **Atualização:** 27/11/2026

## Status por item

| Item | Status | Observação |
|---|---|---|
| Escopo e arquitetura inicial | 🟢 Concluído | Android nativo, Kotlin, Compose/Material 3, operação offline planejada |
| Análise do formulário F-SSMA-0062R00 | 🟢 Documentada | Inventário e nomenclatura de campos registrados |
| Tela inicial / navegação-base | 🟢 Implementada | Tela navegável básica; fluxos funcionais ainda não concluídos |
| Ícone visual do app | 🟢 Conceito aprovado | Barreira centralizada, alerta, sinalizadores e fundo azul; texto/logo removidos na versão limpa |
| Ícone vetorial integrado inicial | 🟡 Implementado | Versão vetorial provisória; falta validar visualmente e gerar recursos adaptive completos |
| Cadastro de Interdição / Notificação / Intervenção | 🟡 Implementação inicial | Formulário e salvamento local implementados, validação/fluxo completo pendente | Fluxo de cadastro completo não implementado |
| Captura de fotos/evidências | 🔴 Pendente | — |
| Assinatura digital por toque | 🔴 Pendente | Complemento digital; validar requisitos e procedimento |
| Persistência Room/SQLite | 🟡 Implementada base | Entidades e DAO iniciais; migrações/testes pendentes | Implementação e migrações ainda não validadas |
| Consulta e pesquisa de registros | 🔴 Pendente | — |
| Painel de Interdições Ativas | 🟡 Implementado | Listagem baseada no status local | — |
| Ciclo de status e acompanhamento | 🟡 Implementação inicial | Status/tratativa e evento ainda precisam de refinamento | Definir/implementar estados e trilha de eventos |
| Liberação formal de interdição | 🟡 Implementada base | Campos obrigatórios de liberação e evento; validação de fluxo/testes pendentes | Exigir responsável, evidência/registro de verificação e preservar histórico; nunca automática |
| Linha do tempo imutável (append-only) | 🔴 Pendente | — |
| PDF | 🟡 Implementação inicial | PDF de compartilhamento inicial; fidelidade, paginação e layout por validar | Fidelidade visual e paginação por validar contra original |
| PDF complementar de liberação | 🔴 Pendente | — |
| Exportação ZIP | 🔴 Pendente | — |
| Backup e restauração | 🔴 Pendente | Integridade e prevenção de duplicidade por testar |
| Uso totalmente offline | 🟡 Requisito definido | Ainda não validado em aparelho/modo avião |
| Testes unitários/instrumentados | 🔴 Pendente | — |
| GitHub Actions / build APK | 🟡 Em validação | Workflow habilitado para branch 1.2.0 | Workflow corrigido; confirmar execução bem-sucedida |
| Instalação e teste físico | 🔴 Pendente | — |
| Changelog | 🟢 Inicial | Changelog v1.0.0 criado; atualizar a cada alteração |

## Próxima sequência sugerida
1. Integrar o ícone nos recursos Android (launcher/adaptive icon).
2. Validar e estabilizar a compilação do APK.
3. Implementar cadastro com todos os campos do formulário e armazenamento offline.
4. Implementar consulta, histórico e painel de interdições ativas.
5. Implementar tratativas e fluxo formal de liberação.
6. Gerar PDFs, exportação ZIP e backup/restauração.
7. Executar testes funcionais e físicos, atualizando este status e o changelog.

**Legenda:** 🟢 concluído/documentado | 🟡 iniciado/em validação | 🔴 pendente.
