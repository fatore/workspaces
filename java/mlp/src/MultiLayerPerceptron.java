import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/*
 * Esta classe irah representar a rede neural completa
 */
public class MultiLayerPerceptron {

	private ArrayList<Neuron> hiddenLayer;
	private ArrayList<Neuron> outputLayer;
	private int inputLayerSize;
	private int hiddenLayerSize;
	private int outputLayerSize;
	private String trainFilename;
	private String testFilename;
	private double stopCriterion;
	private double eta;

	public MultiLayerPerceptron(int inputLayerSize,
			int hiddenLayerSize, 
			int outputLayerSize,
			String trainFilename,
			String testFilename,
			double stopCriterion, double eta) {

		this.stopCriterion = stopCriterion;
		this.eta = eta;
		this.trainFilename = trainFilename;
		this.testFilename = testFilename;

		this.inputLayerSize = inputLayerSize;
		this.hiddenLayerSize = hiddenLayerSize;
		this.outputLayerSize = outputLayerSize;

		this.hiddenLayer = new ArrayList<Neuron>();
		this.outputLayer = new ArrayList<Neuron>();

		// criando os neuronios da camada escondida
		for (int i = 0; i < hiddenLayerSize; i++) {
			this.hiddenLayer.add(
					new Neuron(inputLayerSize));
		}

		// criando os neuronios da camada de saida
		for (int i = 0; i < outputLayerSize; i++) {
			this.outputLayer.add(
					new Neuron(hiddenLayerSize));
		}

	}

	public void forward() throws Exception {
		// 1. abrir o arquivo que contem os exemplos
		FileReader fr = new FileReader(testFilename);
		Scanner scanner = new Scanner(fr);
		PrintWriter out = new PrintWriter(new File("result.dat"));

		//	    System.out.println("Input sample | Obtained Output | Desired Output\n");
		System.out.println("Obtained Output | Desired Output");

		while (scanner.hasNextDouble()) {
			// lendo um exemplo do conjunto de treinamento
			// lendo os atributos
			double input[] = new double[this.inputLayerSize];
			for (int i = 0; i < inputLayerSize; i++) {
				input[i] = scanner.nextDouble();
			}

			// lendo a saida desejada ou esperada
			double expectedOutput[] = 
					new double[outputLayerSize];
			for (int i = 0; i < outputLayerSize; i++) {
				expectedOutput[i] = scanner.nextDouble();
			}

			// 2. processar

			double hidden[] = new double[hiddenLayerSize];
			// para cada neuronio da camada escondida, faca
			for (int i = 0; i < hiddenLayerSize; i++) {
				Neuron neuron = this.hiddenLayer.get(i);
				double net = neuron.netInput(input);
				hidden[i] = 
						neuron.activationFunction(net);
			}

			double output[] = new double[outputLayerSize];
			// para cada neuronio da camada de saida, faca
			for (int i = 0; i < outputLayerSize; i++) {
				Neuron neuron = this.outputLayer.get(i);
				double net = neuron.netInput(hidden);
				output[i] =
						neuron.activationFunction(net);
			}

			// 3. ver a saida obtida

			// imprimindo os atributos do exemplo
			for (int i = 0; i < inputLayerSize; i++) {
				//			System.out.print(input[i]+"   ");
			}

			//		System.out.print(" | ");

			// imprimindo a saida obtida
			for (int i = 0; i < outputLayerSize; i++) {
				System.out.print(output[i]+"   ");
				out.print(output[i]+"   ");
			}

			System.out.print(" | ");

			// imprimindo a saida desejada
			for (int i = 0; i < outputLayerSize; i++) {
				System.out.print(expectedOutput[i]+"   ");
				out.print(expectedOutput[i]+"   ");
			}

			System.out.println();
			out.println();
		}
		scanner.close();
		fr.close();
		out.close();
	}

	public void backpropagation() throws Exception {
		double squaredError;
		int iterations = 0;

		do {
			iterations++;
			squaredError = 0.0;

			// 1. abrir o arquivo que contem os exemplos
			FileReader fr = new FileReader(trainFilename);
			Scanner scanner = new Scanner(fr);

			while (scanner.hasNextDouble()) {
				// lendo um exemplo do conjunto de treinamento
				// lendo os atributos
				double input[] = new double[this.inputLayerSize];
				for (int i = 0; i < inputLayerSize; i++) {
					input[i] = scanner.nextDouble();
				}

				// lendo a saida desejada ou esperada
				double expectedOutput[] = 
						new double[outputLayerSize];
				for (int i = 0; i < outputLayerSize; i++) {
					expectedOutput[i] = scanner.nextDouble();
				}

				// 2. processar

				double hidden[] = new double[hiddenLayerSize];
				// para cada neuronio da camada escondida, faca
				for (int i = 0; i < hiddenLayerSize; i++) {
					Neuron neuron = this.hiddenLayer.get(i);
					double net = neuron.netInput(input);
					hidden[i] = 
							neuron.activationFunction(net);
				}

				double output[] = new double[outputLayerSize];
				// para cada neuronio da camada de saida, faca
				for (int i = 0; i < outputLayerSize; i++) {
					Neuron neuron = this.outputLayer.get(i);
					double net = neuron.netInput(hidden);
					output[i] =
							neuron.activationFunction(net);
				}

				// expectedOutput ??? -> saida desejada
				// output ??? -> saida obtida

				// calculando o erro!!!
				double error[] = new double[outputLayerSize];
				for (int i = 0; i < outputLayerSize; i++) {
					error[i] = expectedOutput[i] -
							output[i];

					squaredError += Math.pow(error[i], 2.0);
				}

				// RESOLVENDO A CAMADA DE SAIDA
				double df_de_o___dnet_de_o[] = 
						new double[outputLayerSize];

				// f^o'(net^o) = f^o(net^o) * (1 - f^o(net^o))
				for (int i = 0; i < outputLayerSize; i++) {
					df_de_o___dnet_de_o[i] =
							output[i] * (1.0 - output[i]);
				}

				double delta_de_o[] = 
						new double[outputLayerSize];
				for (int i = 0; i < outputLayerSize; i++) {
					delta_de_o[i] = error[i] * 
							df_de_o___dnet_de_o[i];
				}

				// RESOLVENDO A CAMADA ESCONDIDA
				double delta_de_h[] = new double[hiddenLayerSize];
				for (int j = 0; j < hiddenLayerSize; j++) {
					double somatorio = 0.0;
					for (int i = 0; i < outputLayerSize; i++)
					{
						Neuron nSaida = 
								this.outputLayer.get(i);
						double weights[] = 
								nSaida.getWeights();
						somatorio += delta_de_o[i]*
								weights[j+1];
					}

					double df_de_h__dnet_de_h =
							hidden[j] * (1.0 - hidden[j]);

					delta_de_h[j] = df_de_h__dnet_de_h *
							somatorio;
				}

				// vou finalmente usar os deltas
				for (int i = 0; i < outputLayerSize; i++) {
					Neuron nSaida = this.outputLayer.get(i);

					// ufah, adaptamos os pesos da saida
					double w[] = nSaida.getWeights();
					w[0] = w[0] + eta * delta_de_o[0] * 1.0; 
					for (int j = 1; j < w.length; j++) {
						w[j] = w[j] + eta * delta_de_o[i]
								* hidden[j-1];
					}

					nSaida.setWeights(w);
				}

				for (int i = 0; i < hiddenLayerSize; i++) {
					Neuron nEscond = this.hiddenLayer.get(i);

					double w[] = nEscond.getWeights();

					w[0] = w[0] + eta * delta_de_h[0] * 1.0; 
					for (int j = 1; j < w.length; j++) {
						w[j] = w[j] + eta * delta_de_h[i]
								* input[j-1];
					}

					nEscond.setWeights(w);
				}

				/*		
		// 3. ver a saida obtida

		// imprimindo os atributos do exemplo
		for (int i = 0; i < inputLayerSize; i++) {
			System.out.print(input[i]+"   ");
		}

		System.out.print(" | ");

		// imprimindo a saida obtida
		for (int i = 0; i < outputLayerSize; i++) {
			System.out.print(output[i]+"   ");
		}

		System.out.print(" | ");

		// imprimindo a saida desejada
		for (int i = 0; i < outputLayerSize; i++) {
			System.out.print(expectedOutput[i]+"   ");
		}

		System.out.println();

				 */
			}

			fr.close();

			if (iterations % 500 == 0) {
				System.out.println("Erro Quadratico: "+squaredError+" depois de "+iterations+" iteracoes...");
			}

		} while (squaredError > stopCriterion);

		//	  System.out.println("Erro Quadratico: "+squaredError+" depois de "+iterations+" iteracoes...");

	}

	public static void main(String args[]) throws Exception {

		if (args.length != 7) {
			System.out.println("java MultiLayerPerceptron inputLayerSize hiddenLayerSize outputLayerSize trainFilename testFilename stopCriterion eta\n");
			System.exit(0);
		}

		//        System.out.println("Training...");

		int inputLayerSize  = Integer.parseInt(args[0]);
		int hiddenLayerSize = Integer.parseInt(args[1]);
		int outputLayerSize = Integer.parseInt(args[2]);
		String trainFilename= args[3];
		String testFilename = args[4];
		double stopCriterion= Double.parseDouble(args[5]);
		double eta	    = Double.parseDouble(args[6]);

		MultiLayerPerceptron mlp = 
				new MultiLayerPerceptron(
						inputLayerSize,
						hiddenLayerSize,
						outputLayerSize,
						trainFilename,
						testFilename,
						stopCriterion,
						eta
						);

		mlp.backpropagation();

		//        System.out.println("Testing...");
		mlp.forward();
	}
}
