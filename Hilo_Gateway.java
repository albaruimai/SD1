import java.lang.Exception;
import java.net.Socket;
import java.io.*;
import java.util.Arrays;

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
        
		byte[] lista= new byte[9999];
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
	public void escribeSocket (Socket p_sk, String p_Datos)
	{
		try
		{

			OutputStream aux = p_sk.getOutputStream();
			PrintWriter esc= new PrintWriter(aux);/*
			esc.println("HTTP/1.1 200 OK");
			esc.println("Content-Type: text/html");
			esc.println("\r\n");
			esc.println("<html><body><h1>HOLAAAAA</h1></body></html>");*/
            System.out.println("HOLAAAAA");
			esc.flush();
		}
		catch (Exception e)
		{
			System.out.println("Error: " + e.toString());
		}
		return;
	}
	
	public boolean autorizar(Socket p_sk, String p_Datos){
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
			cad=mini[1].substring(0,4);
			}
		}


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
		}catch (Exception e5) {
          	System.out.println("Error: " + e5.toString());
		}

		System.out.println(proc);


//Hasta aqui hemos separado la informacion y visto que procesador pertocaria a la tarjeta de credito
//Ahora Buscamos el host e ip del procesador en el archivo


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

		return auto;

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
	
    public void run() {
		int resultado=0;
		String Cadena="";
		
		
        try {

			Cadena = this.leeSocket (skCliente, Cadena);
			System.out.println(Cadena);

			String partes[]=Cadena.split("\\?");

			System.out.println(partes[0]);

			String tipo=partes[0].substring(3);

			System.out.println(tipo);


			if(tipo.equals("auth")){
				boolean auto=this.autorizar(skCliente, partes[1]);
			}

			this.escribeSocket(skCliente, Cadena);
			/*
			* Se escribe en pantalla la informacion que se ha recibido del
			* cliente
			

			Cadena = "Operacion realizada con exito";
			this.escribeSocket (skCliente, Cadena);						
		*/	
			
			//skCliente.close();
			
			//System.exit(0); No se debe poner esta sentencia, porque en ese caso el primer cliente que cierra rompe el socket 
			//				  y desconecta a todos				
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
