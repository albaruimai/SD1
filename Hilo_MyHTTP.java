import java.lang.Exception;
import java.net.Socket;
import java.io.*;

public class Hilo_MyHTTP extends Thread {

	private Socket skCliente;
	
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
			p_Datos = new String();

			boolean primera=false;
			boolean segunda=false;
			do
			{
				tempb = flujo.readByte();				
				if(tempb=='/'&&primera==true){
					segunda=true;
				}
				if(primera==true && segunda==false){
					lista[i] = tempb;
					i++;
				}

				if(tempb=='/'&&primera==false){
					primera=true;
				}
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
			PrintWriter esc= new PrintWriter(aux);
			esc.println("HTTP/1.1 200 OK");
			esc.println("Content-Type: text/html");
			esc.println("\r\n");
			String firstWords = p_Datos.substring(0, p_Datos.lastIndexOf(" "));
			esc.println("<html><body><h1>Error 404</h1></body></html>");
			esc.println(firstWords);
			esc.flush();
		}
		catch (Exception e)
		{
			System.out.println("Error: " + e.toString());
		}
		return;
	}
	
	
	
    public void run() {
		int resultado=0;
		String Cadena="";
		
        try {

			Cadena = this.leeSocket (skCliente, Cadena);
			System.out.println(Cadena);
			this.escribeSocket(skCliente, Cadena);
			/*
			* Se escribe en pantalla la informacion que se ha recibido del
			* cliente
			

			Cadena = "Operacion realizada con exito";
			this.escribeSocket (skCliente, Cadena);						
		*/
			skCliente.close();
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
