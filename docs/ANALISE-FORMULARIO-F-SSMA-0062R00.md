# Análise do formulário F-SSMA-0062R00

**Fonte analisada:** arquivo fornecido nesta conversa: *F-SSMA-0062R00 Formulário de Notificação, Interdição e Intervenção de SSMA.docx*.  
**Identificação impressa:** F-SSMA-0062 | Revisão 00 | Data de revisão 31/08/2022.  
**Extensão:** 1 página.

> Esta análise contempla somente os elementos identificáveis na página recebida. O arquivo-fonte deverá ser incorporado a `docs/referencias/` para auditoria. Não foram presumidas opções ou obrigatoriedades que não constam do formulário.

## 1. Inventário de campos oficiais

| Página | Seção | Nomenclatura original | Tipo digital sugerido | Opções / observações |
|---|---|---|---|---|
| 1 | Cabeçalho | Data | Data | Campo impresso; sem obrigatoriedade explícita visível. |
| 1 | Cabeçalho | Hora | Hora | Campo impresso. |
| 1 | Cabeçalho | Local | Texto | Campo impresso. |
| 1 | Identificação | De | Texto | Campo impresso. |
| 1 | Identificação | Gerência/Empresa (De) | Texto | Primeiro campo com esta nomenclatura. |
| 1 | Identificação | Para | Texto | Campo impresso. |
| 1 | Identificação | Gerência/Empresa (Para) | Texto | Segundo campo com esta nomenclatura. |
| 1 | Caracterização | Área / Equipamento / Atividade/ Documento | Texto multilinha | Preservar nomenclatura original. |
| 1 | Tipo | Interdição | Seleção única | Texto explicativo impresso sobre interdição por não atendimento às exigências mínimas de segurança, risco à integridade física e/ou legislação ambiental e exposição a sanções legais. |
| 1 | Tipo | Notificação | Seleção única | Texto explicativo impresso sobre orientação para providência imediata, correção do problema e prevenção de acidentes, degradação ambiental e/ou saneamento de pendências documentais. |
| 1 | Tipo | Intervenção | Seleção única | Texto explicativo impresso; determina que ações para eliminação dos desvios sejam resolvidas em no máximo 24 horas. |
| 1 | Descrição | Descrição da condição não conforme | Texto multilinha | Campo de descrição. |
| 1 | Severidade | Severidade | Seleção única | Leve; Moderada; Grave. |
| 1 | Orientação | Recomendações | Texto multilinha | O formulário instrui que, atendidas as recomendações, o Emitente deverá ser comunicado e o campo “Liberação de área” preenchido pelo Emitente. |
| 1 | Responsáveis | Responsável ou testemunha da área/documentação interditada / notificada | Texto | Campo impresso. |
| 1 | Responsáveis | Assinatura do Emitente | Assinatura | Campo impresso. |
| 1 | Tratativa | Providências Tomadas | Texto multilinha | Campo impresso. |
| 1 | Tratativa | Observação (es) | Texto multilinha | Campo impresso. |
| 1 | Tratativa | Prazo de execução | Texto/data-prazo | A fonte não especifica o formato de entrada. |
| 1 | Liberação | Nome | Texto | Na seção “Liberação da área”. |
| 1 | Liberação | Gerência | Texto | Na seção “Liberação da área”. |
| 1 | Liberação | Assinatura | Assinatura | Na seção “Liberação da área”. |

## 2. Classificações originais

Tipos impressos: **Interdição**, **Notificação** e **Intervenção**. Severidade: **Leve**, **Moderada** e **Grave**. Não há outras opções de severidade visíveis na fonte.

## 3. Campos repetitivos

“Gerência/Empresa” aparece duas vezes, vinculada respectivamente a “De” e “Para”; devem ser armazenados separadamente. A página não contém grade de ações múltiplas nem outro grupo explicitamente repetitivo.

## 4. Assinaturas

Existem os espaços “Assinatura do Emitente” e “Assinatura” no bloco “Liberação da área”. Captura por toque na tela é uma modalidade digital complementar; o documento não especifica certificado, método eletrônico ou regra jurídica de assinatura.

## 5. Instruções expressas no formulário

1. Interdição: descreve não atendimento a exigências mínimas de segurança e os riscos associados.
2. Notificação: orienta providência imediata para corrigir o problema e evitar acidentes, degradação ambiental e/ou sanar problemas documentais.
3. Intervenção: estabelece prazo máximo de 24 horas para resolver as ações decorrentes destinadas à eliminação dos desvios.
4. Atendidas as recomendações, o Emitente deverá ser comunicado e o campo “Liberação de área” preenchido pelo Emitente.

## 6. Obrigatoriedade

A página não apresenta asteriscos, legenda de obrigatoriedade ou declaração explícita de campos obrigatórios. Portanto, esta análise não atribui obrigatoriedade oficial a nenhum campo. Validações técnicas que o aplicativo venha a exigir devem ser classificadas como complementos digitais.

## 7. Elementos não encontrados como campos originais

Não aparecem: número sequencial, unidade/obra, matrícula, função, responsável individual por ação, status de ação, data de conclusão, fotos/evidências, geolocalização, linha do tempo, identificador técnico, justificativa de recusa de liberação ou campo de verificação adicional. Se implementados, serão complementos do sistema, não campos do formulário.

## 8. Reprodução em PDF

A fonte ocupa uma página A4 em orientação retrato. Preservar o cabeçalho com logotipo, título, código/revisão/data; blocos Data/Hora/Local, De/Para e respectivas Gerência/Empresa; Área/Equipamento/Atividade/Documento; opções dos três tipos e seus textos explicativos; quadro Descrição da condição não conforme e Severidade; instrução de comunicação ao Emitente; Recomendações; responsáveis e assinaturas; Providências Tomadas; Observação (es)/Prazo de execução; e seção final Liberação da área com Nome, Gerência e Assinatura.

Textos longos deverão continuar em páginas adicionais sem omissão. A função de PDF só deverá ser considerada validada após comparação visual com a página original.

## 9. Pendências

- Versionar o documento-fonte no repositório em `docs/referencias/`.
- Confirmar com o responsável pelo processo se há revisão posterior à Revisão 00 de 31/08/2022.
- Validar o fluxo de assinatura/liberação com o procedimento corporativo aplicável.

## 10. Referência visual

A leitura da página 1 confirma o quadro de severidade à direita da descrição, os blocos de recomendações e providências, e o bloco “Liberação da área” no rodapé com Nome, Gerência e Assinatura.
