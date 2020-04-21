import java.lang.Exception;
import java.net.Socket;
import java.io.*;

public class Hilo_MyHTTP extends Thread {

	private Socket skCliente;
	private DataInputStream input=null;
	private DataOutputStream out=null;

	
	public Hilo_MyHTTP(Socket p_cliente)
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
		byte[] lista= new byte[600];
		byte tempb = 0;
		int i = 0;
		try
		{
			InputStream aux = p_sk.getInputStream();
			DataInputStream flujo = new DataInputStream( aux );
			input= flujo;
			p_Datos = new String();

			do
			{
				tempb = flujo.readByte();	
				lista[i] = tempb;
				i++;
				
			}while(flujo.available() > 0);

			String auxi=new String (lista, "UTF-8");
			String [] partes=auxi.split("/");

			String[] peque= null;

			if(partes.length>1){
				peque=partes[1].split(" ");
			}


			if(peque!=null&&peque[0].equals("favicon.ico")){

			}else{
				p_Datos= auxi;
			}

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

			FileReader read=null;
			BufferedReader read2=null;
			String petic=p_Datos;
			String html="";
			try {
				read=new FileReader(petic);
				read2=new BufferedReader(read);
				
				String linea=read2.readLine();
					while (linea!=null) {
						html=html.concat(linea);
						linea=read2.readLine();
					}
			}catch (FileNotFoundException f) {
				this.escribeSocket(skCliente, "404.html");
			}
			try {
				OutputStream aux = skCliente.getOutputStream();
				PrintWriter esc= new PrintWriter(aux);
				esc.println("HTTP/1.1 200 OK");
				esc.println("Content-Type: text/html");
				esc.println("\r\n");
				
				esc.println(html);
				esc.flush();
				
			}catch (Exception e8) {
				System.out.println("Error: " + e8.toString());
			}	
				
		}catch (Exception e7) {
			System.out.println("Error: " + e7.toString());
			
		}	
		return;
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
	
    public void run() {
		int resultado=0;
		String Cadena="";
		String Cadena2="";
		
        try {

			Cadena = this.leeSocket (skCliente, Cadena);
			System.out.println(Cadena);


			

				String[] trozos= Cadena.split("/");
				String uno=trozos[1];
				if(!trozos[0].equals("GET ")){
					this.escribeSocket(skCliente, "405.html");
					skCliente.close();
				}else{
					if(uno.equals("gatewaySD")){


						
						
						try{
							


							
							
						//	out=new DataOutputStream(skCliente.getOutputStream());
							try{String petic=trozos[2].substring(0, trozos[2].lastIndexOf(" "));
							Socket gate=new Socket("localhost", Integer.parseInt("9997"));
								OutputStream aux = gate.getOutputStream();
								DataOutputStream esc= new DataOutputStream(aux);
								esc.writeByte(2);
								for(int j=0;j<petic.length();j++){
									esc.writeBytes(Character.toString(petic.charAt(j)));
								}
								esc.writeByte(3);
								esc.writeByte(this.pedirLRC(petic));

								esc.flush();

								Cadena="";
								System.out.println(this.leeSocket(gate, Cadena).substring(2));
								
							}
							catch (Exception e)
							{
								System.out.println("Error: " + e.toString());
							}
						
						}
						catch (Exception e2)
						{
							System.out.println("Error: " + e2.toString());

						}
					}else{
						String petic=trozos[1].substring(0, trozos[1].lastIndexOf(" "));
						try {
							FileReader read=new FileReader(petic);
							escribeSocket(skCliente, petic);
						}catch (FileNotFoundException f) {
							this.escribeSocket(skCliente, "404.html");
						}

						skCliente.close();

					}	
				}
				
				
				
				
			
			



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
        catch (Exception e3) {
          System.out.println("Error: " + e3.toString());

		  try{
			  skCliente.close();
		  }catch (Exception e4) {
          	System.out.println("Error: " + e4.toString());
		  }

        }
      }
}
