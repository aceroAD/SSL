import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

public class ServidorCoke {
	public static final int puerto = 6666;
	public static final int FILAS = 6;
	private static Socket conexion;
	private boolean[] izquierda = new boolean[FILAS];
	private boolean[] derecha = new boolean[FILAS];

	
	private String rutaAlmacen;
	private String claveAlmacen;
	private String claveCertificado;
	
	public ServidorCoke(String rutaAlmacen, String claveAlmacen, String claveCertificado) {
		
		this.rutaAlmacen = rutaAlmacen;
		this.claveAlmacen = claveAlmacen;
		this.claveCertificado = claveCertificado;
		
		for (int i = 0; i < FILAS; i++) {
			izquierda[i] = false;
			derecha[i] = false;
		}
	}
	
	public SSLServerSocket getSocketSSL() throws KeyStoreException, 
	NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException, 
	KeyManagementException {
		SSLServerSocket socketSSL = null;
		
		FileInputStream fileAlmacen = new FileInputStream(this.rutaAlmacen);
		KeyStore almacen = KeyStore.getInstance(KeyStore.getDefaultType());
		almacen.load(fileAlmacen, claveAlmacen.toCharArray());
		
		System.out.println("Leido el almacen de llaves");
		
		KeyManagerFactory fabricaClaves = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		fabricaClaves.init(almacen, claveCertificado.toCharArray());
		
		System.out.println("Leida clave");
		
		SSLContext contextoSSL = SSLContext.getInstance("TLS");
		contextoSSL.init(fabricaClaves.getKeyManagers(), null, null);
		
		SSLServerSocketFactory fabricaSockets = contextoSSL.getServerSocketFactory();
		socketSSL = (SSLServerSocket) fabricaSockets.createServerSocket(puerto);
		System.out.println("Socket Creado.");
		return socketSSL;
	}
	
	public void escuchar() throws UnrecoverableKeyException, KeyManagementException, KeyStoreException, 
	NoSuchAlgorithmException, CertificateException, IOException {
		SSLServerSocket socketSSL = this.getSocketSSL();
		while(true) {
			conexion = socketSSL.accept();
			Peticion peticion = new Peticion(conexion);
			Thread cliente = new Thread(peticion);
			cliente.start();
		}
	}
	
	public static void main(String[] args) {
		ServidorCoke servidor = new ServidorCoke("./almacenservidor", "almacenservidor", "claveservidor");
		try {
			servidor.escuchar();
		} catch (UnrecoverableKeyException | KeyManagementException | KeyStoreException | NoSuchAlgorithmException
				| CertificateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized boolean reservarAsiento(int fila, String lado) {
		if (lado.equalsIgnoreCase("izquierda")) {
			if(izquierda[fila] == true)
				return false;
			else {
				izquierda[fila] = true;
				return true;
			}
		}
		else if(lado.equalsIgnoreCase("derecha")) {
			if(derecha[fila] == true)
				return false;
			else {
				derecha[fila] = true;
				return true;
			}
		}
		return false;
	}
	
	public synchronized boolean anularReserva(int fila, String lado) {
		if (lado.equalsIgnoreCase("izquierda")) {
			if(izquierda[fila] == false)
				return false;
			else {
				izquierda[fila] = false;
				return true;
			}
		}
		else if(lado.equalsIgnoreCase("derecha")) {
			if(derecha[fila] == false)
				return false;
			else {
				derecha[fila] = false;
				return true;
			}
		}
		return false;
	}
	
	public  boolean[] getIzquierda() {
		return this.izquierda;
	}
	
	public  boolean[] getDerecha() {
		return this.derecha;
	}
	

}

