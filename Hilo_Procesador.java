import java.lang.Exception;
import java.net.Socket;
import java.io.*;

public class Hilo_Procesador extends Thread {

	private Socket skCliente;
	private DataInputStream input=null;
	private DataOutputStream out=null;

	
	public Hilo_Procesador(Socket p_cliente)
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
	
	
/*
	public int pedirLRC(String petic){
		int LRC=0;
		for(int i=0;i<petic.length();i++){
			if(i==0){
				LRC=petic.charAt(i);
			}else{
				LRC=LRC^petic.charAt(i);
			}
		}
		System.out.println("LRC="+LRC);
		return LRC;
	}
	
	*/
    public void run() {
		int resultado=0;
		String Cadena="";
		
        try {

			Cadena = this.leeSocket (skCliente, Cadena);
			System.out.println(Cadena);
			
			String partes[]=Cadena.split("+");


			String tipo=partes[0];

            String resto=partes[1];

            for(int i=2;i<partes.length();i++){
                resto=resto.concat("+");
                resto=resto.concat(partes[i]);
            }



			if(tipo.equals("est")){
				this.estado(skCliente, resto);
			}else{
				if(tipo.equals("auth")){
					this.auto(skCliente, resto);
				}else{
					if(tipo.equals("min")){
						this.minimo(skCliente, resto);
					}
					else{
						if(tipo.equals("max")){
							this.maximo(skCliente, resto);
						}
						else{
							if(tipo.equals("index")){
								this.index(skCliente, resto);
							}

						}
					}
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
