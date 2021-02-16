package bdd;

import captors.Captor;
import captors.CaptorListener;
import captors.CaptorSocket;
import captors.TypeMesure;

import java.sql.*;
import java.util.LinkedList;
import java.util.TreeSet;



public class BaseDonnes implements CaptorListener{
	Connection connexion;
	java.sql.Timestamp sqlTime;
	int ret;
	public BaseDonnes() {
		try {
		connexion = DriverManager.getConnection(
		"jdbc:mysql://localhost/bdd?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
		"root",
		"");
		}
		catch(SQLException e) {
		e.printStackTrace();
		}
		
	}
	
	
	public TreeSet<Captor> recupListeCapteursComplete(){
		TreeSet<Captor> liste = new TreeSet<>();
		ResultSet rst;
		Statement stmt;
		try {
			stmt = connexion.createStatement();
			rst = stmt.executeQuery("SELECT * FROM capteur");
			while(rst.next()){
				liste.add(createCaptorFromRst(rst));
			}
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return liste;
	}
	
	
	public LinkedList<Valeur> vueCapteur(Captor c, Timestamp debut, Timestamp fin){
		LinkedList<Valeur> valeurs = new LinkedList<>();
		ResultSet rst;
		PreparedStatement ps;
		try {
			ps=connexion.prepareStatement("SELECT * "
					+ "FROM mesure WHERE "
					+ "(temps BETWEEN ? AND ?) AND "
					+ "(idC=?)");
			ps.setTimestamp(1,debut);
			ps.setTimestamp(2,fin);
			ps.setString(3, c.getNom());
			
			rst = ps.executeQuery();
			
			System.out.println(ps.toString());

			while(rst.next()){
				valeurs.add(new Valeur(rst.getFloat("valeur"),rst.getTimestamp("temps")));
			}
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return valeurs;
	}
	
	
	public void captorValueChanged(CaptorSocket c) {
		PreparedStatement ps;
		try {
			java.util.Date date=new java.util.Date();
			sqlTime=new java.sql.Timestamp(date.getTime());
			ps=connexion.prepareStatement("INSERT INTO mesure (valeur,idC,temps) values(?,?,?)");
			ps.setFloat(1,c.getValue());
			ps.setString(2, c.getNom());
			ps.setTimestamp(3,sqlTime);
			ps.executeUpdate();			
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void captorConnected(CaptorSocket c) {
		ResultSet rst;
		Statement stmt;
		PreparedStatement ps;
		try {
			stmt = connexion.createStatement();
			rst = stmt.executeQuery("SELECT COUNT(*) FROM capteur WHERE nomC='"+c.getNom()+"'");
			rst.next();
			if(rst.getInt(1) == 0) {
				ps=connexion.prepareStatement("INSERT INTO capteur(nomC,batiment,etage,lieu,nomTC,minBase,maxBase)"+
				" VALUES (?,?,?,?,?,?,?)");
				ps.setString(1,c.getNom());
				ps.setString(2, c.getBatiment());
				ps.setShort(3, (short) c.getEtage());
				ps.setString(4, c.getLieu());
				ps.setString(5, String.valueOf(c.getType()));
				ps.setFloat(6, c.getMin());
				ps.setFloat(7, c.getMax());
				ps.executeUpdate();			
				ps.close();
			}
			stmt.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void writeMinMax(Captor c) {
		PreparedStatement ps;
		try {
			ps=connexion.prepareStatement("UPDATE capteur"
					+ " SET minBase=?,"
					+ " maxBase=?"
					+ " WHERE nomC=?");
			ps.setFloat(1, c.getMin());
			ps.setFloat(2, c.getMax());
			ps.setString(3,c.getNom());
			ps.executeUpdate();
			ps.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private Captor createCaptorFromRst(ResultSet rst) {
		TypeMesure type;
		try {
			switch(rst.getString("nomTC")) {
			case "EAU":
				type = TypeMesure.WATER;
				break;
			case "ELECTRICITE":
				type = TypeMesure.ELECTRICITY;
				break;
			case "TEMPERATURE":
				type = TypeMesure.TEMPERATURE;
				break;
			default:
				type = TypeMesure.PRESSURIZEDAIR;
				break;
			}
			return new Captor(rst.getString("nomC"), rst.getString("batiment"),rst.getString("lieu"), rst.getShort("etage"), type, rst.getFloat("minBase"), rst.getFloat("maxBase"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Captor("Erreur SQL", "" ,"" , 0, TypeMesure.WATER, 0, 0);
		}
		
	}

}
