package net.kyaz0x1.tokenchecker;

import net.kyaz0x1.tokenchecker.files.FileUtils;
import net.kyaz0x1.tokenchecker.files.type.FileExtensionType;
import net.kyaz0x1.tokenchecker.manager.TokenManager;

import java.io.File;
import java.util.List;
import java.util.Scanner;

public class TokenChecker {

    public static void main(String[] args){
        if(args.length != 1){
            System.out.println("Argumentos inválidos! Use: java -jar TokenChecker.jar <file>");
            return;
        }

        final File file = new File(args[0]);

        if(!file.exists()){
            System.out.println("O arquivo informado não existe! Certifique-se de que o local foi informado corretamente.");
            return;
        }

        if(!FileUtils.hasExtension(file, FileExtensionType.TEXT_FILE)){
            System.out.println("O arquivo informado não é um arquivo de texto!");
            return;
        }

        final TokenManager tokenManager = TokenManager.getInstance();
        final List<String> tokens = tokenManager.loadTokens(file);

        if(tokens.isEmpty()){
            System.out.println("Não existe nenhum token para checar!");
            return;
        }

        System.out.println("Tokens: " + tokens.size());

        for(String token : tokens){
            final boolean result = tokenManager.check(token);
            final String tokensChecked = String.format("(%d/%d)",
                    tokenManager.getTokensChecked().get(),
                    tokens.size()
            );

            System.out.printf("%s %s %s\n", tokensChecked, result ? "[+]" : "[-]", token);
        }

        System.out.printf(">> Tokens: %d | Works: %d | Deads: %d\n",
                tokens.size(),
                tokenManager.getTokensWork().size(),
                tokenManager.getTokensDead().size()
        );

        System.out.println("Deseja salvar os tokens? Digite \"y\" para sim ou \"n\" para não.");

        try(Scanner in = new Scanner(System.in)){
            final String option = in.nextLine();
            switch(option){
                case "y":
                    tokenManager.saveTokens();
                    break;
                case "n":
                    System.out.println("Os tokens não serão salvados! Fechando programa...");
                    break;
                default:
                    System.out.println("Por padrão, os tokens não serão salvados. Fechando programa...");
            }
        }
    }

}