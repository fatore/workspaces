package br.usp.pf.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.StringTokenizer;

/**
 *
 * @author Francisco Morgani Fatore
 */
public class Conformation {

	/**
	 */
	private boolean[] chain;
	//private static Pattern p = Pattern.compile(" ");

	public Conformation() {
		//matrix that represents the contacts
		chain = new boolean[27 * 27];

		//fills the array
		Arrays.fill(chain, false);
	}
	
	@Override
	public String toString() {
		String ret = "";
		for (int i = 0; i < chain.length; i++) {
			if (chain[i]) {
				ret += " " + i;
			}
		}
		return ret;
	}

	/**
	 * @return
	 */
	public boolean[] getChain() {
		return chain;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 67 * hash + Arrays.hashCode(this.chain);

		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}

		final Conformation other = (Conformation) obj;
		for (int i = 0; i < chain.length; i++) {
			if (chain[i] != other.chain[i]) {
				return false;
			}
		}

		return true;
	}

	//returns the last read line, null case its the end of file
	public String read(BufferedReader in) throws IOException {

		String line, ini, end;
		//String[] tokens;
		StringTokenizer st;

		while ((line = in.readLine()) != null) {

			//tokens = p.split(line);
			st = new StringTokenizer(line," ");

			if (st.countTokens() == 2) {

				ini = st.nextToken();
				end = st.nextToken();

				//27 * ini-1 = column and end - 1 = line of the false matrix
				int index = (27 * (Integer.parseInt(ini) - 1)) + (Integer.parseInt(end) - 1);
				this.chain[index] = true;

			} else {
				//got to a new conformation or to a *
				return line;
			}
		}

		//got to the end of file
		return line;
	}
}
