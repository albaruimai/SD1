import java.lang.Exception;
import java.net.Socket;
import java.io.*;
import java.util.*;

public class Hilo_Gateway extends Thread {

	private Socket skCliente;
	
	public Hilo_Gateway(Socket p_cliente)
	{
		this.skCliente = p_cliente;
	}
	
	/*
	* Lee datos del socket. Supone que se le pasa un buffer con hueco 
	*	suficiente para los datos. Devuelve el numero de bytes leidos o
	* 0 si se cierra fichero o -1 si hay error.
	*/
	public String leeSocket (Socket p_sk, String p_Datos)
	{
        
		byte[] lista= new byte[1000];
		byte tempb2 = 0;
		int i = 0;
		try
		{
			InputStream aux = p_sk.getInputStream();
			DataInputStream flujo = new DataInputStream( aux );
			p_Datos = new String();

			do
			{
				tempb2 = flujo.readByte();	
                lista[i] = tempb2;
                i++;

			}while(flujo.available() > 0);


			p_Datos= new String (lista, "UTF-8");
			
		}
		catch (Exception e)
		{
			System.out.println("Error: " + e.toString());
		}
      return p_Datos;
	}


	/*
	* Escribe dato en el socket cliente. Devuelve numero de bytes escritos,
	* o -1 si hay error.
	*/
	
	
	public void autorizar(Socket p_sk, String p_Datos){
		boolean auto=false;
		String host="";
		String ip="";
		String proc="";
		String nombre="";
		String tarjeta="";
		String imp="";
		String cvv="";
		String cad="";
		String[] datos=p_Datos.split("&");
		for(int i=0;i<datos.length;i++){
			String[] mini=datos[i].split("=");
			if(mini[0].equals("name")){
				nombre=this.amendSentence(mini[1]);
			}
			if(mini[0].equals("card")){
				tarjeta=mini[1];
			}

			if(mini[0].equals("amount")){
				imp=mini[1];
			}

			if(mini[0].equals("cvv")){
				cvv=mini[1];
			}

			if(mini[0].equals("exp")){
			cad=mini[1];
			}
		}

		if(nombre.equals("")||tarjeta.equals("")||imp.equals("")||cvv.equals("")||nombre.equals("")){
			String respuesta="Error: URL no valida";
				System.out.println(respuesta);
			try{
				OutputStream aux = p_sk.getOutputStream();
				DataOutputStream esc= new DataOutputStream(aux);

//16 es para url no valida


				esc.writeByte(16);/*
				for(int j=0;j<respuesta.length();j++){
					esc.writeUTF(Character.toString(respuesta.charAt(j)));
				}*/
				esc.flush();
				p_sk.close();
			}
			catch (Exception e)
			{
				System.out.println("Error: " + e.toString());
			}

		}
		else{
			FileReader read=null;
			BufferedReader read2=null;
			try {
			read=new FileReader("Bines.txt");
			read2=new BufferedReader(read);
			
			
				while (proc.equals("")) {

					String linea=read2.readLine();
					String[] fila=linea.split("#");
					if(fila[0].charAt(0)==tarjeta.charAt(0)){
						proc=fila[1];
					}
				}
			}
			
			catch (Exception e5) {
				System.out.println("Error: " + e5.toString());
			}

			System.out.println(proc);

			read=null;
			read2=null;
			try {
			read=new FileReader("Procesadores.txt");
			read2=new BufferedReader(read);
			
			
				while (ip.equals("")) {

					String linea=read2.readLine();
					String[] fila=linea.split("#");
					if(fila[0].charAt(0)==proc.charAt(0)){
						host=fila[1];
						ip=fila[2];
					}
				}
			}catch (Exception e6) {
				System.out.println("Error: " + e6.toString());
			}

			System.out.println(host);
			System.out.println(ip);


			try{
				Socket proces=new Socket(host, Integer.parseInt(ip));
				String cadena=new String("auth+"+proc+"+"+imp);
				OutputStream aux = proces.getOutputStream();
				DataOutputStream esc= new DataOutputStream(aux);
				for(int j=0;j<cadena.length();j++){
					esc.writeBytes(Character.toString(cadena.charAt(j)));
				}
				esc.flush();
			}
			catch (java.net.ConnectException e7) {
				String respuesta="Error: no se ha podido conectar con el Procesador";
				System.out.println(respuesta);
				try{
					OutputStream aux = p_sk.getOutputStream();
					DataOutputStream esc= new DataOutputStream(aux);
					esc.writeByte(19);
					/*
					for(int j=0;j<respuesta.length();j++){
						esc.writeBytes(Character.toString(respuesta.charAt(j)));
					}*/
					esc.flush();
					p_sk.close();
				}
				catch (Exception e)
				{
					System.out.println("Error: " + e.toString());
				}
			}catch (Exception e2)
			{
				System.out.println("Error: " + e2.toString());
			}
		}

		


//Hasta aqui hemos separado la informacion y visto que procesador pertocaria a la tarjeta de credito
//Ahora Buscamos el host e ip del procesador en el archivo


		
		return ;

	}


	public String amendSentence(String sstr) 
    { 
        char[] str=sstr.toCharArray(); 
		String result="";
          
        // Traverse the string 
        for (int i=0; i < str.length; i++) 
        { 
            // Convert to lowercase if its 
            // an uppercase character 
            if (str[i]>='A' && str[i]<='Z') 
            { 
                  
                // Print space before it 
                // if its an uppercase character 
                if (i != 0) 
                    result=result.concat(" "); 
      
                // Print the character 
                result=result.concat(String.valueOf(str[i])); 
            } 
      
            // if lowercase character 
            // then just print 
            else
            	result=result.concat(String.valueOf(str[i])); 
        } 
		return result;
    }    


	public void estado(Socket p_sk, String p_Datos){
		String proc="";
		String host="";
		String ip="";
		String[] datos=p_Datos.split("&");

		proc=datos[0].split("=")[1].replaceAll("\u0000.*", "");
		FileReader read=null;
		BufferedReader read2=null;
		try {
		read=new FileReader("Procesadores.txt");
		read2=new BufferedReader(read);
		
		String linea=read2.readLine();
			while (ip.equals("")&&linea!=null) {

				
				String[] fila=linea.split("#");
				if(fila[0].charAt(0)==proc.charAt(0)){
					host=fila[1];
					ip=fila[2];
				}
				linea=read2.readLine();
			}
		}catch (Exception e6) {
          	System.out.println("Error: " + e6.toString());
		}


		//Si pedimos informacion de un procesador que no existe
		if(ip.equals("")){
			String respuesta="Error: no se ha podido conectar con el Procesador";
			System.out.println(respuesta);
			try{
				OutputStream aux = p_sk.getOutputStream();
				DataOutputStream esc= new DataOutputStream(aux);
				esc.writeByte(19);
				/*
				for(int j=0;j<respuesta.length();j++){
					esc.writeBytes(Character.toString(respuesta.charAt(j)));
				}*/
				esc.flush();
				p_sk.close();
			}
			catch (Exception e)
			{
				System.out.println("Error: " + e.toString());
			}
		}

		if(datos.length==1&&!ip.equals("")){
			try{
				Socket proces=new Socket(host, Integer.parseInt(ip));
				String cadena=new String("est+"+proc+"+get");
				OutputStream aux = proces.getOutputStream();
				DataOutputStream esc= new DataOutputStream(aux);
				esc.writeByte(2);
				
					esc.writeUTF(cadena);
				
				esc.writeByte(3);
				esc.writeByte(this.pedirLRC(cadena));
				esc.flush();
				
			}
			catch (Exception e)
			{
				System.out.println("Error: " + e.toString());
			}
		}



		if(datos.length==2&&!ip.equals("") ){
			String info=datos[1].split("=")[1].replaceAll("\u0000.*", "");
			try{
				Socket proces2=new Socket(host, Integer.parseInt(ip));
				String cadena2=new String("est+"+ proc +"+"+info + "+set");
				OutputStream aux = proces2.getOutputStream();
				DataOutputStream esc= new DataOutputStream(aux);
		//		esc.writeByte(2);
				for(int j=0;j<cadena2.length();j++){
					esc.writeBytes(Character.toString(cadena2.charAt(j)));
				}
		//		esc.writeByte(3);
		//		esc.writeByte(this.pedirLRC(cadena2));
				esc.flush();
				
			}
			catch (Exception e2)
			{
				System.out.println("Error: " + e2.toString());
			}
		}

	}



	public void minimo(Socket p_sk, String p_Datos){
		String proc="";
		String host="";
		String ip="";
		String[] datos=p_Datos.split("&");

		proc=datos[0].split("=")[1].replaceAll("\u0000.*", "");
		FileReader read=null;
		BufferedReader read2=null;
		try {
		read=new FileReader("Procesadores.txt");
		read2=new BufferedReader(read);
		
		String linea=read2.readLine();
			while (ip.equals("")&&linea!=null) {

				
				String[] fila=linea.split("#");
				if(fila[0].charAt(0)==proc.charAt(0)){
					host=fila[1];
					ip=fila[2];
				}
				linea=read2.readLine();
			}
		}catch (Exception e6) {
          	System.out.println("Error: " + e6.toString());
		}


		//Si pedimos informacion de un procesador que no existe
		if(ip.equals("")){
			String respuesta="Error: no se ha podido conectar con el Procesador";
			System.out.println(respuesta);
			try{
				OutputStream aux = p_sk.getOutputStream();
				DataOutputStream esc= new DataOutputStream(aux);
				esc.writeByte(19);
				/*
				for(int j=0;j<respuesta.length();j++){
					esc.writeBytes(Character.toString(respuesta.charAt(j)));
				}*/
				esc.flush();
				p_sk.close();
			}
			catch (Exception e)
			{
				System.out.println("Error: " + e.toString());
			}
		}


		//getter
		if(datos.length==1&&!ip.equals("")){
			try{
				Socket proces=new Socket(host, Integer.parseInt(ip));
				String cadena=new String("min+" + proc +"+get");
				System.out.println(cadena.length());
				OutputStream aux = proces.getOutputStream();
				DataOutputStream esc= new DataOutputStream(aux);
			//	esc.writeByte(2);
				for(int j=0;j<cadena.length();j++){
					esc.writeBytes(Character.toString(cadena.charAt(j)));
				}
		//		esc.writeByte(3);
	//			esc.writeByte(this.pedirLRC(cadena));
				esc.flush();
				
			}
			catch (Exception e)
			{
				System.out.println("Error: " + e.toString());
			}
		}


		//Setter
		if(datos.length==2&&!ip.equals("") ){
			String info=datos[1].split("=")[1].replaceAll("\u0000.*", "");
			info=info.substring(0, info.length()-2);
			try{
				Socket proces2=new Socket(host, Integer.parseInt(ip));
				String cadena2=new String("min+"+ proc +"+"+ info + "+set");
				OutputStream aux = proces2.getOutputStream();
				DataOutputStream esc= new DataOutputStream(aux);
		//		esc.writeByte(2);
				for(int j=0;j<cadena2.length();j++){
					esc.writeBytes(Character.toString(cadena2.charAt(j)));
				}
	//			esc.writeByte(3);
	//			esc.writeByte(this.pedirLRC(cadena2));
				esc.flush();
				
			}
			catch (Exception e2)
			{
				System.out.println("Error: " + e2.toString());
			}
		}
		System.out.println(host);
		System.out.println(ip);
	}



	public void maximo(Socket p_sk, String p_Datos){
		String proc="";
		String host="";
		String ip="";
		String[] datos=p_Datos.split("&");


		proc=datos[0].split("=")[1].replaceAll("\u0000.*", "");
		FileReader read=null;
		BufferedReader read2=null;
		try {
		read=new FileReader("Procesadores.txt");
		read2=new BufferedReader(read);
		
		String linea=read2.readLine();
			while (ip.equals("")&&linea!=null) {

				
				String[] fila=linea.split("#");
				if(fila[0].charAt(0)==proc.charAt(0)){
					host=fila[1];
					ip=fila[2];
				}
				linea=read2.readLine();
			}
		}catch (Exception e6) {
          	System.out.println("Error: " + e6.toString());
		}


		//Si pedimos informacion de un procesador que no existe
		if(ip.equals("")){
			String respuesta="Error: no se ha podido conectar con el Procesador";
			System.out.println(respuesta);
			try{
				OutputStream aux = p_sk.getOutputStream();
				DataOutputStream esc= new DataOutputStream(aux);
				esc.writeByte(19);
				/*
				for(int j=0;j<respuesta.length();j++){
					esc.writeBytes(Character.toString(respuesta.charAt(j)));
				}*/
				esc.flush();
				p_sk.close();
			}
			catch (Exception e)
			{
				System.out.println("Error: " + e.toString());
			}
		}


		//getter
		if(datos.length==1&&!ip.equals("")){
			
			try{
				Socket proces=new Socket(host, Integer.parseInt(ip));
				String cadena=new String("max+"+proc+"+get");
				System.out.println(cadena.length());
				OutputStream aux = proces.getOutputStream();
				DataOutputStream esc= new DataOutputStream(aux);
			//	esc.writeByte(2);
				for(int j=0;j<cadena.length();j++){
					esc.writeBytes(Character.toString(cadena.charAt(j)));
				}
		//		esc.writeByte(3);
		//		esc.writeByte(this.pedirLRC(cadena));
				esc.flush();
				
			}
			catch (Exception e)
			{
				System.out.println("Error: " + e.toString());
			}
		}


		//Setter
		if(datos.length==2&&!ip.equals("") ){
			String info=datos[1].split("=")[1].replaceAll("\u0000.*", "");
			info=info.substring(0, info.length()-2);
			try{
				Socket proces2=new Socket(host, Integer.parseInt(ip));
				String cadena2=new String("max+"+ proc +"+"+info + "+set");
				OutputStream aux = proces2.getOutputStream();
				DataOutputStream esc= new DataOutputStream(aux);
		//		esc.writeByte(2);
				for(int j=0;j<cadena2.length();j++){
					esc.writeBytes(Character.toString(cadena2.charAt(j)));
				}
		//		esc.writeByte(3);
		//		esc.writeByte(this.pedirLRC(cadena2));
				esc.flush();
				
			}
			catch (Exception e2)
			{
				System.out.println("Error: " + e2.toString());
			}
		}
	}

//Para pasarle la pagina que imprima todos los datos de los procesadores

	public void index(Socket p_sk){
		ArrayList<Character> procs= new ArrayList<Character>();
		try{
			FileReader read=new FileReader("Procesadores.txt");
			BufferedReader read2=new BufferedReader(read);
			int i=0;
			
			String linea=read2.readLine();
				while (linea!=null) {
					String[] fila=linea.split("#");
					procs.add(fila[0].charAt(0));
					i++;
					linea=read2.readLine();
				}
		}
		catch(Exception e){
			System.out.println("Error: " + e.toString());

		}

		for(int j=0;j<procs.size();j++){

		}


	}


	public int pedirLRC(String petic){
		byte [] binaryValue = petic.getBytes();
		byte lrc = 0x00;

		for(byte b : binaryValue) {

			lrc ^= b;
		}

		// lrc msut be between 48 to 95
		lrc %= 48; 
		lrc += 48;
		return lrc;
	}


	public boolean validarLRC(String petic, char val){
		boolean ok=false;
		byte [] binaryValue = petic.getBytes();
		byte lrc = 0x00;

		for(byte b : binaryValue) {

			lrc ^= b;
		}

		// lrc msut be between 48 to 95
		lrc %= 48; 
		lrc += 48;

		byte val2=(byte) val;
		if(lrc==val2){
			ok=true;
		}


		return ok;
	}

	
    public void run() {
		int resultado=0;
		String Cadena="";
		boolean todo=false;
		
        try {

			Cadena = this.leeSocket (skCliente, Cadena);
			Cadena=Cadena.replaceAll("\u0000.*", "");
			String pet="";
			System.out.println(Cadena);
			if(Cadena.length()>3){
				
				pet=Cadena.substring(1);
				char lrc=pet.charAt(pet.length()-1);
				pet=pet.substring(0,pet.length()-2);

				todo=this.validarLRC(pet,lrc);

			}
			
			if(todo){

/*
								try{
									OutputStream aux = skCliente.getOutputStream();
									DataOutputStream esc= new DataOutputStream(aux);
									esc.writeByte(6);
									esc.flush();
								}
								catch (Exception e4) {
									System.out.println("Error: " + e4.toString());
								}

*/
			String partes[]=pet.split("\\?");


			String tipo=partes[0];



			if(tipo.equals("auth")){
				this.autorizar(skCliente, partes[1]);
			}else{
				if(tipo.equals("status")){
					this.estado(skCliente, partes[1]);
				}else{
					if(tipo.equals("fl")){
						this.minimo(skCliente, partes[1]);
					}
					else{
						if(tipo.equals("ul")){
							this.maximo(skCliente, partes[1]);
						}
						else{
							if(tipo.equals("index")){
								this.index(skCliente);
							}else{

								String respuesta="Error: URL no valida";
								System.out.println(respuesta);
								try{
									OutputStream aux = skCliente.getOutputStream();
									DataOutputStream esc= new DataOutputStream(aux);
									esc.writeByte(16);
								/*	for(int j=0;j<respuesta.length();j++){
										esc.writeBytes(Character.toString(respuesta.charAt(j)));
									}*/
								//	esc.writeUTF(respuesta);
									esc.flush();
								}
								catch (Exception e3) {
									System.out.println("Error: " + e3.toString());
								}

							}

						}
					}
				}
			}

			}else{
				try{
					String respuesta="Se ha perdido informacion";
					OutputStream aux2 = skCliente.getOutputStream();
					DataOutputStream esc2= new DataOutputStream(aux2);
					esc2.writeByte(21);
				//	esc2.writeUTF("Se ha perdido informacion");
				
					esc2.flush();
				}
				catch (Exception e3) {
					System.out.println("Error: " + e3.toString());
				}
			}			
        }
        catch (Exception e) {
          System.out.println("Error: " + e.toString());

		  try{
			  skCliente.close();
		  }catch (Exception e2) {
          	System.out.println("Error: " + e2.toString());
		  }

        }
      }
}
