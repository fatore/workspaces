import java.util.*;

/*
 * Representa um neuronio tanto da camada escondida
 * quanto da camada de saida
 */
public class Neuron {
	private double[] weights; // o primeiro eh o bias
				  // weights[0] -> eh o bias

	public Neuron(int nweights) {
		this.weights = new double[nweights+1];

		// quero inicializar os pesos [-1, 1]
		Random random = new Random();

		// acabei de gerar os pesos
		for (int i = 0; i < nweights + 1; i++) {
			int sign = 1;
			if (random.nextDouble() < 0.5) sign = -1;
			this.weights[i] =
				sign * random.nextDouble();
		}
	}

	public double netInput(double[] input) {
		// x0*w0 + ... + xn-1 * wn-1 + 1*theta

		double net = 1.0 * this.weights[0];

		for (int i = 1; i < this.weights.length; i++) {
			net += input[i-1] * this.weights[i];
		}

		return net;
	}

	public double activationFunction(double net) {
		return 1.0 / (1.0 + Math.exp(-net));
	}

	public double[] getWeights() {
		return this.weights;
	}

	public void setWeights(double weights[]) {
		this.weights = weights;
	}
}
