package ca.polymtl.inf4402.tp1.client;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import ca.polymtl.inf4402.tp1.shared.ServerInterface;

public class Client {
	public static void main(String[] args) {
		String distantHostname = null;

		if (args.length > 0) {
			distantHostname = args[0];
		}

		Client client = new Client(distantHostname);
		client.run();
	}

	FakeServer localServer = null; // Pour tester la latence d'un appel de
									// fonction normal.
	private ServerInterface localServerStub = null;
	private ServerInterface distantServerStub = null;

	public Client(String distantServerHostname) {
		super();

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		localServer = new FakeServer();
		localServerStub = loadServerStub("127.0.0.1");

		if (distantServerHostname != null) {
			distantServerStub = loadServerStub(distantServerHostname);
		}
	}

	private void run() {
		int iterations = 20;
		appelNormal();
		System.out.println("arguement 10 exposant i;temps moyen appel normal de "+iterations+" iterations");
		for (int i = 1; i <= 11; i++) {
			long moyenne = mesureTempsAppelNormal((int) Math.pow(10, i), iterations);
			System.out.println(i+";"+moyenne);
		}

		if (localServerStub != null) {
			appelRMILocal();
			System.out.println("arguement 10 exposant i;temps moyen appelRMILocal de "+iterations+" iterations");
			for (int i = 1; i <= 8; i++) {
				long moyenne = mesureTempsAppelRMILocal((int) Math.pow(10, i), iterations);
				System.out.println(i+";"+moyenne);
			}
		}

		if (distantServerStub != null) {
			appelRMIDistant();
			System.out.println("arguement 10 exposant i;temps moyen appelRMIDistant de "+iterations+" iterations");
			for (int i = 1; i <= 8; i++) {
				long moyenne = mesureTempsAppelRMIDistant((int) Math.pow(10, i), iterations);
				System.out.println(i+";"+moyenne);
			}
		}
	}

	private ServerInterface loadServerStub(String hostname) {
		ServerInterface stub = null;

		try {
			Registry registry = LocateRegistry.getRegistry(hostname);
			stub = (ServerInterface) registry.lookup("server");
		} catch (NotBoundException e) {
			System.out.println("Erreur: Le nom '" + e.getMessage()
					+ "' n'est pas défini dans le registre.");
		} catch (AccessException e) {
			System.out.println("Erreur: " + e.getMessage());
		} catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
		}

		return stub;
	}

	private void appelNormal() {
		byte[] b = new byte[10];
		for (int i = 0; i < b.length; i++) {
            b[i] = (byte)(0xA2);
		}
		System.out.println((byte)(0xA2));
		long start = System.nanoTime();
		localServer.execute(b);
		long end = System.nanoTime();

		System.out.println("Temps écoulé appel normal: " + (end - start)
				+ " ns");
	}
	
	private long mesureTempsAppelNormal(int nbOctects, int iterations) {
		byte[] b = new byte[nbOctects];
		// for (int i = 0; i < b.length; i++) {
		//     b[i] = (byte)(0xA2);
		// }
		long moyenneAppelNormal = 0;
		for (int i = 0; i < iterations; i++) {
			long start = System.nanoTime();
			localServer.execute(b);
			long end = System.nanoTime();
			long tps = end - start;
			moyenneAppelNormal += tps; 
		}
		return moyenneAppelNormal / iterations;
	}

	private void appelRMILocal() {
		try {
			long start = System.nanoTime();
			int result = localServerStub.execute((int)(10e8), (int)(10e1));
			long end = System.nanoTime();

			System.out.println("Temps écoulé appel RMI local: " + (end - start)
					+ " ns");
			System.out.println("Résultat appel RMI local: " + result);
		} catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
		}
	}
	
	private long mesureTempsAppelRMILocal(int nbOctects, int iterations) {
		byte[] b = new byte[nbOctects];
		// for (int i = 0; i < b.length; i++) {
		//     b[i] = (byte)(0xA2);
		// }
		long moyenneAppelNormal = 0;
		for (int i = 0; i < iterations; i++) {
			long start = System.nanoTime();
			try {
				localServerStub.execute(b);
			} catch (RemoteException e) { System.out.println("Erreur: " + e.getMessage()); }
			long end = System.nanoTime();
			long tps = end - start;
			moyenneAppelNormal += tps; 
		}
		return moyenneAppelNormal / iterations;
	}

	private void appelRMIDistant() {
		try {
			long start = System.nanoTime();
			int result = distantServerStub.execute((int)(10e8), (int)(10e1));
			long end = System.nanoTime();
			System.out.println((int)(8*10e6));

			System.out.println("Temps écoulé appel RMI distant: "
					+ (end - start) + " ns");
			System.out.println("Résultat appel RMI distant: " + result);
		} catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
		}
	}
	
	private long mesureTempsAppelRMIDistant(int nbOctects, int iterations) {
		byte[] b = new byte[nbOctects];
		// for (int i = 0; i < b.length; i++) {
        //     b[i] = (byte)(0xA2);
		// }
		long moyenneAppelNormal = 0;
		for (int i = 0; i < iterations; i++) {
			long start = System.nanoTime();
			try {
				distantServerStub.execute(b);
			} catch (RemoteException e) { System.out.println("Erreur: " + e.getMessage()); }
			long end = System.nanoTime();
			long tps = end - start;
			moyenneAppelNormal += tps; 
		}
		return moyenneAppelNormal / iterations;
	}
	
}
