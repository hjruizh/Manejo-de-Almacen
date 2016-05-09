package clases;

public class Fecha {
	public static int mesNumerico(String mes){
		if (mes.equals("Enero")){
			return 0;
		}
		if (mes.equals("Febrero")){
			return 1;
		}
		if (mes.equals("Marzo")){
			return 2;
		}
		if (mes.equals("Abril")){
			return 3;
		}
		if (mes.equals("Mayo")){
			return 4;
		}
		if (mes.equals("Junio")){
			return 5;
		}
		if (mes.equals("Julio")){
			return 6;
		}
		if (mes.equals("Agosto")){
			return 7;
		}
		if (mes.equals("Septiembre")){
			return 8;
		}
		if (mes.equals("Octubre")){
			return 9;
		}
		if (mes.equals("Nomviembre")){
			return 10;
		}
		if (mes.equals("Diciembre")){
			return 11;
		}
		return 0;
	}
	public static int diasDelMes(int mes, String anio){
		 switch(mes){
		  case 0:  // Enero
		  case 2:  // Marzo
		  case 4:  // Mayo
		  case 6:  // Julio
		  case 7:  // Agosto
		  case 9:  // Octubre
		  case 11: // Diciembre
		   return 31;
		  case 3:  // Abril
		  case 5:  // Junio
		  case 8:  // Septiembre
		  case 10: // Noviembre
		   return 30;
		  case 1:  // Febrero
		   if ( ((Integer.parseInt(anio)%100 == 0) && (Integer.parseInt(anio)%400 == 0)) ||
		        ((Integer.parseInt(anio)%100 != 0) && (Integer.parseInt(anio)%  4 == 0))   ) {
			   return 29;  // A�o Bisiesto
		   }
		   else 
		    return 28;
		  default:
		 }
		 return -1;
	}
}
