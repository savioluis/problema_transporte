

import java.io.FileNotFoundException;

public class Principal {

	public static void main(String[] args) throws FileNotFoundException {
		
		String filename = "input.txt";
		Data data = new Data(filename);
		data.exibirArquivo();
		data.exibirInformacoes();

		Modelo modelo = new Modelo(data);
		modelo.solveModel();
	}

}
