import java.net.*;

public class MyHTTP{

	/**
	 * @param args
	 */
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*
		* Descriptores de socket servidor y de socket con el cliente
		*/
		String puerto="";

		try
		{
			
			if (args.length < 3) {
				System.out.println("Debe indicar el puerto de escucha del servidor, ip de la pasarela y puerto de la pasarela");
				System.out.println("$./Servidor puerto_servidor");
				System.exit (1);
			}
			puerto = args[0];
			String ip=args[1];
			String p_ip=args[2];
			ServerSocket skServidor = new ServerSocket(Integer.parseInt(puerto));
		    System.out.println("Escucho el puerto " + puerto);
	
			/*
			* Mantenemos la comunicacion con el cliente
			*/	
			for(;;)
			{
				/*
				* Se espera un cliente que quiera conectarse
				*/
				Socket skCliente = skServidor.accept(); // Crea objeto
		        System.out.println("Sirviendo cliente...");

		        Thread t = new Hilo_MyHTTP(skCliente, ip, p_ip);
		        t.start();
			}
		}
		catch(Exception e)
		{
			System.out.println("Error: " + e.toString());
			
		}
	}

}
