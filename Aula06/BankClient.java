import java.io.*;
import java.net.*;

public class BankClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 12345);
             BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
             
            System.out.println("Conectado ao servidor.");

            while (true) {
                System.out.println("Digite a operação no formato: <numeroConta> <operacao> <valor>");
                System.out.print("Ou digite 'sair' para encerrar: ");
                String input = console.readLine();
                if ("sair".equalsIgnoreCase(input)) {
                    break;
                }

                out.println(input);
                String response = in.readLine();
                System.out.println("Resposta do servidor: " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
