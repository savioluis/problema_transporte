# Problema do Transporte

Problema de Programaçãoo Linear que tem como objetivo minimizar os custos de transporte de produtos, obedecendo limites de oferta e demanda.

## Objetivo:

Modelar, de forma genérica, o problema do transporte considerando os dois
possíveis casos (balanceado e desbalanceado). No caso desbalanceado, têm-se tanto `produção > demanda` e `produção < demanda`.

## Exemplo de entrada (arquivo no formato .txt):

3 2       `Número de origens e número de destinos`

5 5 10    `Produção de cada origem`

7 8       `Demanda de cada destino`

14 7      `Custo do transporte por unidade da origem 1 aos destinos 1 e 2`

8 12      `Custo do transporte por unidade da origem 2 aos destinos 1 e 2`

5 9       `Custo do transporte por unidade da origem 3 aos destinos 1 e 2`

