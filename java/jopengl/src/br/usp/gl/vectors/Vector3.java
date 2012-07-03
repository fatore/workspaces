package br.usp.gl.vectors;

public class Vector3 extends Vector {

	public Vector3() {
		this.vector = new float[3];
	}
	
	public Vector3(float[] array) {
		this.vector = array;
	}
	
	public void normalize() {
		
		float x = this.vector[0], y = this.vector[1], z = this.vector[2],
				len = (float) Math.sqrt(x * x + y * y + z * z);

		if (len == 0) {
			this.vector[0] = 0;
			this.vector[1] = 0;
			this.vector[2] = 0;
		} else if (len == 1) {
			this.vector[0] = x;
			this.vector[1] = y;
			this.vector[2] = z;
		}

		len = 1 / len;
		this.vector[0] = x * len;
		this.vector[1] = y * len;
		this.vector[2] = z * len;
	}
	
	public static Vector3 normalize(Vector3 input) {
		
		Vector3 dest = new Vector3();

		float x = input.vector[0], y = input.vector[1], z = input.vector[2],
				len = (float) Math.sqrt(x * x + y * y + z * z);

		if (len == 0) {
			dest.vector[0] = 0;
			dest.vector[1] = 0;
			dest.vector[2] = 0;
			return dest;
		} else if (len == 1) {
			dest.vector[0] = x;
			dest.vector[1] = y;
			dest.vector[2] = z;
			return dest;
		}

		len = 1 / len;
		dest.vector[0] = x * len;
		dest.vector[1] = y * len;
		dest.vector[2] = z * len;
		return dest;
	}
	
	public static Vector3 cross(Vector3 vector0, Vector3 vector1) {
		
		float[] result = new float[3];

		result[0] = vector0.getVector()[1] * vector1.getVector()[2] - 
				vector0.getVector()[2] * vector1.getVector()[1];
		result[1] = vector0.getVector()[2] * vector1.getVector()[0] - 
				vector0.getVector()[0] * vector1.getVector()[2];
		result[2] = vector0.getVector()[0] * vector1.getVector()[1] 
				- vector0.getVector()[1] * vector1.getVector()[0];

		
		return new Vector3(result);
	}
	
	public static float[] cross(float[] vector0, float[] vector1) {
		
		float[] result = new float[3];

		result[0] = vector0[1] * vector1[2] - vector0[2] * vector1[1];
		result[1] = vector0[2] * vector1[0] - vector0[0] * vector1[2];
		result[2] = vector0[0] * vector1[1] - vector0[1] * vector1[0];
		
		return result;
	}

	public static float[] normalize(float[] vector) {
		
		float[] dest = new float[3];

		float x = vector[0], y = vector[1], z = vector[2],
				len = (float) Math.sqrt(x * x + y * y + z * z);

		if (len == 0) {
			dest[0] = 0;
			dest[1] = 0;
			dest[2] = 0;
			return dest;
		} else if (len == 1) {
			dest[0] = x;
			dest[1] = y;
			dest[2] = z;
			return dest;
		}

		len = 1 / len;
		dest[0] = x * len;
		dest[1] = y * len;
		dest[2] = z * len;
		return dest;
	}
	
	public void scale(float value) {
		
        this.vector[0] *= value;
        this.vector[1] *= value;
        this.vector[2] *= value;
    };
}
