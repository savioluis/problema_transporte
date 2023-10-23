import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

public class Modelo {

	Data data;
	double infinity = Double.POSITIVE_INFINITY;

	public Modelo(Data data) {
		this.data = data;
	}

	public void solveModel() {
		Loader.loadNativeLibraries();
		MPSolver solver = MPSolver.createSolver("SCIP");

		double infinity = java.lang.Double.POSITIVE_INFINITY;

		if (data.categoria == Data.Categoria.BALANCEADO) {
			MPVariable[][] variaveis = new MPVariable[data.nOrigens][data.nDestinos];

			//CRIANDO AS VARIAVEIS DE DECISAO E COLOCANDO NA MATRIZ (3x2)
			for (int i = 0; i < data.nOrigens; i++) {
				for (int j = 0; j < data.nDestinos; j++) {
					// x00, x01, x10, x11 ... >= 0 e inteiros
					variaveis[i][j] = solver.makeIntVar(0, infinity, "X" +  (i+1) + (j+1));
				}
			}

			//CRIANDO A FUNCAO OBJETIVO
			// 80 * x00 + 215 + x01 + 100 * x10 ...
			MPObjective fObjetivo = solver.objective();
			for (int i = 0; i < data.nOrigens; i++) {
				for (int j = 0; j < data.nDestinos; j++) {
					fObjetivo.setCoefficient(variaveis[i][j], data.custos[i][j]);
				}
			}
			fObjetivo.setMinimization();

			//RESTRICOES DE ORIGEM
			// x00 + x01 = 1000
			// x10 + x11 = 1500
			// x20 + x21 = 1200
			for (int i = 0; i < data.nOrigens; i++) {
				MPConstraint restricaoProducaoOrigem = solver.makeConstraint(data.producaoOrigem[i], data.producaoOrigem[i], "Restricao Producao Origem " + (i+1));
				for (int j = 0; j < data.nDestinos; j++) {
					restricaoProducaoOrigem.setCoefficient(variaveis[i][j], 1);
				}
			}

			//RESTRICOES DE DESTINO
			// x00 + x10 + x20 = 2300
			// x01 + x11 + x21 = 1400
			for (int i = 0; i < data.nDestinos; i++) {
				MPConstraint restricaoDemandaDestino = solver.makeConstraint(data.demandaDestino[i], data.demandaDestino[i], "Restricao Demanda Destino " + (i+1));
				for (int j = 0; j < data.nOrigens; j++) {
					restricaoDemandaDestino.setCoefficient(variaveis[j][i], 1);
				}
			}

			System.out.println("Numero de restricoes = " + solver.numConstraints());

			MPSolver.ResultStatus resultStatus = solver.solve();

			if (resultStatus == MPSolver.ResultStatus.OPTIMAL) {

				System.out.println("Custo da funcao objetivo = " + fObjetivo.value());
				System.out.println("Solucao:");
				for (int i = 0; i < data.nOrigens; i++) {
					for (int j = 0; j < data.nDestinos; j++) {
						System.out.println("X" + (i+1) + (j+1) + " = " + variaveis[i][j].solutionValue());
					}
				}
				System.out.println("Tempo de resolucao = " + solver.wallTime() + " milissegundos");
				System.out.println(solver.exportModelAsLpFormat());
			} else {
				System.out.println("Solucao otima nao encontrada!");
			}


		} else if (data.categoria == Data.Categoria.PRODUCAO_ORIGEM_MAIOR_QUE_DEMANDA_DESTINO) {

			MPVariable[][] variaveis = new MPVariable[data.nOrigens][data.nDestinos+1];

			//CRIANDO AS VARIAVEIS DE DECISAO E COLOCANDO NA MATRIZ (3x3)
			for (int i = 0; i < data.nOrigens; i++) {
				for (int j = 0; j < data.nDestinos+1; j++) {
					// x00, x01, x10, x11 ... >= 0 e inteiros
					variaveis[i][j] = solver.makeIntVar(0, infinity, "X" +  (i+1) + (j+1));
				}
			}

			//CRIANDO A FUNCAO OBJETIVO
			// 80 * x00 + 215 + x01 + 100 * x10 ...
			MPObjective fObjetivo = solver.objective();
			for (int i = 0; i < data.nOrigens; i++) {
				for (int j = 0; j < data.nDestinos; j++) {
					fObjetivo.setCoefficient(variaveis[i][j], data.custos[i][j]);
				}
			}
			fObjetivo.setMinimization();

			//RESTRICOES DE ORIGEM
			// x00 + x01 = 1000
			// x10 + x11 = 1500
			// x20 + x21 = 1200

			for (int i = 0; i < data.nOrigens; i++) {
				MPConstraint restricaoProducaoOrigem = solver.makeConstraint(data.producaoOrigem[i], data.producaoOrigem[i], "Restricao Producao Origem " + (i+1));
				for (int j = 0; j < data.nDestinos+1; j++) {
					restricaoProducaoOrigem.setCoefficient(variaveis[i][j], 1);
				}
			}

			//RESTRICOES DE DESTINO
			// x00 + x10 + x20 = 2300
			// x01 + x11 + x21 = 1400
			for (int i = 0; i < data.nDestinos; i++) {
				MPConstraint restricaoDemandaDestino = solver.makeConstraint(data.demandaDestino[i], data.demandaDestino[i], "Restricao Demanda Destino " + (i+1));
				for (int j = 0; j < data.nOrigens; j++) {
					restricaoDemandaDestino.setCoefficient(variaveis[j][i], 1);
				}
			}

			//RESTRICAO EXTRA DE DESTINO
			// x03 + x13 + x23 = 300
			MPConstraint restricaoDemandaDestino = solver.makeConstraint(data.diferencaBalanceamento(), data.diferencaBalanceamento(), "Restricao Extra Demanda Destino");
			for (int i = 0; i < data.nOrigens; i++) {
				restricaoDemandaDestino.setCoefficient(variaveis[i][data.nDestinos], 1);
			}

			System.out.println("Numero de restricoes = " + solver.numConstraints());

			MPSolver.ResultStatus resultStatus = solver.solve();

			if (resultStatus == MPSolver.ResultStatus.OPTIMAL) {

				System.out.println("Custo da funcao objetivo = " + fObjetivo.value());
				System.out.println("Solucao:");
				for (int i = 0; i < data.nOrigens; i++) {
					for (int j = 0; j < data.nDestinos+1; j++) {
						System.out.println("X" + (i+1) + (j+1) + " = " + variaveis[i][j].solutionValue());
					}
				}
				System.out.println("Tempo de resolucao = " + solver.wallTime() + " milissegundos");
				System.out.println(solver.exportModelAsLpFormat());
			} else {
				System.out.println("Solucao otima nao encontrada!");
			}


		} else {

			MPVariable[][] variaveis = new MPVariable[data.nOrigens+1][data.nDestinos];

			//CRIANDO AS VARIAVEIS DE DECISAO E COLOCANDO NA MATRIZ (4x3)
			for (int i = 0; i < data.nOrigens+1; i++) {
				for (int j = 0; j < data.nDestinos; j++) {
					// x00, x01, x10, x11 ... >= 0 e inteiros
					variaveis[i][j] = solver.makeIntVar(0, infinity, "X" +  (i+1) + (j+1));
				}
			}

			//CRIANDO A FUNCAO OBJETIVO
			// 80 * x00 + 215 + x01 + 100 * x10 ...
			MPObjective fObjetivo = solver.objective();
			for (int i = 0; i < data.nOrigens; i++) {
				for (int j = 0; j < data.nDestinos; j++) {
					fObjetivo.setCoefficient(variaveis[i][j], data.custos[i][j]);
				}
			}
			fObjetivo.setMinimization();

			//RESTRICOES DE ORIGEM
			// x00 + x01 = 1000
			// x10 + x11 = 1500
			// x20 + x21 = 1200

			for (int i = 0; i < data.nOrigens; i++) {
				MPConstraint restricaoProducaoOrigem = solver.makeConstraint(data.producaoOrigem[i], data.producaoOrigem[i], "Restricao Producao Origem " + (i+1));
				for (int j = 0; j < data.nDestinos; j++) {
					restricaoProducaoOrigem.setCoefficient(variaveis[i][j], 1);
				}
			}

			//RESTRICAO EXTRA DE ORIGEM
			// x30 + x31 = 300
			MPConstraint restricaoExtraProducaoOrigem = solver.makeConstraint(data.diferencaBalanceamento(), data.diferencaBalanceamento(), "Restricao Extra Producao Origem");

			for (int i = 0; i < data.nDestinos; i++) {
				restricaoExtraProducaoOrigem.setCoefficient(variaveis[data.nOrigens][i], 1);
			}


			//RESTRICOES DE DESTINO
			// x00 + x10 + x20 + x31 = 2300
			// x01 + x11 + x21 + x32 = 1400
			for (int i = 0; i < data.nDestinos; i++) {
				MPConstraint restricaoDemandaDestino = solver.makeConstraint(data.demandaDestino[i], data.demandaDestino[i], "Restricao Demanda Destino " + (i+1));
				for (int j = 0; j < data.nOrigens+1; j++) {
					restricaoDemandaDestino.setCoefficient(variaveis[j][i], 1);
				}
			}

			data.exibirTipoTratamento();

			System.out.println("Numero de restricoes = " + solver.numConstraints());

			MPSolver.ResultStatus resultStatus = solver.solve();

			if (resultStatus == MPSolver.ResultStatus.OPTIMAL) {

				System.out.println("Custo da funcao objetivo = " + fObjetivo.value());
				System.out.println("Solucao:");
				for (int i = 0; i < data.nOrigens+1; i++) {
					for (int j = 0; j < data.nDestinos; j++) {
						System.out.println("X" + (i+1) + (j+1) + " = " + variaveis[i][j].solutionValue());
					}
				}
				System.out.println("Tempo de resolucao = " + solver.wallTime() + " milissegundos");
				System.out.println(solver.exportModelAsLpFormat());
			} else {
				System.out.println("Solucao otima nao encontrada!");
			}


		}

	}

}
