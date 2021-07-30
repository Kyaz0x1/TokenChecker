package net.kyaz0x1.tokenchecker.api;

import net.kyaz0x1.tokenchecker.api.http.HttpResponseCode;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public final class DiscordAPI {

    public static DiscordAPI INSTANCE;

    private final OkHttpClient client;

    private DiscordAPI(){
        this.client = new OkHttpClient();
    }

    public boolean isValidAccount(String token){
        final String url = "https://discord.com/api/v9/users/@me";
        final Request request = new Request.Builder()
                .url(url)
                .header("Authorization", token)
                .build();
        try {
            final Response response = client.newCall(request).execute();
            return response.code() == HttpResponseCode.OK.getCode();
        }catch(IOException e) {
            return false;
        }
    }

    public static DiscordAPI getInstance(){
        if(INSTANCE == null){
            synchronized(DiscordAPI.class){
                if(INSTANCE == null){
                    INSTANCE = new DiscordAPI();
                }
            }
        }
        return INSTANCE;
    }

}