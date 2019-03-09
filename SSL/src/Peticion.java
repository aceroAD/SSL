import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Peticion implements Runnable{
	private Socket conexion;
	private BufferedReader in;
	private BufferedWriter out;
	Scanner sc;
	
	public Peticion(Socket conexion) {
		this.conexion = conexion;
		sc = new Scanner(System.in);
	}
	
	public void run() {
		try {
			in = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(conexion.getOutputStream()));
			String cadena;
			
			while((cadena = in.readLine()) != null) {
				String[] arrCadena = cadena.split(" ");
				if(arrCadena.length != 1 && arrCadena.length != 3) {
				out.write("Error de comando" + "\n");
				
				}
				else {
					String comando = arrCadena[0];
					
					if(comando.equalsIgnoreCase("LISTA")) {
						out.write(peticionLista());
						
					}
					else if(comando.equalsIgnoreCase("RESERVAR")) {
						int fila = Integer.parseInt(arrCadena[1]);
						out.write(peticionReserva(fila, arrCadena[2]));
						
					}
					else if(comando.equalsIgnoreCase("ANULAR")) {
						int fila = Integer.parseInt(arrCadena[1]);
						out.write(peticionAnular(fila, arrCadena[2]));
						
					}
					else {
						out.write("comando no existente" + "\n");
						
					}
				}
				out.flush();
		}
			in.close();
			out.close();
			conexion.close();
			System.out.println("Un cliente se ha desconectado. \n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String peticionLista() {
		String cadenaLista = "Los asientos libre son: ";
		String huecosIzquierda = " I";
		String huecosDerecha = " D";
		boolean[] izquierda = ServidorCoke.getIzquierda();
		boolean[] derecha = ServidorCoke.getDerecha();
		
		for (int i = 0; i < 6; i++) {
			
			if(izquierda[i] == false)
				huecosIzquierda = huecosIzquierda + " " + (i+1);
			
			if(derecha[i] == false) 
				huecosDerecha = huecosDerecha + " " + (i+1);
		}
		cadenaLista = cadenaLista + huecosDerecha + "    " +  huecosIzquierda + "\n";
			
		return cadenaLista;
	}
	
	private String peticionReserva(int fila, String reserva) {
		if (reserva.equalsIgnoreCase("I")) {
			if (ServidorCoke.reservarAsiento(fila, "izquierda")) {
				return "Reserva realizada correctamente \n";
			}
			else return "Reserva no realizada \n";
		}
		else if (reserva.equalsIgnoreCase("D")) {
			if (ServidorCoke.reservarAsiento(fila, "derecha")) {
				return "Reserva realizada correctamente\n";
			}
			else return "Reserva no realizada \n";
		}
		else return "Error en reserva \n";
	}
	
	private String peticionAnular(int fila, String reserva) {
		if (reserva.equalsIgnoreCase("I")) {
			if (ServidorCoke.anularReserva(fila, "izquierda")) {
				return "Anulacion de reserva realizada correctamente \n";
			}
			else return "Asiento no reservado \n";
		}
		else if (reserva.equalsIgnoreCase("D")) {
			if (ServidorCoke.anularReserva(fila, "derecha")) {
				return "Anulacion de reserva realizada correctamente \n";
			}
			else return "Asiento no reservado \n";
		}
		else return "Error en anulacion \n";
	}
}
