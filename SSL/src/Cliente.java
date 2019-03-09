
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Scanner;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class Cliente {
	
	private String rutaAlmacen;
	private String claveAlmacen;
	private String ipServidor;
	private int puertoServidor;
	private SSLSocket socketSSL;
	
	public Cliente(String rutaAlmacen, String claveAlmacen, String ipServidor, int puertoServidor) 
			throws KeyManagementException, KeyStoreException, NoSuchAlgorithmException, 
			CertificateException, IOException {
		
		this.rutaAlmacen = rutaAlmacen;
		this.claveAlmacen = claveAlmacen;
		this.ipServidor = ipServidor;
		this.puertoServidor = puertoServidor;
	}
	
	public void conectar() throws IOException, KeyManagementException, KeyStoreException,
	NoSuchAlgorithmException, CertificateException {
		String respuesta;
		String cadena;
		this.socketSSL = obtenerSocket(ipServidor, puertoServidor);
		BufferedReader entrada = new BufferedReader(new InputStreamReader(socketSSL.getInputStream()));
		BufferedWriter salida = new BufferedWriter(new OutputStreamWriter(socketSSL.getOutputStream()));
		Scanner sc = new Scanner(System.in);
		
		cadena = sc.nextLine();
		
		while(!cadena.equalsIgnoreCase("salir")) {
			salida.write(cadena + "\n");
			salida.flush();
			
			respuesta = entrada.readLine();
			while(respuesta == null) {}
			System.out.println(respuesta);
			cadena = sc.nextLine();
		}
		sc.close();
	}
	
	public SSLSocket obtenerSocket(String ip, int puerto) throws KeyStoreException, 
	NoSuchAlgorithmException, CertificateException, IOException, KeyManagementException {
		
		FileInputStream fileAlmacen = new FileInputStream(this.rutaAlmacen);
		KeyStore almacenClaves = KeyStore.getInstance(KeyStore.getDefaultType());
		almacenClaves.load(fileAlmacen, claveAlmacen.toCharArray());
		
		System.out.println("Cargadas claves");
		
		TrustManagerFactory fabricaGestores = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		fabricaGestores.init(almacenClaves);
		
		SSLContext contexto = SSLContext.getInstance("TLS");
		contexto.init(null, fabricaGestores.getTrustManagers(), null);
		
		SSLSocketFactory fabrica = contexto.getSocketFactory();
		socketSSL = (SSLSocket) fabrica.createSocket(ipServidor,puerto);
		System.out.println("Socket generado");
		return socketSSL;
		
	}
	
	public static void main(String[] args) {
		Cliente cliente;
		try {
			cliente = new Cliente("./almacencliente", "almacencliente", "localhost", 6666);
			cliente.conectar();
		} catch (KeyManagementException | KeyStoreException | NoSuchAlgorithmException | CertificateException
				| IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
