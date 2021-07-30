package net.kyaz0x1.tokenchecker.manager;

import net.kyaz0x1.tokenchecker.api.DiscordAPI;
import net.kyaz0x1.tokenchecker.files.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public final class TokenManager {

    private static TokenManager INSTANCE;

    private final ExecutorService TOKEN_CHECKER;
    private final ExecutorService TOKEN_WRITER;

    private final DiscordAPI api;

    private List<String> tokens;
    private List<String> tokensWork;
    private List<String> tokensDead;

    private final AtomicInteger tokensChecked;

    private TokenManager(){
        this.api = DiscordAPI.getInstance();

        this.tokens = new ArrayList<>();
        this.tokensWork = new ArrayList<>();
        this.tokensDead = new ArrayList<>();

        this.tokensChecked = new AtomicInteger();

        this.TOKEN_CHECKER = Executors.newFixedThreadPool(10);
        this.TOKEN_WRITER = Executors.newSingleThreadExecutor();
    }

    public List<String> loadTokens(File file){
        try {
            final List<String> lines = Files.readAllLines(Paths.get(file.toURI()));

            for(String line : lines){
                if(!isToken(line))
                    continue;
                tokens.add(line);
            }

            return tokens;
        }catch(IOException e){
            e.printStackTrace();
            return tokens;
        }
    }

    public boolean check(String token){
        try {
            final boolean result = TOKEN_CHECKER.submit(() -> api.isValidAccount(token)).get();
            tokensChecked.incrementAndGet();
            if(result){
                tokensWork.add(token);
            }else{
                tokensDead.add(token);
            }
            return result;
        }catch(ExecutionException | InterruptedException e) {
            tokensDead.add(token);
            return false;
        }
    }

    public void saveTokens(){
        TOKEN_WRITER.submit(() -> {
            final File fileWorks = new File("tokens-works.txt");
            final File fileDeads = new File("tokens-deads.txt");

            try{
                if(!fileWorks.exists()){
                    System.out.println("Criando arquivo para salvar os tokens válidos...");
                    fileWorks.createNewFile();
                }
                if(!fileDeads.exists()){
                    System.out.println("Criando arquivo para salvar os tokens inválidos...");
                    fileDeads.createNewFile();
                }
            }catch(IOException e) {
                e.printStackTrace();
            }

            FileUtils.write(tokensWork.stream().collect(Collectors.joining("\n")), fileWorks);

            System.out.println("Todas os tokens válidos foram salvados com sucesso!");

            FileUtils.write(tokensDead.stream().collect(Collectors.joining("\n")), fileDeads);

            System.out.println("Todas os tokens inválidos foram salvados com sucesso!");
            System.exit(0);
        });
    }

    private boolean isToken(String value){
       return value.matches("[a-zA-Z0-9]{24}\\.[a-zA-Z0-9]{6}\\.[a-zA-Z0-9_\\-]{27}|mfa\\.[a-zA-Z0-9_\\-]{84}");
    }

    public List<String> getTokensWork() {
        return tokensWork;
    }

    public List<String> getTokensDead() {
        return tokensDead;
    }

    public AtomicInteger getTokensChecked() {
        return tokensChecked;
    }

    public static TokenManager getInstance(){
        if(INSTANCE == null){
            synchronized(TokenManager.class){
                if(INSTANCE == null){
                    INSTANCE = new TokenManager();
                }
            }
        }
        return INSTANCE;
    }

}