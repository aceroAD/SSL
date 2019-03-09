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
			if(arrCadena.length != 1 || arrCadena.length != 3) {
				out.write("Error de comando" + "\n");
				
			}
			else {
				String comando = arrCadena[0];
				
				if(comando.equalsIgnoreCase("LISTA")) {
					out.write(peticionHowMany());
					
				}
				else if(comando.equalsIgnoreCase("COKE")) {
					out.write(peticionCoke());
					
				}
				else if(comando.equalsIgnoreCase("BLOCK")) {
					out.write(peticionBlock());
					
				}
				else if (comando.equalsIgnoreCase("UNBLOCK")){
					out.write(peticionUnBlock());
					
				}
				else {
					out.write("comando no existente" + "\n");
					
				}
				
				out.flush();
			}
			}
			in.close();
			out.close();
			conexion.close();
			System.out.println("Un cliente se ha desconectado.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String peticionHowMany() {
		return "SERVER COKE: hay " + ServidorCoke.getCokes() + " cokes en la maquina" + "\n";
	}
	
	private String peticionCoke() {
		if(ServidorCoke.getStatus())
			return "SERVER COKE: maquina esta bloqueada"  + "\n";
		else if(ServidorCoke.getCokes() == 0)
			return "SERVER COKE: esta vacia"  + "\n";
		else  {
			ServidorCoke.takeCoke();
			return "SERVER COKE: TOMATE UNA COKE!" + "\n";
		}
	}
	
	private String peticionBlock() {
		ServidorCoke.bloquear(true);
		return "SERVER COKE: maquina bloqueada" + "\n";
	}
	private String peticionUnBlock() {
		ServidorCoke.bloquear(false);
		return "SERVER COKE: maquina desbloqueada" + "\n";
	}
}
