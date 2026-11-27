# SCL - INI

Sistema de Interdição, Notificação e Intervenção de SSMA da Salum Construções Ltda.

- **Versão:** 1.0.0 — fundação inicial
- **Plataforma:** Android nativo, Kotlin, Jetpack Compose e Material 3
- **Persistência prevista:** Room/SQLite
- **Operação:** offline

## Estado do desenvolvimento

A base do projeto, tela inicial, documentação e workflow inicial foram criados. Os módulos funcionais completos ainda estão em desenvolvimento.

## Documentação

- `CHANGELOG.md`
- `docs/ANALISE-FORMULARIO-F-SSMA-0062R00.md`
- `docs/CHECKLIST-MESTRE-REVISAO.md`

## Compilação

GitHub Actions executa o build debug e publica o artefato `SCL-INI-debug-apk`. A validação do build não substitui testes em aparelho.

## Regras de integridade

A interdição só poderá ser liberada por evento formal de liberação, com identificação do responsável e histórico preservado. Concluir ação, vencer prazo ou anexar foto não libera uma interdição automaticamente.
