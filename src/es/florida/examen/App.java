package es.florida.examen;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class App {

	public static class XML {

		private String id, url, user, password;

		public XML(String id, String url, String user, String password) {
			this.id = id;
			this.url = url;
			this.user = user;
			this.password = password;
		}

		public String getUrl() {
			return url;
		}

		public String getUser() {
			return user;
		}

		public String getPassword() {
			return password;
		}

		public String getId() {
			return id;
		}

	}

	public static class ListaXML {
		private List<XML> lista = new ArrayList<XML>();

		public ListaXML() {
		}

		public void anyadirXML(XML xml) {
			lista.add(xml);
		}

		public List<XML> getListaXML() {
			return lista;
		}
	}

	// Ejercicio_001
	public static void crearXML() {
		ListaXML lista = new ListaXML();
//		XML xml;
		int idUltimo = 0;

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("¿Añadir nuevo registro (s/n)?");

		try {
			String respuesta = br.readLine();

			while (respuesta.equals("s")) {
				System.out.println("url: ");
				String url = br.readLine();
				System.out.println("password: ");
				String password = br.readLine();
				System.out.println("user: ");
				String user = br.readLine();

				idUltimo++;

				XML xml = new XML(String.valueOf(idUltimo), url, user, password);
				lista.anyadirXML(xml);

				System.out.println("¿Añadir nuevo registro (s/n)? ");
				respuesta = br.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document document = dBuilder.newDocument();

			Element raiz = document.createElement("ficheroXML");
			document.appendChild(raiz);

			for (XML x : lista.getListaXML()) {
				Element element = document.createElement("registro");

				String id = String.valueOf(x.getId());
				element.setAttribute("id", id);
				raiz.appendChild(element);

				Element url = document.createElement("url");
				url.appendChild(document.createTextNode(String.valueOf(x.getUrl())));
				element.appendChild(url);

				Element user = document.createElement("user");
				user.appendChild(document.createTextNode(String.valueOf(x.getUser())));
				element.appendChild(user);

				Element password = document.createElement("password");
				password.appendChild(document.createTextNode(String.valueOf(x.getPassword())));
				element.appendChild(password);
			}

			// Guardar documento en disco
			// Crear serializador
			TransformerFactory tF = TransformerFactory.newInstance();
			Transformer transformer = tF.newTransformer();

			// Darle formato al documento
			transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			DOMSource source = new DOMSource(document);

			try {
				// escribimos el resultado en el fichero
				FileWriter fw = new FileWriter("config.xml");
				StreamResult result = new StreamResult(fw);
				transformer.transform(source, result);
				fw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Número de nodos: " + idUltimo);
	}

	// ejercicio 02

	public static void leerXML() {

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document document = dBuilder.parse(new File("config.xml"));

			Element raiz = document.getDocumentElement();
			System.out.println("Contenido XML: " + raiz.getNodeName() + ":");
			NodeList nodeList = document.getElementsByTagName("registro");

			for (int i = 0; i < nodeList.getLength(); i++) {

				Node node = nodeList.item(i);
				System.out.println("");

				Element eElement = (Element) node;

				String id = eElement.getAttribute("id");
				System.out.println("ID registro: " + id);

				String url = eElement.getElementsByTagName("url").item(0).getTextContent();
				System.out.println("url: " + url);

				String user = eElement.getElementsByTagName("user").item(0).getTextContent();
				System.out.println("user: " + user);

				String password = eElement.getElementsByTagName("password").item(0).getTextContent();
				System.out.println("password: " + password);

				// conectarse a la base de datos
				Class.forName("com.mysql.cj.jdbc.Driver");

				try {
					Connection con = DriverManager.getConnection(url, user, password);
					System.out.println("Conexión correcta!");

					// ejercicio 003
					Scanner teclado = new Scanner(System.in);
					Statement stmt = con.createStatement();
					ResultSet rs = stmt.executeQuery("SELECT id, titulo FROM saludables");
					System.out.println("");

					ResultSetMetaData rsmd = rs.getMetaData();
					int numCampos = rsmd.getColumnCount();

					while (rs.next()) {
						for (int j = 1; j <= numCampos; j++) {
							System.out.print(rs.getString(j) + " // ");
						}
						System.out.println("");
					}

					String primerId = null;
					String segundoId = null;

					// ejercicio 04
					System.out.println("Introduce el primer id");
					String id1 = teclado.nextLine();
					Statement stmt2 = con.createStatement();
					ResultSet rs2 = stmt.executeQuery("SELECT id, calorias FROM saludables WHERE id =" + id1);
					ResultSetMetaData rsmd2 = rs2.getMetaData();
					int numCampos2 = rsmd2.getColumnCount();
					while (rs2.next()) {
						for (int j = 1; j <= numCampos2; j++) {
							System.out.print(rs2.getString(j) + " // ");

						}
						for (int j = 0; j < numCampos2; j++) {
							if (numCampos2 == 2) {
								primerId = rs2.getString(numCampos2);
							}

						}
						System.out.println("");
					}

					System.out.println("Introduce el segundo id");
					String id2 = teclado.nextLine();
					Statement stmt3 = con.createStatement();
					ResultSet rs3 = stmt.executeQuery("SELECT id, calorias FROM saludables WHERE id =" + id2);
					ResultSetMetaData rsmd3 = rs3.getMetaData();
					int numCampos3 = rsmd2.getColumnCount();
					while (rs3.next()) {
						for (int j = 1; j <= numCampos3; j++) {
							System.out.print(rs3.getString(j) + " // ");
						}
						for (int j = 1; j <= numCampos3; j++) {
							if (numCampos3 == 2) {
								segundoId = rs3.getString(numCampos3);
							}
						}
						System.out.println("");
					}

					int id1ToInt = Integer.parseInt(primerId);
					int id2ToInt = Integer.parseInt(segundoId);

					float valorMedioCalorias = id1ToInt + id2ToInt / 2;
					System.out.println("La media de calorias es de: " + valorMedioCalorias);

					rs3.close();
					rs2.close();
					rs.close();
					con.close();
				} catch (SQLException e) {
					System.err.println("Error en la conexión");
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ejercicio 05
	public static void insertarEnBD() {
		Scanner teclado = new Scanner(System.in);
		String sentencia = "";
		String editar = "";

		System.out.println("¿Quieres insertar un registro? (s/n)");
		editar = teclado.nextLine();

		while (editar.equals("s")) {
			System.out.println("Introduce el id:");
			String id = teclado.nextLine();
			System.out.println("Introduce el titulo:");
			String titulo = teclado.nextLine();
			System.out.println("Introduce las calorias:");
			String calorias = teclado.nextLine();

			try {

				Class.forName("com.mysql.cj.jdbc.Driver");
				Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/comidas", "root", "");
				PreparedStatement ps = con
						.prepareStatement("INSERT INTO saludables (id, titulo, calorias) VALUES (?,?,?)");
				ps.setString(1, id);
				ps.setString(2, titulo);
				ps.setString(3, calorias);
				int resultadoInsertar = ps.executeUpdate();
				if (resultadoInsertar > 0) {
					System.out.println("Comida guardada en base de datos");
				} else {
					System.out.println("No se ha podido insertar la nueva fila");
				}

				System.out.println("¿Quieres insertar un registro? (s/n)");
				editar = teclado.nextLine();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// ejercicio 06
	public static void calorias() throws ClassNotFoundException {
		Scanner teclado = new Scanner(System.in);
		System.out.println("Introduce un número de calorias");
		String calorias = teclado.nextLine();
		Class.forName("com.mysql.cj.jdbc.Driver");

		try {
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/comidas", "root", "");
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT titulo, calorias FROM saludables where calorias>=" + calorias);
			ResultSetMetaData rsmd = rs.getMetaData();
			int numCampos = rsmd.getColumnCount();

			while (rs.next()) {
				for (int j = 1; j <= numCampos; j++) {
					System.out.print(rs.getString(j) + " // ");
				}
				System.out.println("");
			}

			rs.close();
			con.close();
		} catch (SQLException e) {
			System.err.println("Error en la conexión");
			e.printStackTrace();
		}
	}

	// ejercicio 07

	public static void crearFichero() throws ClassNotFoundException, IOException {
		Scanner teclado = new Scanner(System.in);
		Class.forName("com.mysql.cj.jdbc.Driver");
		String nombrefichero = null;
		String calorias = null;
		try {
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/comidas", "root", "");
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM saludables");
			ResultSetMetaData rsmd = rs.getMetaData();
			int numCampos = rsmd.getColumnCount();

			while (rs.next()) {
				for (int j = 1; j <= numCampos; j++) {

					nombrefichero = rs.getString(2);
					calorias = rs.getString(3);
				}
				System.out.println("Nombre fichero: " + nombrefichero);
				FileWriter fw = new FileWriter(nombrefichero);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(calorias);
				bw.close();
				fw.close();
				System.out.println("");
			}

			rs.close();
			con.close();
		} catch (SQLException e) {
			System.err.println("Error en la conexión");
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws ClassNotFoundException, IOException {

		Scanner teclado = new Scanner(System.in);
		System.out.println("Crear xml (s/n)");
		String opcion = teclado.nextLine();
		if (opcion.equals("s")) {
			crearXML();
		}
		leerXML();
		insertarEnBD();
		calorias();
		crearFichero();

		teclado.close();
	}

}
