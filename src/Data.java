import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

public class Data {

    enum Categoria {
        BALANCEADO,
        PRODUCAO_ORIGEM_MAIOR_QUE_DEMANDA_DESTINO,
        DEMANDA_DESTINO_MAIOR_QUE_PRODUCAO_ORIGEM,
    }

    int nOrigens;
    int nDestinos;
    int[] producaoOrigem;
    int[] demandaDestino;
    int[][] custos;
    Categoria categoria;
    String nomeArquivo;

    public Data(String filename) throws FileNotFoundException {
        try {
            File file = new File(filename);
            Scanner scanner = new Scanner(file);

            nOrigens = scanner.nextInt();
            nDestinos = scanner.nextInt();

            producaoOrigem = new int[nOrigens];
            demandaDestino = new int[nDestinos];

            for (int i = 0; i < nOrigens; i++) {
                producaoOrigem[i] = scanner.nextInt();
            }

            for (int i = 0; i < nDestinos; i++) {
                demandaDestino[i] = scanner.nextInt();
            }

            custos = new int[nOrigens][nDestinos];

            for (int i = 0; i < nOrigens; i++) {
                for (int j = 0; j < nDestinos; j++) {
                    custos[i][j] = scanner.nextInt();
                }
            }

            int contadorProducaoOrigens = somaDoArray(producaoOrigem);
            int contadorDemandaDestinos = somaDoArray(demandaDestino);

            defineCategoria(contadorProducaoOrigens, contadorDemandaDestinos);

            nomeArquivo = filename;

            scanner.close();
        } catch (FileNotFoundException erro) {
            System.out.println("Erro ao ler o arquivo !");
            throw erro;
        }
    }

    private void defineCategoria(int contadorProducaoOrigens, int contadorDemandaDestinos) {
        if (contadorProducaoOrigens == contadorDemandaDestinos) {
            categoria = Categoria.BALANCEADO;
        } else if (contadorProducaoOrigens > contadorDemandaDestinos) {
            categoria = Categoria.PRODUCAO_ORIGEM_MAIOR_QUE_DEMANDA_DESTINO;
        } else {
            categoria = Categoria.DEMANDA_DESTINO_MAIOR_QUE_PRODUCAO_ORIGEM;
        }
    }

    private int somaDoArray(int[] array) {
        int somador = Arrays.stream(array).sum();
        return somador;
    }


    public int diferencaBalanceamento() {
        return Math.abs(somaDoArray(producaoOrigem) - somaDoArray(demandaDestino));
    }

    public void exibirArquivo() {
        System.out.println("-----------------------");

        System.out.println("Arquivo lido: " + nomeArquivo);

        System.out.println(nOrigens + " " + nDestinos);

        for (int i = 0; i < producaoOrigem.length; i++) {
            if (i == producaoOrigem.length - 1) System.out.println(producaoOrigem[i]);
            else System.out.print(producaoOrigem[i] + " ");
        }

        for (int i = 0; i < demandaDestino.length; i++) {
            if (i == demandaDestino.length - 1) System.out.println(demandaDestino[i]);
            else System.out.print(demandaDestino[i] + " ");
        }

        for (int i = 0; i < custos.length; i++) {
            for (int j = 0; j < custos[i].length; j++) {
                if (j == custos[j].length - 1) System.out.println(custos[i][j]);
                else System.out.print(custos[i][j] + " ");
            }
        }

        System.out.println("-----------------------");
//		System.out.println("");
    }

    public void exibirInformacoes() {
        int contadorDemanda = 0;
        int contadorProducao = 0;

        exibirTipoTratamento();

        System.out.println("-----------------------");

        System.out.println("Origens: " + nOrigens);
        System.out.println("Destinos: " + nDestinos);

        System.out.println("-----------------------");

        System.out.println("Producoes:");
        for (int i = 0; i < producaoOrigem.length; i++) {
            contadorProducao += producaoOrigem[i];
            System.out.println("Origem " + (i + 1) + ": " + producaoOrigem[i]);
        }

        System.out.println("-----------------------");

        System.out.println("Demandas:");
        for (int i = 0; i < demandaDestino.length; i++) {
            contadorDemanda += demandaDestino[i];
            System.out.println("Destino " + (i + 1) + ": " + demandaDestino[i]);
        }

        System.out.println("-----------------------");

        System.out.println("Total Producao = " + contadorProducao);
        System.out.println("Total Demanda = " + contadorDemanda);

        System.out.println("-----------------------");

        System.out.println("Custos de transporte:");
        for (int i = 0; i < custos.length; i++) {
            for (int j = 0; j < custos[i].length; j++) {
                System.out.println("Origem " + (i + 1) + " para Destino " + (j + 1) + ": " + custos[i][j]);
            }
        }

        System.out.println("-----------------------");
        System.out.println();
    }

    public void exibirTipoTratamento() {
        System.out.println();
        System.out.println("Tipo de tratamento: ");

        if (categoria == Categoria.BALANCEADO) {
            System.out.println("Balanceado (Producao = Demanda)");
        } else if (categoria == Categoria.PRODUCAO_ORIGEM_MAIOR_QUE_DEMANDA_DESTINO) {
            System.out.println("Desbalanceado (Producao > Demanda)");
        } else {
            System.out.println("Desbalanceado (Producao < Demanda)");
        }
        System.out.println();
    }
}
