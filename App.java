package ex2sql;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Scanner;

public class App {

	private static Scanner clientName;
	private static Scanner console;

	public static void main(String[] args) throws Exception {
		System.out.println("שלום וברוך הבא למערכת קופת חולים מתחילים ");
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/ex2";
		String username = "root";
		String password = "7031615Nati";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url,username,password);
		Statement stmt = conn.createStatement();
		console = new Scanner(System.in);
		boolean exitprogram = false;
		while(!exitprogram) {
			System.out.println("please enter your choise:");
			System.out.println(" type 1 for see list of the patient\n type 2 for add immediately"
					+ " client to queue\n type 3 to see the longest waiting time of 10 patients\n"
					+ "type exit to finish ");
			String choise = console.next();
			System.out.println(choise);	
			if(choise.equals("1")) {
				queueList(stmt);
			}
			else if(choise.equals("2")) {
				enterClient(stmt);
			}
			else if (choise.equals("3")){
				tenTopWatingClient(stmt);
			}
			else if(choise.equals("exit")) {
				exitprogram = true;
			}
			else {
				System.out.println("worng choise!!!!");	
			}
			System.out.println();
		}
		stmt.close();
		conn.close();
	}

	private static void queueList (Statement stmt) throws SQLException{
		console = new Scanner(System.in);  
		System.out.println("Helo doctor please insert id");  
		String sqlDoctorName = console.next();
		System.out.println();		
		String sql = "SELECT  distinct ex2.queue_reserved.doctor_id,ex2.patients.pastient_name, ex2.queue_reserved.queue_time FROM ex2.queue_reserved JOIN   ex2.doctors ON  ex2.doctors.doctor_id =  ex2.doctors.doctor_id JOIN ex2.patients ON ex2.patients.pastient_id = ex2.queue_reserved.pastient_id where    ex2.queue_reserved.doctor_id ="+sqlDoctorName+" Order by  ex2.queue_reserved.queue_time"; 
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next()){
			//Retrieve by column name
			int id  = rs.getInt("doctor_id");
			String name  = rs.getString ("pastient_name");
			Timestamp date  = (new Timestamp(rs.getTimestamp ("queue_time").getTime()));		    
			//Display values
			System.out.print("doctor_id: " + id+"|");
			System.out.print(" pastient_name: " + name+"|");
			System.out.print(" queue_time: " + date+"|");
			System.out.println();
			//Clean-up environment

		}
		rs.close();
	}	
	private static void enterClient(Statement stmt) throws SQLException {
		Timestamp selectCurrnetDate = new Timestamp(System.currentTimeMillis());
		String currnetDate ="'" + selectCurrnetDate.toString() + "'";
		clientName = new Scanner(System.in);
		System.out.print("enter new client:");
		String a = clientName.next();
		String sql = "INSERT INTO  ex2.queue(queue_id , time) select count(queue_id)+1 ,"+ currnetDate + "from ex2.queue"; 	
		stmt.addBatch(sql);
		stmt.executeBatch();
		sql = "select * from ex2.queue order by queue_id desc limit 1";
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next()){
			String queue_id  = rs.getString ("queue_id");
			Timestamp date  = (new Timestamp(rs.getTimestamp ("time").getTime()));		    
			//Display values
			System.out.print("name :" +a.toString() + " | ");
			System.out.print(" id : " + queue_id+" | ");
			System.out.print(" queue_time: " + date+" | ");
			System.out.println();
			//Clean-up environment	    
		}
		rs.close();
	}
	private static void tenTopWatingClient(Statement stmt) throws SQLException {
		String sqlquery = "CREATE VIEW topWatingClient AS\r\n" + 
				"    SELECT \r\n" + 
				"        a.queue_id, time, queue_time, a.time - b.queue_time AS dis\r\n" + 
				"    FROM\r\n" + 
				"        ex2.queue AS a\r\n" + 
				"            JOIN\r\n" + 
				"        ex2.queue_reserved AS b\r\n" + 
				"    WHERE\r\n" + 
				"        a.queue_id = b.queue_id\r\n" + 
				"    ORDER BY dis DESC\r\n" + 
				"    LIMIT 10;";		
		stmt.addBatch(sqlquery);
		stmt.executeBatch();
		System.out.println("view table tenTopWatingClient created!!");		
	}


	/*
	private static void numOfpatientToday(Statement stmt) throws SQLException{
	create trigger sum_of_patint_insert
	after insert  on ex2.queue for each row  update ex2.queue_summery as t
 	set  t.pastient_num = (select count(a.doctor_id)  from  ex2.queue_summery as a
  	join ex2.queue_reserved as b where a.doctor_id = b.doctor_id);

	create trigger sum_of_patint_delete
	after delete  on ex2.queue for each row  update ex2.queue_summery as t
 	set  t.pastient_num = (select count(a.doctor_id)  from  ex2.queue_summery as a
  	join ex2.queue_reserved as b where a.doctor_id = b.doctor_id);
	}
	 */














}


