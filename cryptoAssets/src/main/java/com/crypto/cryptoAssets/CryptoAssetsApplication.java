package com.crypto.cryptoAssets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import service.AssetsService;

@SpringBootApplication
public class CryptoAssetsApplication implements CommandLineRunner {


	private AssetsService assetsService = new AssetsService();


	public static void main(String[] args) {

		SpringApplication.run(CryptoAssetsApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		assetsService.receiveDataCsv();
		assetsService.AssetsList();

	}
}
