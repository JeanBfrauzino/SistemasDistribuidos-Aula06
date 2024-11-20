import java.io.*;
import java.net.*;
import java.util.*;

public class BankServer {
    private static final String ACCOUNTS_FILE = "accounts.txt";

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Servidor iniciado. Aguardando conexões...");
            
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                     
                    System.out.println("Cliente conectado.");
                    
                    String request = in.readLine();
                    String response = processRequest(request);
                    out.println(response);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String processRequest(String request) {
        String[] parts = request.split(" ");
        if (parts.length != 3) {
            return "Erro: Solicitação inválida. Formato esperado: <numeroConta> <operacao> <valor>";
        }

        String accountNumber = parts[0];
        String operation = parts[1].toLowerCase();
        double amount;

        try {
            amount = Double.parseDouble(parts[2]);
        } catch (NumberFormatException e) {
            return "Erro: Valor inválido.";
        }

        Map<String, Double> accounts = loadAccounts();
        if (!accounts.containsKey(accountNumber)) {
            return "Erro: Conta não encontrada.";
        }

        double currentBalance = accounts.get(accountNumber);
        if ("saque".equals(operation)) {
            if (currentBalance < amount) {
                return "Erro: Saldo insuficiente.";
            }
            currentBalance -= amount;
        } else if ("deposito".equals(operation)) {
            currentBalance += amount;
        } else {
            return "Erro: Operação inválida. Use 'saque' ou 'deposito'.";
        }

        accounts.put(accountNumber, currentBalance);
        saveAccounts(accounts);

        return String.format("Operação concluída. Saldo atualizado: %.2f", currentBalance);
    }

    private static Map<String, Double> loadAccounts() {
        Map<String, Double> accounts = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ACCOUNTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                accounts.put(parts[0], Double.parseDouble(parts[1].replace(",", "."))); // Substituição da vírgula por ponto
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return accounts;
    }

    private static void saveAccounts(Map<String, Double> accounts) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ACCOUNTS_FILE))) {
            for (Map.Entry<String, Double> entry : accounts.entrySet()) {
                writer.printf("%s %.2f%n", entry.getKey(), entry.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
