package clases;

public class validarMesDia {
	public static String agregarCero(int num){
		if (num<10){
			return "0"+ String.valueOf(num);
		}
		else{
			return String.valueOf(num);
		}
	}
}
