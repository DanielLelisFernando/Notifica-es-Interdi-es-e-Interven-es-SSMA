# Checklist mestre de revisão — SCL - INI

## Formulário e fidelidade
- [ ] Arquivo-fonte F-SSMA-0062R00 versionado em `docs/referencias/`.
- [x] Inventário inicial dos campos oficiais documentado.
- [ ] Conferência campo a campo com SSMA responsável.
- [ ] PDF comparado visualmente com o original.

## Aplicativo e dados
- [ ] Navegação validada em aparelho físico.
- [ ] Room/SQLite e migrações não destrutivas.
- [ ] Integridade referencial e relacionamentos testados.
- [ ] Registros preservados após atualização.
- [ ] Teste integral offline/modo avião.

## Interdições
- [ ] Painel de interdições ativas.
- [ ] Linha do tempo append-only.
- [ ] Ação concluída não libera interdição.
- [ ] Liberação exige responsável identificado.
- [ ] Histórico anterior preservado.
- [ ] PDF complementar de liberação validado.

## Documentos e exportação
- [ ] PDF completo e paginado.
- [ ] Exportação ZIP.
- [ ] Backup/restauração com integridade e prevenção de duplicidade.

## Qualidade e entrega
- [ ] Testes unitários e instrumentados.
- [ ] Build GitHub Actions aprovado.
- [ ] APK instalado e aberto em aparelho físico.
- [ ] Changelog atualizado.
