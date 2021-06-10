package service;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

@Service
@Slf4j
public class AssetsService{


    public static void hello(){
        log.info("Hello World");
    }

    List<String> assets = new ArrayList<>();
    List<String[]> csvAssets = new ArrayList<>();
    Map<String, BigDecimal> priceCsv = new HashMap<>();
    Map<String, BigDecimal> priceApi = new HashMap<>();
    BigDecimal totalAssetsUSD = BigDecimal.valueOf(0);
    Map<String,BigDecimal> comparedUsdValue = new HashMap<>();
    Map<String,BigDecimal> betterEvolution = new HashMap<>();
    Map<String,BigDecimal> worstEvolution = new HashMap<>();

    public void receiveDataCsv() throws IOException {
        Reader reader = Files.newBufferedReader(Paths.get("src/main/resources/test.csv"));
        CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();

        csvAssets = csvReader.readAll();
        for (String[] x : csvAssets) {
            BigDecimal qtty =  new BigDecimal(x[2]);
            BigDecimal valueUsd = new BigDecimal(x[3]);
            totalAssetsUSD = totalAssetsUSD.add(qtty.multiply(valueUsd));
        }


    }

    public static Response doGetRequest(String asset) {
        RestAssured.defaultParser = Parser.JSON;
        return given().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON).
                        when().get("https://api.coincap.io/v2/assets/"+
                                                asset+"/history?interval=d1&start=1617753600000&end=1617753601000").
                        then().contentType(ContentType.JSON).extract().response();
    }

    public List<String> getAssetsFromApi(List<String> assetsList){
        assets.addAll(assetsList);
       return assetsList;
    }

    public void AssetsList() {

        List<String> name = new ArrayList<>();
        List<String> assetsList = new ArrayList<>();
        List<String[]> sli = new ArrayList<>();
        List<String[]> sli2 = new ArrayList<>();
        List<String> valuesApi = new ArrayList<>();
        List<BigDecimal> csvPriceToCalculate = new ArrayList<>();
        List<BigDecimal> apiPriceToCalculate = new ArrayList<>();
        List<BigDecimal> variation = new ArrayList<>();


        for(String[] x : csvAssets){
            name.add(x[0]);
        }
            for(String x : name){
                Response response = doGetRequest(x);
                List<LinkedHashMap<?,?>> list =  response.jsonPath().getList("data");
                    for(LinkedHashMap<?,?> ls : list){
                        assetsList.add(ls.toString());
                    }

            }
            for(String x : assetsList){
                sli.add(x.split(","));
            }
            for(String[] x : sli){
                sli2.add(x[0].split("="));
            }
            for(String[] z : sli2){
                valuesApi.add(z[1]);
            }
            for (int i = 0; i < name.size(); i++) {
                priceCsv.put(csvAssets.get(i)[1], new BigDecimal(csvAssets.get(i)[3]).setScale(2, RoundingMode.CEILING));
            }

            for (int i = 0; i < name.size(); i++) {
                priceApi.put(csvAssets.get(i)[1], new BigDecimal(valuesApi.get(i)).setScale(2, RoundingMode.CEILING));
            }


        calculateVariation(csvPriceToCalculate, apiPriceToCalculate, variation);

        System.out.println("\n\n");
        System.out.println("-----------------Results---------------");
        System.out.println("\n\n");
        System.out.println("total = USD$ "+ totalAssetsUSD.setScale(2, RoundingMode.CEILING)+
                ", best_asset = " + betterEvolution.keySet().toString().replaceAll("[\\[\\],]","") +
                ", best_performance = " + betterEvolution.values().toString().replaceAll("[\\[\\],]","") +" percent, " +
                ", worst_asset = " + worstEvolution.keySet().toString().replaceAll("[\\[\\],]","") +
                ", worst_performance = " + worstEvolution.values().toString().replaceAll("[\\[\\],]","") +" percent");
        System.out.println("\n\n");
        System.out.println("---------------------------------------");
        System.out.println("\n\n");
        }

    public void calculateVariation(List<BigDecimal> csvPriceToCalculate, List<BigDecimal> apiPriceToCalculate, List<BigDecimal> variation) {
        for(BigDecimal value: priceCsv.values()) {
            csvPriceToCalculate.add(value);
        }
        for(BigDecimal value: priceApi.values()) {
            apiPriceToCalculate.add(value);
        }
        for(int i = 0; i < csvAssets.size(); i++){
            BigDecimal res1 = csvPriceToCalculate.get(i).multiply(new BigDecimal(100.0))
                    .divide(apiPriceToCalculate.get(i), 2, RoundingMode.HALF_UP);
            BigDecimal res2 = new BigDecimal(100.0);
            BigDecimal var = res2.subtract(res1);
            comparedUsdValue.put(csvAssets.get(i)[1],var);
        }
        BigDecimal max = Collections.max(comparedUsdValue.values());
        BigDecimal min = Collections.min(comparedUsdValue.values());
        for (Map.Entry<String, BigDecimal> entry : comparedUsdValue.entrySet()) {
            if (entry.getValue()== max) {
                betterEvolution.put(entry.getKey(),max);
            }
        }
        for (Map.Entry<String, BigDecimal> entry : comparedUsdValue.entrySet()) {
            if (entry.getValue()== min) {
                worstEvolution.put(entry.getKey(),min);
            }
        }


    }


}
