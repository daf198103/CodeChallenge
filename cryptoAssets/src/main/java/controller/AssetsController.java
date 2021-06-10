package controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import service.AssetsService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping(value = "/api.coincap.io")
public class AssetsController {
    

    @Autowired
    private AssetsService assetService;


//    @GetMapping(value="/v2/assets")
//    public List<String> getAssets() {
//        List<String> assets = new ArrayList<>();
//        return assets;
//    }

    public void MyGETRequest() throws IOException {
        URL urlForGetRequest = new URL("https://api.coincap.io/v2/assets");
        String readLine = null;
        HttpURLConnection conection = (HttpURLConnection) urlForGetRequest.openConnection();
        conection.setRequestMethod("GET");
        int responseCode = conection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conection.getInputStream()));
            //StringBuffer response = new StringBuffer();
            List<String> response = new ArrayList<>();
            while ((readLine = in.readLine()) != null) {
                response.add(readLine);
            }
            in.close();
            assetService.getAssetsFromApi(response);
            // print result
            System.out.println("JSON String Result " + response.toString());
            //GetAndPost.POSTRequest(response.toString());
        } else {
            System.out.println("GET NOT WORKED");
        }
    }
    
}
