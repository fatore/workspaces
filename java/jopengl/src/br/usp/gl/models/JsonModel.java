package br.usp.gl.models;

import br.usp.gl.util.JsonObjectReader;

public class JsonModel extends Model {

	
	public JsonModel(String filename) {
		try {
			JsonObjectReader reader = new JsonObjectReader(filename);
			
			this.positions = reader.getPositions();
			this.normals = reader.getNormals();
			this.indices = reader.getIndices();
			
		} catch (Exception e) {
			System.err.println("Failed to load json object.");
			e.printStackTrace();
		}
	}
}
